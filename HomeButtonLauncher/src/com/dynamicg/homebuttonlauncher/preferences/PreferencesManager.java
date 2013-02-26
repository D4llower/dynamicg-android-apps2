package com.dynamicg.homebuttonlauncher.preferences;

import java.util.Arrays;

import android.content.Context;
import android.content.pm.PackageManager;

import com.dynamicg.homebuttonlauncher.tools.AppHelper;

public class PreferencesManager {

	protected static final String PREF_SHORTLIST = "apps";
	protected static final String PREF_SETTINGS = "settings";

	private static final String[] DFLT_GOOGLE_SEARCH = new String[] {
		"com.google.android.googlequicksearchbox/com.google.android.googlequicksearchbox.SearchActivity"
		, "com.android.quicksearchbox/com.android.quicksearchbox.SearchActivity"
	};

	public final PrefShortlist prefShortlist;
	public final PrefSettings prefSettings;

	public PreferencesManager(Context context) {
		this.prefShortlist = new PrefShortlist(context.getPackageManager(), context.getSharedPreferences(PREF_SHORTLIST, Context.MODE_PRIVATE));
		this.prefSettings = new PrefSettings(context.getSharedPreferences(PREF_SETTINGS, Context.MODE_PRIVATE));
		checkOnStartup(context);
		//invalidate(); // to test invalid apps
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

	public int getTabIndex() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void switchShortlist(int tabIndex) {
		// TODO Auto-generated method stub

	}

}
