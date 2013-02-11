package com.dynamicg.homebuttonlauncher.preferences;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.dynamicg.homebuttonlauncher.R;
import com.dynamicg.homebuttonlauncher.dialog.SizePrefsHelper;

public class PrefSettings {

	private static final String KEY_LAYOUT = "layout";
	private static final String KEY_LABEL_SIZE = "labelSize";
	private static final String KEY_ICON_SIZE = "iconSize";
	private static final String KEY_HIGH_RES = "highRes";

	public static final int NUM_LAYOUTS = 4;
	private static final int LAYOUT_PLAIN_2 = 1;
	private static final int LAYOUT_COMPACT_2 = 2;
	private static final int LAYOUT_COMPACT_4 = 3;

	private final SharedPreferences sharedPrefs;

	public PrefSettings(SharedPreferences settingsPrefs) {
		this.sharedPrefs = settingsPrefs;
	}

	public void writeAppSettings(int layout, int labelSize, int iconSize, boolean highResIcons) {
		Editor edit = sharedPrefs.edit();
		edit.putInt(KEY_LAYOUT, layout);
		edit.putInt(KEY_LABEL_SIZE, labelSize);
		edit.putInt(KEY_ICON_SIZE, iconSize);
		edit.putBoolean(KEY_HIGH_RES, highResIcons);
		edit.commit();
	}

	// this is "sp". see com.dynamicg.homebuttonlauncher.dialog.SizePrefsHelper.LABEL_SIZES
	public int getLabelSize() {
		return sharedPrefs.getInt(KEY_LABEL_SIZE, SizePrefsHelper.DEFAULT_LABEL_SIZE);
	}

	// this is "dp". see com.dynamicg.homebuttonlauncher.dialog.SizePrefsHelper.ICON_SIZES
	public int getIconSize() {
		return sharedPrefs.getInt(KEY_ICON_SIZE, SizePrefsHelper.DEFAULT_ICON_SIZE);
	}

	public int getLayoutType() {
		return sharedPrefs.getInt(KEY_LAYOUT, 0);
	}

	public int getListLayoutId() {
		int layoutType = getLayoutType();
		if (layoutType==LAYOUT_COMPACT_4) {
			return R.layout.applist_gridview4;
		}
		else if (layoutType==LAYOUT_PLAIN_2 || layoutType==LAYOUT_COMPACT_2) {
			return R.layout.applist_gridview2;
		}
		return R.layout.applist_listview;
	}

	public int getMinWidthDimen() {
		int layoutType = getLayoutType();
		if (layoutType==LAYOUT_PLAIN_2 || layoutType==LAYOUT_COMPACT_4) {
			return R.dimen.widthWide;
		}
		return R.dimen.widthDefault;
	}

	public int getAppEntryLayoutId() {
		int layoutType = getLayoutType();
		if (layoutType==LAYOUT_COMPACT_2 || layoutType==LAYOUT_COMPACT_4) {
			return R.layout.app_entry_compact;
		}
		return R.layout.app_entry_default;
	}

	public boolean isHighResIcons() {
		return sharedPrefs.getBoolean(KEY_HIGH_RES, false);
	}

}
