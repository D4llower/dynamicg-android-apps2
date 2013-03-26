package com.dynamicg.homebuttonlauncher.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.dynamicg.homebuttonlauncher.HBLConstants;
import com.dynamicg.homebuttonlauncher.R;
import com.dynamicg.homebuttonlauncher.dialog.SizePrefsHelper;

public class PrefSettings {

	public static final String KEY_LAYOUT = "layout";
	public static final String KEY_LABEL_SIZE = "labelSize";
	public static final String KEY_ICON_SIZE = "iconSize";
	public static final String KEY_HIGH_RES = "highRes";
	public static final String KEY_AUTO_START_SINGLE = "autoStart";
	public static final String KEY_NUM_TABS = "numTabs";
	public static final String KEY_BACKGROUND_ICON_LOADER = "iconsInBG";
	public static final String KEY_SEMI_TRANSPARENT = "semiTransp";

	public static final int NUM_LAYOUTS = 5;
	private static final int LAYOUT_PLAIN_2 = 1;
	private static final int LAYOUT_COMPACT_2 = 2;
	private static final int LAYOUT_COMPACT_4 = 3;
	private static final int LAYOUT_COMPACT_3 = 4;

	public final SharedPreferences sharedPrefs;

	public PrefSettings(Context context) {
		this.sharedPrefs = context.getSharedPreferences(HBLConstants.PREFS_SETTINGS, Context.MODE_PRIVATE);
	}

	public void apply(String key, int value) {
		Editor edit = sharedPrefs.edit();
		edit.putInt(key, value);
		edit.apply();
	}

	public void apply(String key, String value) {
		Editor edit = sharedPrefs.edit();
		edit.putString(key, value);
		edit.apply();
	}

	public int getIntValue(String key) {
		return sharedPrefs.getInt(key, 0);
	}

	public String getStringValue(String key, String dflt) {
		return sharedPrefs.getString(key, dflt);
	}

	// this is "sp". see com.dynamicg.homebuttonlauncher.dialog.SizePrefsHelper.LABEL_SIZES
	public int getLabelSize() {
		return sharedPrefs.getInt(KEY_LABEL_SIZE, SizePrefsHelper.DEFAULT_LABEL_SIZE);
	}

	// this is "dp". see com.dynamicg.homebuttonlauncher.dialog.SizePrefsHelper.ICON_SIZES
	public int getIconSize() {
		return sharedPrefs.getInt(KEY_ICON_SIZE, SizePrefsHelper.DEFAULT_ICON_SIZE);
	}

	public int getNumTabs() {
		return sharedPrefs.getInt(KEY_NUM_TABS, 0);
	}

	public int getLayoutType() {
		return sharedPrefs.getInt(KEY_LAYOUT, 0);
	}

	public int[] getMainLayout() {
		int layoutType = getLayoutType();
		if (layoutType==LAYOUT_COMPACT_4) {
			return new int[]{R.layout.applist_gridview, 4};
		}
		else if (layoutType==LAYOUT_COMPACT_3) {
			return new int[]{R.layout.applist_gridview, 3};
		}
		else if (layoutType==LAYOUT_PLAIN_2 || layoutType==LAYOUT_COMPACT_2) {
			return new int[]{R.layout.applist_gridview, 2};
		}
		return new int[]{R.layout.applist_listview, 0};
	}

	public int getMinWidthDimension() {
		int layoutType = getLayoutType();
		if (layoutType==LAYOUT_PLAIN_2 || layoutType==LAYOUT_COMPACT_4) {
			return R.dimen.widthWide;
		}
		return R.dimen.widthDefault;
	}

	public int getAppEntryLayoutId() {
		int layoutType = getLayoutType();
		if (layoutType==LAYOUT_COMPACT_2 || layoutType==LAYOUT_COMPACT_3 || layoutType==LAYOUT_COMPACT_4) {
			return R.layout.app_entry_compact;
		}
		return R.layout.app_entry_default;
	}

	public boolean isHighResIcons() {
		return sharedPrefs.getBoolean(KEY_HIGH_RES, false);
	}

	public boolean isAutoStartSingle() {
		return sharedPrefs.getBoolean(KEY_AUTO_START_SINGLE, false);
	}

	public boolean isBackgroundIconLoader() {
		return sharedPrefs.getBoolean(KEY_BACKGROUND_ICON_LOADER, false);
	}

	public boolean isSemiTransparent() {
		return sharedPrefs.getBoolean(KEY_SEMI_TRANSPARENT, false);
	}

}
