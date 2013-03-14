package com.dynamicg.homebuttonlauncher;

import android.content.Context;
import android.content.pm.PackageManager;

import com.dynamicg.homebuttonlauncher.preferences.PrefSettings;

public class GlobalContext {

	public static PackageManager packageManager;
	public static PrefSettings prefSettings;

	public static void init(Context context, PrefSettings prefSettings) {
		GlobalContext.packageManager = context.getPackageManager();
		GlobalContext.prefSettings = prefSettings;
	}

}
