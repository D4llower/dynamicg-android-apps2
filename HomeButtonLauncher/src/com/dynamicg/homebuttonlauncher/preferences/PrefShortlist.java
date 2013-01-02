package com.dynamicg.homebuttonlauncher.preferences;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import com.dynamicg.homebuttonlauncher.tools.AppHelper;

public class PrefShortlist {

	private final PackageManager packageManager;
	public final SharedPreferences sharedPrefs;

	public PrefShortlist(PackageManager packageManager, SharedPreferences appPrefs) {
		this.packageManager = packageManager;
		this.sharedPrefs = appPrefs;
	}

	public int size() {
		return sharedPrefs.getAll().size();
	}

	public Collection<String> getComponents() {
		Map<String, ?> all = sharedPrefs.getAll();
		Set<String> keySet = all.keySet();
		return keySet;
	}

	public void add(List<String> components) {
		Editor edit = sharedPrefs.edit();
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
		Editor edit = sharedPrefs.edit();
		for (String comp:components) {
			edit.remove(comp);
		}
		edit.commit();
	}

}
