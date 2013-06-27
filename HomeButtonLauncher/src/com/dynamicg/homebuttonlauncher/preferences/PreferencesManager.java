package com.dynamicg.homebuttonlauncher.preferences;

import java.util.Arrays;

import android.content.Context;
import android.content.SharedPreferences;

import com.dynamicg.homebuttonlauncher.GlobalContext;
import com.dynamicg.homebuttonlauncher.HBLConstants;
import com.dynamicg.homebuttonlauncher.tools.AppHelper;

public class PreferencesManager {

	private static final String KEY_TAB_INDEX = "tabIndex";
	private static final String KEY_TAB_LABEL_PREFIX = "tabTitle.";

	public static final String[] DFLT_GOOGLE_SEARCH = new String[] {
		"com.google.android.googlequicksearchbox/com.google.android.googlequicksearchbox.SearchActivity"
		, "com.android.quicksearchbox/com.android.quicksearchbox.SearchActivity"
	};

	private final Context context;
	public final PrefSettings prefSettings;
	public final PrefShortlist prefShortlist;

	private int currentTabIndex;

	protected static String getShortlistName(int tabindex) {
		return tabindex==0 ? HBLConstants.PREFS_APPS: HBLConstants.PREFS_APPS+tabindex;
	}

	private SharedPreferences getShortlistPrefs(int tabindex) {
		return context.getSharedPreferences(getShortlistName(tabindex), Context.MODE_PRIVATE);
	}

	public PreferencesManager(Context context) {
		this.context = context;
		this.prefSettings = new PrefSettings(context);
		GlobalContext.init(context, prefSettings);

		currentTabIndex = getCurrentTabIndex(prefSettings);
		this.prefShortlist = new PrefShortlist(getShortlistPrefs(currentTabIndex));
		if (currentTabIndex==0) {
			// skip for all extra tabs
			checkOnStartup();
		}
	}

	private static int getCurrentTabIndex(PrefSettings prefSettings) {
		final int numTabs = prefSettings.getNumTabs();
		if (numTabs==0) {
			return 0;
		}
		final int homeTabIndex = prefSettings.getHomeTabNum()-1;
		if (homeTabIndex>=0 && homeTabIndex<numTabs) {
			return homeTabIndex;
		}
		final int recentTabIndex = prefSettings.getIntValue(KEY_TAB_INDEX);
		if (recentTabIndex>=0 && recentTabIndex<numTabs) {
			return recentTabIndex;
		}
		return 0;
	}

	private void checkOnStartup() {
		if (prefShortlist.size()>0) {
			return;
		}
		for (String defaultApp:DFLT_GOOGLE_SEARCH) {
			if (AppHelper.getMatchingApp(defaultApp)!=null) {
				prefShortlist.add(Arrays.asList(defaultApp));
				return;
			}
		}
	}

	public void updateCurrentTabIndex(int tabindex) {
		currentTabIndex = tabindex;
		prefShortlist.switchSharedPrefs(getShortlistPrefs(tabindex));
		prefSettings.apply(KEY_TAB_INDEX, tabindex);
	}

	public int getTabIndex() {
		return currentTabIndex;
	}

	public String getTabTitle(int index) {
		return prefSettings.getStringValue(KEY_TAB_LABEL_PREFIX+index, "");
	}

	public void writeTabTitle(int index, String label) {
		prefSettings.apply(KEY_TAB_LABEL_PREFIX+index, label);
	}

}
