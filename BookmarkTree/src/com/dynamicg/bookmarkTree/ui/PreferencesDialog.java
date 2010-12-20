package com.dynamicg.bookmarkTree.ui;

import java.util.ArrayList;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.dynamicg.bookmarkTree.BookmarkTreeContext;
import com.dynamicg.bookmarkTree.PreferencesWrapper;
import com.dynamicg.bookmarkTree.R;
import com.dynamicg.bookmarkTree.data.BrowserBookmarkLoader;
import com.dynamicg.bookmarkTree.data.writer.AlphaSortWriter;
import com.dynamicg.bookmarkTree.data.writer.SeparatorChangedBookmarkWriter;
import com.dynamicg.bookmarkTree.model.BrowserBookmarkBean;
import com.dynamicg.bookmarkTree.util.CommonDialogHelper;
import com.dynamicg.bookmarkTree.util.DialogButtonPanelWrapper;
import com.dynamicg.bookmarkTree.util.SimpleProgressDialog;
import com.dynamicg.common.ui.SimpleAlertDialog;

public class PreferencesDialog extends Dialog {

	private static int actionCounter=-1;
    public static final int ACTION_DUMP_BOOKMARKS = ++actionCounter;
    public static final int ACTION_SHOW_DISCLAIMER = ++actionCounter;
    
	private final BookmarkTreeContext ctx;
	private final String currentSeparator;
	private final PreferencesWrapper prefsWrapper;
	private final SpinnerUtil spinnerUtil;

	private EditText separatorItem;
	private CheckBox doFullUpdateCheckbox;
	private CheckBox showDeleteIconCheckbox;
	private CheckBox optimiseLayout;
	private CheckBox keepStateCheckbox;

	private boolean dataRefreshRequired;

	public PreferencesDialog(BookmarkTreeContext ctx) {
		super(ctx.activity);
		this.ctx = ctx;
		this.prefsWrapper = ctx.preferencesWrapper;
		this.spinnerUtil = new SpinnerUtil(this);
		
		CommonDialogHelper.expandContent(this, R.layout.prefs_body);

		currentSeparator = ctx.getFolderSeparator();
		this.show();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setTitle("Preferences");

		separatorItem = (EditText)findViewById(R.id.prefsSeparator);
		separatorItem.setText(currentSeparator);
		separatorItem.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
				checkForChangedSeparator();
			}
		});

		doFullUpdateCheckbox = (CheckBox)findViewById(R.id.prefsFullUpdateOnChange);
		checkForChangedSeparator(); // inactivate intially
		
		Button sortAlpha = (Button)findViewById(R.id.prefsActionSortAlpha);
		sortAlpha.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new SimpleAlertDialog(ctx.activity, "Sort browser bookmarks?", "OK", "Cancel") {
					@Override
					public void onPositiveButton() {
						new SimpleProgressDialog(ctx.activity, "Please wait...") {
							@Override
							public void backgroundWork() {
								new AlphaSortWriter(ctx);
							}
							@Override
							public void done() {
								ctx.reloadAndRefresh(); // needs to be done by main thread
								Toast.makeText(getContext(), "Bookmark sort done", Toast.LENGTH_SHORT).show();
							}
						};
					}
				};
			}
		});
		
		showDeleteIconCheckbox = (CheckBox)findViewById(R.id.prefsDeletionActive);
		showDeleteIconCheckbox.setChecked(prefsWrapper.isShowDeleteIcon());

		optimiseLayout = (CheckBox)findViewById(R.id.prefsOptimiseLayout);
		optimiseLayout.setChecked(prefsWrapper.isOptimisedLayout());

		keepStateCheckbox = (CheckBox)findViewById(R.id.prefsKeepState);
		keepStateCheckbox.setChecked(prefsWrapper.isKeepState());
		
		new DialogButtonPanelWrapper(this) {
			@Override
			public void onPositiveButton() {
				saveClicked();
			}
		};

		// attach spinners
		spinnerUtil.bind ( R.id.prefsListStyle, prefsWrapper.prefsBean.getListStyle(), SpinnerUtil.getListStyleItems() );
		spinnerUtil.bind ( R.id.prefsSortOption, prefsWrapper.prefsBean.getSortOption(), SpinnerUtil.getSortOptionItems() );
		
	}
	
	private void checkForChangedSeparator() {
		String newValue = separatorItem.getText().toString();
		boolean hasChanges = newValue!=null && !newValue.equals(currentSeparator);
		doFullUpdateCheckbox.setEnabled(hasChanges);
		doFullUpdateCheckbox.setTextColor(hasChanges?Color.WHITE:Color.LTGRAY);
	}
	
	private void saveClicked() {
		
		boolean massUpdateTitles = this.doFullUpdateCheckbox.isChecked();
		if (!massUpdateTitles) {
			saveMain();
			savePostprocessing();
		}
		else {
			new SimpleProgressDialog(ctx.activity, "Please wait...") {
				@Override
				public void backgroundWork() {
					saveMain();
				}
				@Override
				public void done() {
					savePostprocessing();
				}
			};
		}
			
	}
	
	private void saveMain() {
		processSeparatorUpdate();
		prefsWrapper.setShowDeleteIcon(showDeleteIconCheckbox.isChecked());
		prefsWrapper.setOptimisedLayout(optimiseLayout.isChecked());
		
		prefsWrapper.prefsBean.setListStyle(spinnerUtil.getCurrentValue(R.id.prefsListStyle));
		prefsWrapper.prefsBean.setSortOption(spinnerUtil.getCurrentValue(R.id.prefsSortOption));
		prefsWrapper.prefsBean.setKeepState(keepStateCheckbox.isChecked()?1:0);
		
		// see if "refresh" is required
		if ( spinnerUtil.isChanged(R.id.prefsListStyle)
				|| spinnerUtil.isChanged(R.id.prefsSortOption)
				) {
			dataRefreshRequired = true;
		}
		
		prefsWrapper.write();
	}

	private void savePostprocessing() {
		if (dataRefreshRequired) {
			ctx.reloadAndRefresh(); // needs to be done by main thread
		}
		dismiss(); // close dialog
	}
	
	private void processSeparatorUpdate() {

		String newSeparator = separatorItem.getText().toString();
		if (newSeparator==null || newSeparator.trim().length()==0 || newSeparator.equals(currentSeparator) ) {
			// no changes, cancel
			return;
		}

		prefsWrapper.setNewSeparator(newSeparator);

		if (doFullUpdateCheckbox.isChecked()) {
			new SeparatorChangedBookmarkWriter(ctx, currentSeparator, newSeparator);
		}

		dataRefreshRequired = true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, ACTION_SHOW_DISCLAIMER, 0, "Show disclaimer");
		menu.add(0, ACTION_DUMP_BOOKMARKS, 0, "Show internal bookmarks");
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		int id = item.getItemId();
		if (id==ACTION_DUMP_BOOKMARKS) {
			dumpBrowserBookmarks();
		}
		else if (id==ACTION_SHOW_DISCLAIMER) {
			DisclaimerPopup.show(ctx);
		}
		return true;
	}

	private void dumpBrowserBookmarks() {
		ctx.reloadAndRefresh(); // so that the gui is really in sync with what we display here
		final ArrayList<BrowserBookmarkBean> rows = BrowserBookmarkLoader.loadBrowserBookmarks(ctx.activity);
		
		final StringBuffer sb = new StringBuffer();
		for (BrowserBookmarkBean row:rows) {
			sb.append(row.getFullTitle() + "\n" );
		}
		
		new SimpleAlertDialog(ctx.activity, "Browser Bookmarks", "Close") {
			public View getBody() {
				TextView text = new TextView(ctx.activity);
				text.setText(sb.toString());

				HorizontalScrollView xscroll = new HorizontalScrollView(ctx.activity);
				xscroll.addView(text);
				
				ScrollView yscroll = new ScrollView(ctx.activity);
				yscroll.addView(xscroll);
				yscroll.setPadding(10, 0, 10, 0);
				
				return yscroll;
			}
		};
	}
	
}
