package com.dynamicg.homebuttonlauncher;

import java.util.HashMap;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.dynamicg.homebuttonlauncher.preferences.PrefSettings;

public class GlobalContext {

	public static PackageManager packageManager;
	public static PrefSettings prefSettings;

	public static final HashMap<String, String> labels = new HashMap<String, String>();
	public static final HashMap<String, Drawable> icons = new HashMap<String, Drawable>();

	public static void init(Context context, PrefSettings prefSettings) {
		GlobalContext.packageManager = context.getPackageManager();
		GlobalContext.prefSettings = prefSettings;
	}

	public static void resetCache() {
		labels.clear();
		icons.clear();
	}

}
