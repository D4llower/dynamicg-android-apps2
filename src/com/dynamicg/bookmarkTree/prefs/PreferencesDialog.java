package com.dynamicg.bookmarkTree.prefs;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.dynamicg.bookmarkTree.BookmarkTreeContext;
import com.dynamicg.bookmarkTree.R;
import com.dynamicg.bookmarkTree.chrome.ChromeWrapper;
import com.dynamicg.bookmarkTree.data.writehandler.SeparatorChangedHandler;
import com.dynamicg.bookmarkTree.dialogs.AboutDialog;
import com.dynamicg.bookmarkTree.dialogs.ColorPickerDialog;
import com.dynamicg.bookmarkTree.dialogs.ColorPickerDialog.ColorSelectedListener;
import com.dynamicg.bookmarkTree.prefs.SpinnerUtil.KeyValue;
import com.dynamicg.bookmarkTree.util.DialogButtonPanelWrapper;
import com.dynamicg.bookmarkTree.util.SimpleProgressDialog;
import com.dynamicg.common.LayoutUtil;
import com.dynamicg.common.SystemUtil;

@SuppressLint("HandlerLeak")
public class PreferencesDialog extends Dialog {

	private final BookmarkTreeContext ctx;
	private final Context context;
	private final String currentSeparator;
	private final SpinnerUtil spinnerUtil;

	private final float dialogWidth;
	private final float tabHeight;
	private final boolean kitkat = ChromeWrapper.isKitKat();

	private EditText separatorItem;
	private CheckBox doFullUpdateCheckbox;

	private boolean dataRefreshRequired;

	private HashMap<PrefEntryInt, Integer> prefToViewMap = new HashMap<PrefEntryInt, Integer>();

	public PreferencesDialog(BookmarkTreeContext ctx) {
		super(ctx.activity);
		this.ctx = ctx;
		this.context = getContext();
		this.spinnerUtil = new SpinnerUtil(this);

		PrefEntryInt.resetUpdatedValue();
		currentSeparator = ctx.getFolderSeparator(BookmarkTreeContext.SP_LEGACY);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		Resources resources = context.getResources();
		this.dialogWidth = resources.getDimension(R.dimen.prefsDialogWidth);
		this.tabHeight = resources.getDimension(R.dimen.prefsTabHeight);

		this.show();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		if (kitkat) {
			setContentView(R.layout.prefs_kk);
		}
		else {
			setContentView(R.layout.prefs_tab_control);
			prepareTabs();
			setupSeparatorItem();
			doFullUpdateCheckbox = (CheckBox)findViewById(R.id.prefsFullUpdateOnChange);
			checkForChangedSeparator(); // inactivate intially
		}

		// bind checkboxes
		bindCheckbox(R.id.prefsSortCaseInsensitive, PreferencesWrapper.sortCaseInsensitive);

		// bind spinners
		bindSpinner ( R.id.prefsListStyle, PreferencesWrapper.listStyle, SpinnerUtil.getListStyleItems(context), R.string.prefsListStyle );
		bindSpinner ( R.id.prefsSortOption, PreferencesWrapper.sortOption, SpinnerUtil.getSortOptionItems(context), R.string.prefsSortLabel );
		bindSpinner ( R.id.prefsIconScaling, PreferencesWrapper.iconScaling, SpinnerUtil.getIconScalingItems(context), R.string.prefsIconScaling );

		// color items
		bindColorPicker(R.id.prefsColorFolder, PreferencesWrapper.colorFolder, R.string.prefsColorFolder);
		bindColorPicker(R.id.prefsColorBookmarkTitle, PreferencesWrapper.colorBookmarkTitle, R.string.prefsColorBookmarkTitle);
		bindColorPicker(R.id.prefsColorBookmarkUrl, PreferencesWrapper.colorBookmarkUrl, R.string.prefsColorBookmarkUrl);

		// save/cancel panel
		new DialogButtonPanelWrapper(this, DialogButtonPanelWrapper.TYPE_SAVE_CANCEL) {
			@Override
			public void onPositiveButton() {
				saveClicked();
			}
		};

		if (!kitkat) {
			setLinks();
			// see http://devstream.stefanklumpp.com/2010/07/android-display-dialogs-in-fullscreen.html
			getWindow().setLayout( (int)this.dialogWidth, LayoutParams.FILL_PARENT);
		}
	}

	private void setLinks() {

		/*
		 * APP LINK
		 */
		{
			TextView linkNode = (TextView)findViewById(R.id.prefsLinkMarket);
			linkNode.setOnClickListener(MarketLinkHelper.getMarketAppLink(context));
			LayoutUtil.indentedFocusable(linkNode, "\u2192 ", context.getString(R.string.prefsLinkToMarket));
		}

		/*
		 * DONATION LINK
		 */
		{
			TextView donationNode = (TextView)findViewById(R.id.prefsLinkDonationApp);
			donationNode.setOnClickListener(MarketLinkHelper.getDonationLink(context));
			LayoutUtil.indentedFocusable(donationNode, "\u2192 ", context.getString(R.string.prefsLinkDonationApp));
		}

		/*
		 * ABOUT
		 */
		{
			TextView aboutNode = (TextView)findViewById(R.id.prefsLinkAbout);
			aboutNode.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					AboutDialog.show(ctx);
				}
			});
			LayoutUtil.indentedFocusable(aboutNode, "\u2192 ", context.getString(R.string.commonAbout));
		}

	}

	private void setupSeparatorItem() {
		separatorItem = (EditText)findViewById(R.id.prefsSeparator);
		separatorItem.setText(currentSeparator);
		separatorItem.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
				checkForChangedSeparator();
			}
		});
	}

	private void prepareTabs() {
		TabHost tabs = (TabHost) this.findViewById(R.id.prefsTabHost);
		tabs.setup();

		int[] layouts = {R.id.prefsTabContent1, R.id.prefsTabContent2};
		int[] titles = {R.string.prefsGroupPresentation, R.string.prefsGroupToolsAndSetup};

		TabSpec tspec;
		View child;
		for ( int i=0;i<layouts.length;i++) {
			tspec = tabs.newTabSpec("tab"+i);
			tspec.setContent(layouts[i]);
			tspec.setIndicator ( bold(context.getString(titles[i])) );
			tabs.addTab(tspec);
			// smaller tab height
			child = tabs.getTabWidget().getChildAt(i);
			child.getLayoutParams().height = (int)this.tabHeight;
			child.setPadding(0,child.getPaddingTop(),0,child.getPaddingBottom());
		}

	}

	private static SpannableString bold(String text) {
		SpannableString str = new SpannableString(text);
		str.setSpan(new StyleSpan(Typeface.BOLD), 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return str;
	}

	private void bindSpinner(int spinnerResId, PrefEntryInt prefEntry, ArrayList<KeyValue> items, int prompt) {
		prefToViewMap.put(prefEntry, spinnerResId);
		spinnerUtil.bind(spinnerResId, prefEntry, items, prompt);
	}

	private CheckBox bindCheckbox(int id, PrefEntryInt prefEntry) {
		prefToViewMap.put(prefEntry, id);
		CheckBox box = (CheckBox)findViewById(id);
		box.setChecked(prefEntry.isOn());
		return box;
	}

	private void bindColorPicker(int id, final PrefEntryInt prefEntry, int titleRes) {
		prefToViewMap.put(prefEntry, id);

		TextView link = (TextView)findViewById(id);
		//LayoutUtil.underline(link);
		LayoutUtil.indentedFocusable(link, "\u2022 ", context.getString(titleRes) );

		final ColorSelectedListener colorSelectedListener = new ColorSelectedListener() {
			@Override
			public void colorSelected(int selectedColor) {
				prefEntry.updatedValue = selectedColor;
				dataRefreshRequired = true;
			}
		};
		link.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new ColorPickerDialog(context, prefEntry.updatedValue, colorSelectedListener, false);
			}
		});
	}

	private void checkForChangedSeparator() {
		String newValue = separatorItem.getText().toString();
		boolean hasChanges = newValue!=null && !newValue.equals(currentSeparator);
		doFullUpdateCheckbox.setEnabled(hasChanges);
		doFullUpdateCheckbox.setTextColor(hasChanges?Color.WHITE:Color.LTGRAY);
	}

	private void saveClicked() {

		final Handler pleaseReloadToastHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				SystemUtil.toastShort(context, context.getString(R.string.hintRestartApp));
			}
		};

		boolean massUpdateTitles = kitkat ? false : this.doFullUpdateCheckbox.isChecked();
		if (!massUpdateTitles) {
			saveMain(pleaseReloadToastHandler);
			savePostprocessing();
		}
		else {
			new SimpleProgressDialog(ctx.activity, R.string.commonPleaseWait) {
				@Override
				public void backgroundWork() {
					saveMain(pleaseReloadToastHandler);
				}
				@Override
				public void done() {
					savePostprocessing();
				}
				@Override
				public String getErrorTitle(Throwable exception) {
					return "Separator update failed";
				}

			};
		}

	}

	private void saveMain(Handler pleaseReloadToastHandler) {

		boolean toastForReopen =
				PreferencesWrapper.listStyle.value != spinnerUtil.getCurrentValue(R.id.prefsListStyle)
				|| PreferencesWrapper.iconScaling.value != spinnerUtil.getCurrentValue(R.id.prefsIconScaling)
				;

		processSeparatorUpdate();

		for (PrefEntryInt entry:prefToViewMap.keySet()) {
			push(entry, prefToViewMap.get(entry));
		}

		// see if "refresh" is required
		if ( spinnerUtil.isChanged(R.id.prefsListStyle)
				|| spinnerUtil.isChanged(R.id.prefsSortOption)
				)
		{
			dataRefreshRequired = true;
		}

		PreferencesUpdater.writeAll();

		// fix for "java.lang.RuntimeException: Can't create handler inside thread that has not called Looper.prepare()"
		// with "changed separator" this will be called within a thread so we have to route through a message handler
		if (toastForReopen) {
			pleaseReloadToastHandler.sendEmptyMessage(0);
		}
	}

	private void push(PrefEntryInt item, int id) {
		View view = findViewById(id);
		if (view instanceof Spinner) {
			item.setNewValue(spinnerUtil.getCurrentValue(id));
		}
		else if (view instanceof CheckBox) {
			item.setNewValue ( ((CheckBox)view).isChecked() ? 1 : 0 );
		}
	}

	private void savePostprocessing() {
		if (dataRefreshRequired) {
			ctx.reloadAndRefresh(); // needs to be done by main thread
		}
		dismiss(); // close dialog
	}

	private void processSeparatorUpdate() {

		if (kitkat) {
			return;
		}

		String newSeparator = separatorItem.getText().toString();
		if (newSeparator==null || newSeparator.trim().length()==0 || newSeparator.equals(currentSeparator) ) {
			// no changes, cancel
			return;
		}

		PreferencesUpdater.setNewSeparator(newSeparator);

		if (doFullUpdateCheckbox.isChecked()) {
			new SeparatorChangedHandler(ctx, currentSeparator, newSeparator);
		}

		dataRefreshRequired = true;
	}

}
