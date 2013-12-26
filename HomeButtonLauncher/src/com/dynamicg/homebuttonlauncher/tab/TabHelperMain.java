package com.dynamicg.homebuttonlauncher.tab;

import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TabHost;
import android.widget.TextView;

import com.dynamicg.homebuttonlauncher.MainActivityHome;
import com.dynamicg.homebuttonlauncher.OnLongClickListenerWrapper;
import com.dynamicg.homebuttonlauncher.R;
import com.dynamicg.homebuttonlauncher.dialog.SpinnerHelper;
import com.dynamicg.homebuttonlauncher.preferences.HomeLauncherBackupAgent;
import com.dynamicg.homebuttonlauncher.preferences.PreferencesManager;
import com.dynamicg.homebuttonlauncher.tools.DialogHelper;
import com.dynamicg.homebuttonlauncher.tools.DialogHelper.TextEditorListener;

public class TabHelperMain extends TabHelper {

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

		int padding = (int)context.getResources().getDimension(R.dimen.gridViewPaddingTop);

		ViewGroup moveTabPanel = (ViewGroup)activity.getLayoutInflater().inflate(R.layout.common_spinner_panel, null);
		final SpinnerHelper switchTabSpinner = new SpinnerHelper(moveTabPanel.findViewById(R.id.spinnerPanelSpinner));
		switchTabSpinner.bind(items, 0);

		// "move" label and padding
		TextView newPosLabel = (TextView)moveTabPanel.findViewById(R.id.spinnerPabelLabel);
		newPosLabel.setText(R.string.moveTab);
		newPosLabel.append(":");
		moveTabPanel.setPadding(padding, padding, padding, padding);

		// tab height
		final SeekBar heightSeekBar = new SeekBar(context);
		heightSeekBar.setMax(4);
		heightSeekBar.setProgress(preferences.getTabExtraHeight());
		heightSeekBar.setPadding(heightSeekBar.getPaddingLeft(), padding, heightSeekBar.getPaddingRight(), padding);

		// extras panel
		LinearLayout extras = new LinearLayout(context);
		extras.setOrientation(LinearLayout.VERTICAL);
		extras.setPadding(padding, padding, padding, padding);
		extras.addView(heightSeekBar);
		extras.addView(moveTabPanel);

		/*
		 * label editor
		 */
		final String currentLabel = preferences.getTabTitle(tabindex);
		TextEditorListener callback = new DialogHelper.TextEditorListener() {
			@Override
			public void onTextChanged(String text) {
				preferences.writeTabTitle(tabindex, text);
				preferences.saveTabExtraHeight(heightSeekBar.getProgress());
				activity.redrawTabContainer();
				applyMoveTab(tabindex, switchTabSpinner);
			}
		};

		DialogHelper.openLabelEditor(context, currentLabel, InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS, callback, extras);
	}

	private void applyMoveTab(int tabindex, SpinnerHelper switchTabSpinner) {
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
