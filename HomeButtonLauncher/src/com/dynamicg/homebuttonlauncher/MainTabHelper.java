package com.dynamicg.homebuttonlauncher;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;
import android.widget.TextView;

import com.dynamicg.common.Logger;
import com.dynamicg.homebuttonlauncher.preferences.PreferencesManager;

public class MainTabHelper {

	private static final Logger log = new Logger(MainTabHelper.class);

	private final Context context;
	private final MainActivityHome activity;
	private final PreferencesManager preferences;

	private int numTabs;
	private TabSpec[] tabs;
	private View[] tabviews;

	public MainTabHelper(MainActivityHome activity, PreferencesManager preferences) {
		this.activity = activity;
		this.context = activity;
		this.preferences = preferences;
		this.numTabs = preferences.prefSettings.getNumTabs();
	}

	public void bindTabs() {
		final int selectedIndex = preferences.getTabIndex();
		final LayoutInflater inflater = activity.getLayoutInflater();
		final TabHost tabhost = (TabHost)inflater.inflate(R.layout.tabs_container, null);
		tabhost.setup();
		createTabs(tabhost);
		tabhost.setCurrentTab(selectedIndex);

		tabhost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabId) {
				int index = Integer.parseInt(tabId);
				activity.updateOnTabSwitch(index);
			};
		});

		View.OnLongClickListener longClickListener = new OnLongClickListenerWrapper() {
			@Override
			public boolean onLongClickImpl(View v) {
				int index = (Integer)v.getTag();
				editLabel(index);
				return true;
			}
		};

		int tabHeight = (int)context.getResources().getDimension(R.dimen.tabHeight);
		for (int i=0;i<numTabs;i++) {
			View tab = tabviews[i];
			tab.setTag(i);
			tab.setLongClickable(true);
			tab.setOnLongClickListener(longClickListener);
			// layout options is same as time recording, see com.dynamicg.timerecording.util.ui.TabHostUtil.prepareTabs(Dialog, int, int[], int[])
			tab.getLayoutParams().height = tabHeight;
			tab.setPadding(0, tab.getPaddingTop(), 0, tab.getPaddingBottom());
		}

		// attach after header
		View header = activity.findViewById(R.id.headerContainer);
		ViewGroup main = (ViewGroup)header.getParent();
		main.addView(tabhost, main.indexOfChild(header)+1);
	}

	private void createTabs(TabHost tabhost) {
		final TabHost.TabContentFactory factory = new TabHost.TabContentFactory() {
			@Override
			public View createTabContent(String tag) {
				return new View(context);
			}
		};

		this.tabs = new TabSpec[numTabs];
		this.tabviews = new View[numTabs];

		TabWidget tabWidget = tabhost.getTabWidget();
		if (log.isDebugEnabled) {
			log.debug("tabWidget", tabWidget.getChildCount(), tabWidget.getChildAt(0));
		}

		for (int i=0;i<numTabs;i++) {
			TabSpec spec = tabhost.newTabSpec(Integer.toString(i));
			spec.setIndicator(preferences.getTabTitle(i));
			spec.setContent(factory);
			tabhost.addTab(spec);
			tabs[i] = spec;
			tabviews[i] = tabWidget.getChildAt(i); // up to 4.2 we have one child view (LinerarLayout) per tab
		}
	}

	protected void editLabel(final int index) {
		final String currentLabel = preferences.getTabTitle(index);

		final EditText editor = new EditText(context);
		editor.setText(currentLabel);
		editor.setSingleLine();
		if (currentLabel.length()>0) {
			editor.setSelection(currentLabel.length());
		}
		editor.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		DialogInterface.OnClickListener okListener = new OnClickListenerDialogWrapper(context) {
			@Override
			public void onClickImpl(DialogInterface dialog, int which) {
				String newLabel = editor.getText().toString();
				newLabel = newLabel!=null?newLabel:"";
				setLabel(index, newLabel);
				preferences.writeTabTitle(index, newLabel);
			}
		};
		builder.setPositiveButton(R.string.buttonOk, okListener );
		builder.setNegativeButton(R.string.buttonCancel, null);
		builder.setView(editor);
		AlertDialog dialog = builder.show();

		// auto open keyboard
		dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
	}

	private void setLabel(int index, String label) {
		log.debug("set label", label, index);
		//tabs[index].setIndicator(label); // does not work
		TextView title = (TextView)tabviews[index].findViewById(android.R.id.title);
		if (title!=null) {
			title.setText(label);
		}
	}

	public void redraw() {
		View tabContainer = activity.findViewById(android.R.id.tabhost);
		if (tabContainer!=null) {
			((ViewGroup)tabContainer.getParent()).removeView(tabContainer);
		}
		bindTabs();
	}

}
