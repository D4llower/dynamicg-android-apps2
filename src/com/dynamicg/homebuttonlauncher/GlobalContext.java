package com.dynamicg.homebuttonlauncher;

import java.util.HashMap;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import com.dynamicg.homebuttonlauncher.preferences.PrefSettings;

public class GlobalContext {

	public static PackageManager packageManager;
	public static PrefSettings prefSettings;
	public static Resources resources;
	public static float density;

	public static final HashMap<String, String> labels = new HashMap<String, String>();
	public static final HashMap<String, Drawable> icons = new HashMap<String, Drawable>();

	public static void init(Context context, PrefSettings prefSettings) {
		GlobalContext.packageManager = context.getPackageManager();
		GlobalContext.prefSettings = prefSettings;
		GlobalContext.resources = context.getResources();
		GlobalContext.density = resources.getDisplayMetrics().density;
	}

	public static void resetCache() {
		labels.clear();
		icons.clear();
	}

	public static int getDimension(int id) {
		return (int)resources.getDimension(id);
	}

}
