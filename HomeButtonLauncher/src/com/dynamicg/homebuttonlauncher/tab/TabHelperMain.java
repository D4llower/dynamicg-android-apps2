package com.dynamicg.homebuttonlauncher.tab;

import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TextView;

import com.dynamicg.common.Logger;
import com.dynamicg.homebuttonlauncher.MainActivityHome;
import com.dynamicg.homebuttonlauncher.OnLongClickListenerWrapper;
import com.dynamicg.homebuttonlauncher.R;
import com.dynamicg.homebuttonlauncher.dialog.SpinnerHelper;
import com.dynamicg.homebuttonlauncher.preferences.HomeLauncherBackupAgent;
import com.dynamicg.homebuttonlauncher.preferences.PreferencesManager;
import com.dynamicg.homebuttonlauncher.tools.DialogHelper;
import com.dynamicg.homebuttonlauncher.tools.DialogHelper.TextEditorListener;

public class TabHelperMain extends TabHelper {

	private static final Logger log = new Logger(TabHelperMain.class);

	private final PreferencesManager preferences;

	public TabHelperMain(MainActivityHome activity, PreferencesManager preferences) {
		super(activity
				, preferences.prefSettings.getNumTabs()
				, activity.findViewById(R.id.headerContainer)
				, preferences.prefSettings.isTabAtBottom()
				);
		this.preferences = preferences;
	}

	@Override
	public TabHost bindTabs() {
		final int selectedIndex = preferences.getTabIndex();

		TabHost.OnTabChangeListener onTabChangeListener = new TabHost.OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabId) {
				int tabindex = Integer.parseInt(tabId);
				if (preferences.getTabIndex()!=tabindex) {
					activity.updateOnTabSwitch(tabindex);
				}
			};
		};

		View.OnLongClickListener longClickListener = new OnLongClickListenerWrapper() {
			@Override
			public boolean onLongClickImpl(View v) {
				int index = (Integer)v.getTag();
				editLabel(index);
				return true;
			}
		};

		String[] labels = new String[numTabs];
		for (int i=0;i<numTabs;i++) {
			labels[i] = preferences.getTabTitle(i);
		}

		TabHost tabhost = bindTabs(selectedIndex, labels, onTabChangeListener, longClickListener);
		return tabhost;
	}

	protected void editLabel(final int tabindex) {

		/*
		 * switch tab position
		 */
		SpinnerHelper.SpinnerEntries items = new SpinnerHelper.SpinnerEntries();
		items.add(-1, "");
		for (int i=0;i<preferences.prefSettings.getNumTabs();i++) {
			if (i!=tabindex) {
				items.addPadded(i, i+1);
			}
		}

		ViewGroup moveTabPanel = (ViewGroup)activity.getLayoutInflater().inflate(R.layout.common_spinner_panel, null);
		final SpinnerHelper switchTabSpinner = new SpinnerHelper(moveTabPanel.findViewById(R.id.spinnerPanelSpinner));
		switchTabSpinner.bind(items, 0);

		// "move" label and padding
		((TextView)moveTabPanel.findViewById(R.id.spinnerPabelLabel)).setText(R.string.moveTab);
		int padding = (int)context.getResources().getDimension(R.dimen.appLinePadding);
		moveTabPanel.setPadding(padding, padding, padding, padding);

		/*
		 * label editor
		 */
		final String currentLabel = preferences.getTabTitle(tabindex);
		TextEditorListener callback = new DialogHelper.TextEditorListener() {
			@Override
			public void onTextChanged(String text) {
				setLabel(tabindex, text);
				preferences.writeTabTitle(tabindex, text);

				int newTabIndex = switchTabSpinner.getSelectedValue();
				if (newTabIndex>=0) {
					try {
						preferences.exchangeTabData(tabindex, newTabIndex);
						activity.forceTabSwitch(newTabIndex);
						HomeLauncherBackupAgent.requestBackup(context);
					}
					catch (Throwable t) {
						DialogHelper.showCrashReport(context, t);
					}
				}
			}
		};

		DialogHelper.openLabelEditor(context, currentLabel, InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS, callback, moveTabPanel);
	}

	private void setLabel(int index, String label) {
		log.debug("set label", label, index);
		//tabs[index].setIndicator(label); // does not work
		TextView title = (TextView)tabviews[index].findViewById(android.R.id.title);
		if (title!=null) {
			title.setText(label);
		}
	}

	public TabHost redraw() {
		View tabContainer = activity.findViewById(android.R.id.tabhost);
		if (tabContainer!=null) {
			((ViewGroup)tabContainer.getParent()).removeView(tabContainer);
		}

		if (numTabs==0) {
			return null;
		}

		return bindTabs();
	}

}
