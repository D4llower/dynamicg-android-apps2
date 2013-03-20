package com.dynamicg.homebuttonlauncher.tools.icons;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import com.dynamicg.common.Logger;
import com.dynamicg.homebuttonlauncher.AppEntry;
import com.dynamicg.homebuttonlauncher.GlobalContext;
import com.dynamicg.homebuttonlauncher.preferences.PrefSettings;

/*
 * see https://github.com/android/platform_frameworks_base/blob/master/core/java/com/android/internal/app/ResolverActivity.java
 * (mIconDpi)
 */
public class LargeIconLoader {

	private static final Logger log = new Logger(LargeIconLoader.class);

	private final int largeIconDensity;

	private LargeIconLoader(Context context) {
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);
		this.largeIconDensity = activityManager.getLauncherLargeIconDensity();
	}

	public static LargeIconLoader createInstance(Context context, PrefSettings settings) {
		if (!settings.isHighResIcons()) {
			return null;
		}
		return new LargeIconLoader(context);
	}

	public Drawable getLargeIcon(AppEntry entry) {
		int icon = entry.resolveInfo.getIconResource();
		if (icon==0) {
			return null;
		}
		try {
			Resources appRes = GlobalContext.packageManager.getResourcesForApplication(entry.getPackage());
			Drawable drawableForDensity = appRes.getDrawableForDensity(icon, largeIconDensity);
			log.debug("getLargeIcon", drawableForDensity, entry.label);
			return drawableForDensity;
		}
		catch (NameNotFoundException e) {
			return null; // ignore
		}
	}

}
