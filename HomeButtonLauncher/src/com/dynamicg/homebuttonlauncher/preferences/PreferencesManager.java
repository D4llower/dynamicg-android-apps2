package com.dynamicg.homebuttonlauncher.preferences;

import java.util.Arrays;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import com.dynamicg.homebuttonlauncher.tools.AppHelper;

public class PreferencesManager {

	protected static final String PREF_SHORTLIST = "apps";
	protected static final String PREF_SETTINGS = "settings";

	private static final String KEY_TAB_INDEX = "tabIndex";
	private static final String KEY_TAB_LABEL_PREFIX = "tabTitle.";

	private static final String[] DFLT_GOOGLE_SEARCH = new String[] {
		"com.google.android.googlequicksearchbox/com.google.android.googlequicksearchbox.SearchActivity"
		, "com.android.quicksearchbox/com.android.quicksearchbox.SearchActivity"
	};

	private final Context context;
	public final PrefSettings prefSettings;
	public final PrefShortlist prefShortlist;

	protected static String getShortlistName(int tabindex) {
		return tabindex==0 ? PREF_SHORTLIST : PREF_SHORTLIST+tabindex;
	}

	private SharedPreferences getShortlistPrefs(int tabindex) {
		return context.getSharedPreferences(getShortlistName(tabindex), Context.MODE_PRIVATE);
	}

	public PreferencesManager(Context context) {
		this.context = context;
		this.prefSettings = new PrefSettings(context.getSharedPreferences(PREF_SETTINGS, Context.MODE_PRIVATE));
		int tabindex = getTabIndex();
		this.prefShortlist = new PrefShortlist(context.getPackageManager(), getShortlistPrefs(tabindex));
		if (tabindex==0) {
			// skip for all extra tabs
			checkOnStartup(context);
		}
	}

	private void checkOnStartup(Context context) {
		if (prefShortlist.size()>0) {
			return;
		}
		PackageManager packageManager = context.getPackageManager();
		for (String defaultApp:DFLT_GOOGLE_SEARCH) {
			if (AppHelper.getMatchingApp(packageManager, defaultApp)!=null) {
				prefShortlist.add(Arrays.asList(defaultApp));
				return;
			}
		}
	}

	public void switchShortlist(int tabindex) {
		prefShortlist.switchSharedPrefs(getShortlistPrefs(tabindex));
		prefSettings.write(KEY_TAB_INDEX, tabindex);
	}

	public int getTabIndex() {
		int tabindex = prefSettings.getIntValue(KEY_TAB_INDEX);
		int numTabs = prefSettings.getNumTabs();
		return tabindex>=numTabs?0:tabindex;
	}

	public String getTabTitle(int index) {
		return prefSettings.getStringValue(KEY_TAB_LABEL_PREFIX+index, "");
	}

	public void writeTabTitle(int index, String label) {
		prefSettings.write(KEY_TAB_LABEL_PREFIX+index, label);
	}

}
