package com.dynamicg.homebuttonlauncher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

public class Settings {

	private static final String PREFS_APPS = "apps";
	private static final String DFLT_GOOGLE_SEARCH = "com.google.android.googlequicksearchbox/com.google.android.googlequicksearchbox.SearchActivity";

	private final SharedPreferences appPrefs;
	private final PackageManager packageManager;

	public Settings(Context context) {
		this.appPrefs = context.getSharedPreferences(PREFS_APPS, Context.MODE_PRIVATE);
		this.packageManager = context.getPackageManager();
		checkOnStartup();
		//invalidate(); // to test invalid apps
	}

	public Collection<String> getComponents() {
		Map<String, ?> all = appPrefs.getAll();
		Set<String> keySet = all.keySet();
		return keySet;
	}

	public void add(List<String> components) {
		Editor edit = appPrefs.edit();
		for (String comp:components) {
			edit.putInt(comp, 0);
		}
		edit.commit();
		validateAll();
	}

	public void remove(List<String> components) {
		removeImpl(components);
		validateAll();
	}

	private void validateAll() {
		final Collection<String> components = getComponents();
		final ArrayList<String> zombies = new ArrayList<String>();
		for (String component:components) {
			ResolveInfo matchingApp = AppHelper.getMatchingApp(packageManager, component);
			if (matchingApp==null || !AppHelper.getComponentName(matchingApp).equals(component)) {
				zombies.add(component);
			}
		}
		if (zombies.size()>0) {
			removeImpl(zombies);
		}
	}

	private void removeImpl(List<String> components) {
		Editor edit = appPrefs.edit();
		for (String comp:components) {
			edit.remove(comp);
		}
		edit.commit();
	}

	private void checkOnStartup() {
		if (appPrefs.getAll().size()==0) {
			add(Arrays.asList(DFLT_GOOGLE_SEARCH));
		}
	}

	@SuppressWarnings("unused")
	private void invalidate() {
		Collection<String> components = getComponents();
		Editor edit = appPrefs.edit();
		edit.clear();
		for (String s:components) {
			edit.putInt("ZZZ"+s, 0);
		}
		edit.commit();
	}

}
