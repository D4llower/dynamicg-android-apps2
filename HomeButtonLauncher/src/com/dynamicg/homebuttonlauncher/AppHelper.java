package com.dynamicg.homebuttonlauncher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import com.dynamicg.common.Logger;

public class AppHelper {

	@SuppressWarnings("unused")
	private static final Logger log = new Logger(AppHelper.class);

	private static final String SELF = "com.dynamicg.homebuttonlauncher/com.dynamicg.homebuttonlauncher.MainActivityOpen";

	public static String getComponentName(ResolveInfo resolveInfo) {
		return resolveInfo.activityInfo.packageName + "/" + resolveInfo.activityInfo.name;
	}

	// see http://stackoverflow.com/questions/2780102/open-another-application-from-your-own-intent
	public static Intent getStartIntent(String componentName) {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		intent.setComponent(ComponentName.unflattenFromString(componentName));
		return intent;
	}

	public static ResolveInfo getMatchingApp(PackageManager packageManager, String component) {
		Intent intent = getStartIntent(component);
		final List<ResolveInfo> apps = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
		if (apps!=null && apps.size()>0 && getComponentName(apps.get(0)).equals(component)) {
			// TODO ## is there no "strict match" ??
			return apps.get(0);
		}
		return null;
	}

	public static List<AppEntry> getAllAppsList(Context context, Settings settings) {
		final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

		PackageManager packageManager = context.getPackageManager();
		ArrayList<AppEntry> list = new ArrayList<AppEntry>();

		Collection<String> selectedComponents = new HashSet<String>(settings.getComponents());
		selectedComponents.add(SELF); // do not show my own app in the list

		final List<ResolveInfo> apps = packageManager.queryIntentActivities(mainIntent, 0);
		for (ResolveInfo resolveInfo:apps) {
			String component = getComponentName(resolveInfo);
			// System.err.println("COMPONENT:["+component+"]");
			if (!selectedComponents.contains(component)) {
				// skip apps already on the list
				list.add(new AppEntry(packageManager, resolveInfo));
			}
		}

		return sort(list);
	}

	public static List<AppEntry> getSelectedAppsList(Context context, Settings settings) {
		final PackageManager packageManager = context.getPackageManager();
		final Collection<String> components = settings.getComponents();
		final ArrayList<AppEntry> list = new ArrayList<AppEntry>();

		for (String component:components) {
			ResolveInfo matchingApp = getMatchingApp(packageManager, component);
			if (matchingApp!=null) {
				list.add(new AppEntry(packageManager, matchingApp));
			}
		}

		return sort(list);

	}

	private static List<AppEntry> sort(List<AppEntry> list) {
		Collections.sort(list, new Comparator<AppEntry>(){
			@Override
			public int compare(AppEntry lhs, AppEntry rhs) {
				return lhs.getLabel().compareToIgnoreCase(rhs.getLabel());
			}
		});
		return list;
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
	 */
	public static boolean showPlayStoreLink(Context context, AppEntry appEntry) {
		return true;
	}

	//	private static final String PLAY_STORE_INSTALLER = "com.android.vending, com.google.android.feedback";
	//
	//	public static boolean isInstalledFromPlayStore(Context context, AppEntry appEntry) {
	//		String installerPackageName = context.getPackageManager().getInstallerPackageName(appEntry.getPackage());
	//		log.debug("isInstalledFromPlayStore", appEntry.getPackage(), installerPackageName);
	//		boolean fromPlayStore = installerPackageName!=null && PLAY_STORE_INSTALLER.contains(installerPackageName);
	//		//		if (!fromPlayStore) {
	//		//			Toast.makeText(context, "Installer: "+installerPackageName, Toast.LENGTH_SHORT).show();
	//		//		}
	//		return fromPlayStore;
	//	}
	//
	//	public static boolean isInstalledFromPlayStore(Context context, AppEntry appEntry) {
	//		final PackageManager packageManager = context.getPackageManager();
	//		final String pkgname = appEntry.getPackage();
	//
	//		final String installerPackageName = packageManager.getInstallerPackageName(pkgname);
	//		if (installerPackageName!=null) {
	//			// assume this is from play store
	//			return true;
	//		}
	//
	//		try {
	//			final PackageInfo packageInfo = packageManager.getPackageInfo(pkgname, 0);
	//			final boolean isSystem = (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0;
	//			final boolean isUpdatedSystem = (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) > 0;
	//			log.debug("SYS", isSystem, isUpdatedSystem, pkgname);
	//		}
	//		catch (NameNotFoundException e) {
	//		}
	//
	//		return false;
	//	}

}