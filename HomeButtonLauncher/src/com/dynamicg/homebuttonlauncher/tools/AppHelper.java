package com.dynamicg.homebuttonlauncher.tools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import com.dynamicg.common.Logger;
import com.dynamicg.homebuttonlauncher.AppEntry;
import com.dynamicg.homebuttonlauncher.AppListContainer;
import com.dynamicg.homebuttonlauncher.GlobalContext;
import com.dynamicg.homebuttonlauncher.HBLConstants;
import com.dynamicg.homebuttonlauncher.preferences.PrefShortlist;

public class AppHelper {

	private static final Logger log = new Logger(AppHelper.class);

	private static final String SELF = "com.dynamicg.homebuttonlauncher/com.dynamicg.homebuttonlauncher.MainActivityOpen";
	private static final int MAX_SORTNR = 999;

	public static String getComponentName(ResolveInfo resolveInfo) {
		return new StringBuilder()
		.append(resolveInfo.activityInfo.packageName)
		.append("/")
		.append(resolveInfo.activityInfo.name)
		.toString();
	}

	// see http://stackoverflow.com/questions/2780102/open-another-application-from-your-own-intent
	public static Intent getStartIntent(String componentName) {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		intent.setComponent(ComponentName.unflattenFromString(componentName));
		return intent;
	}

	public static ResolveInfo getMatchingApp(String component) {
		Intent intent = getStartIntent(component);
		final List<ResolveInfo> apps = GlobalContext.packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
		if (apps!=null && apps.size()==1) {
			// note "intent.setComponent" with only the activity name will match multiple entries (e.g. "com.dynamicg.bookmarkTree.Main")
			return apps.get(0);
		}
		else {
			log.debug("no match", component, (apps!=null?apps.size():-1));
		}
		return null;
	}

	public static AppListContainer getAllAppsList(PrefShortlist settings) {
		final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

		ArrayList<AppEntry> list = new ArrayList<AppEntry>();

		Collection<String> selectedComponents = new HashSet<String>(settings.getComponentsSet());
		selectedComponents.add(SELF); // do not show my own app in the list

		final List<ResolveInfo> apps = GlobalContext.packageManager.queryIntentActivities(mainIntent, 0);
		for (ResolveInfo resolveInfo:apps) {
			String component = getComponentName(resolveInfo);
			// System.err.println("COMPONENT:["+component+"]");
			if (!selectedComponents.contains(component)) {
				// skip apps already on the list
				list.add(new AppEntry(resolveInfo, 0, false));
			}
		}

		return new AppListContainer(list);
	}

	private static int getSortNr(Map<String, Integer> components, String component) {
		int sortnr = components.get(component);
		if (sortnr==0) {
			// unsorted new entries get to the bottom
			return MAX_SORTNR;
		}
		return sortnr;
	}

	public static AppListContainer getSelectedAppsList(PrefShortlist settings, boolean forMainScreen) {
		final Map<String, Integer> components = settings.getComponentsMap();
		final ArrayList<AppEntry> list = new ArrayList<AppEntry>();
		for (String component:components.keySet()) {

			if (component.startsWith(HBLConstants.SHORTCUT_PREFIX)) {
				if (component.contains(HBLConstants.SHORTCUT_SEPARATOR)) {
					int sortnr = getSortNr(components, component);
					list.add(new AppEntry(component, sortnr, forMainScreen));
				}
				continue;
			}

			ResolveInfo matchingApp = getMatchingApp(component);
			if (matchingApp!=null) {
				int sortnr = getSortNr(components, component);
				list.add(new AppEntry(matchingApp, sortnr, forMainScreen));
			}

		}
		return new AppListContainer(list);
	}

	/**
	 * neither "installerPackageName" nor "FLAG_SYSTEM / FLAG_UPDATED_SYSTEM_APP" gets us reliable info on whether
	 * an app is actually available in play store or not.
	 * e.g. "Maps" is system, "Calendar" is system+system_updated, "Browser" & "Settings" is system, but only the first two should have the "play store" link
	 * 
	 * also, some apps have installerPackageName==null even though they were installed through play store (??)
	 * 
	 * @param context
	 * @param appEntry
	 * @return
	 * @return
	 */
	//	public static boolean showPlayStoreLink(Context context, AppEntry appEntry) {
	//		return true;
	//	}

	public static AppListContainer getShortcutApps() {
		Intent shortcutsIntent = new Intent(Intent.ACTION_CREATE_SHORTCUT);
		List<ResolveInfo> shortcutApps = GlobalContext.packageManager.queryIntentActivities(shortcutsIntent, 0);
		ArrayList<AppEntry> list = new ArrayList<AppEntry>();
		for (ResolveInfo resolveInfo:shortcutApps) {
			list.add(new AppEntry(resolveInfo, 0, false));
		}
		return new AppListContainer(list);
	}

}