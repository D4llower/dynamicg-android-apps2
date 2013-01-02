package com.dynamicg.homebuttonlauncher.preferences;

import java.util.Arrays;

import android.content.Context;

public class PreferencesManager {

	private static final String PREF_SHORTLIST = "apps";
	private static final String PREF_SETTINGS = "settings";
	private static final String DFLT_GOOGLE_SEARCH = "com.google.android.googlequicksearchbox/com.google.android.googlequicksearchbox.SearchActivity";

	public final PrefShortlist prefShortlist;
	public final PrefSettings prefSettings;

	public PreferencesManager(Context context) {
		this.prefShortlist = new PrefShortlist(context.getPackageManager(), context.getSharedPreferences(PREF_SHORTLIST, Context.MODE_PRIVATE));
		this.prefSettings = new PrefSettings(context.getSharedPreferences(PREF_SETTINGS, Context.MODE_PRIVATE));
		checkOnStartup();
		//invalidate(); // to test invalid apps
	}

	private void checkOnStartup() {
		if (prefShortlist.size()==0) {
			prefShortlist.add(Arrays.asList(DFLT_GOOGLE_SEARCH));
		}
	}

}
