package com.dynamicg.homebuttonlauncher.tab;

import android.widget.TabHost;

import com.dynamicg.homebuttonlauncher.MainActivityHome;
import com.dynamicg.homebuttonlauncher.R;
import com.dynamicg.homebuttonlauncher.dialog.AppConfigDialog;

public class TabHelperAppAdd extends TabHelper {

	private final AppConfigDialog appConfigDialog;
	private final int selectedIndex;

	public TabHelperAppAdd(MainActivityHome activity, AppConfigDialog appConfigDialog, int selectedIndex) {
		super(activity, 2, appConfigDialog.findViewById(R.id.headerContainer));
		this.appConfigDialog = appConfigDialog;
		this.selectedIndex = selectedIndex;
	}

	@Override
	public TabHost bindTabs() {
		TabHost.OnTabChangeListener onTabChangeListener = new TabHost.OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabId) {
				appConfigDialog.tabChanged(Integer.parseInt(tabId));
			};
		};
		String[] labels = new String[]{"APPS", "SHORTCUTS"};
		TabHost tabhost = bindTabs(selectedIndex, labels, onTabChangeListener, null);
		return tabhost;
	}

}
