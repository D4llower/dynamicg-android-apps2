package com.dynamicg.bookmarkTree.prefs;

import android.graphics.Color;
import android.text.format.Time;

import com.dynamicg.bookmarkTree.BookmarkTreeContext;

public class PreferencesWrapper {
	
	/*
	 * GLOBALS
	 */
	public static final int SORT_ALPHA = 0;
	public static final int SORT_FOLDERS_FIRST = 1;
	public static final int SORT_BOOKMARKS_FIRST = 2;
	
	public static final int LIST_SIZE_LARGE = 0;
	public static final int LIST_SIZE_MEDIUM = 1;
	public static final int LIST_SIZE_SMALL = 2;
	
	public static final int ICON_SCALING_NONE = 0;
	public static final int ICON_SCALING_160 = 1;
	public static final int ICON_SCALING_160_120 = 2;
	public static final int ICON_SCALING_240 = 3;
	public static final int ICON_SCALING_240_160 = 4;
	public static final int ICON_SCALING_160_100 = 5;
	public static final int ICON_SCALING_160_80 = 6;
	
	/*
	 * KEYS
	 */
	protected static final String KEY_FOLDER_SEPARATOR = "separator";
	protected static final String DEFVALUE_FOLDER_SEPARATOR = "-";
	
	public static final String KEY_DISCLAIMER = "disclaimerLastDisplayed";
	private static final String KEY_DATE_INITIALISED = "dateInitialised";
	
	/*
	 * PREFERENCE ITEMS
	 */
	public static final PrefEntryInt listStyle = new PrefEntryInt("listStyle", 0);
	public static final PrefEntryInt sortOption = new PrefEntryInt("sortOption", 0);
	public static final PrefEntryInt keepState = new PrefEntryInt("keepState", 1);
	public static final PrefEntryInt iconScaling = new PrefEntryInt("scaleIcons", ICON_SCALING_160_120);
	public static final PrefEntryInt sortCaseInsensitive = new PrefEntryInt("sortCaseInsensitive", 1);

	public static final PrefEntryInt colorFolder = new PrefEntryInt("color.folder", Color.WHITE);
	public static final PrefEntryInt colorBookmarkTitle = new PrefEntryInt("color.bookmarkTitle", Color.WHITE);
	public static final PrefEntryInt colorBookmarkUrl = new PrefEntryInt("color.bookmarkUrl", Color.WHITE);
	
	public static final SeparatorPreference separatorPreference = new SeparatorPreference();
	static {
		String separator = BookmarkTreeContext.settings.getString(KEY_FOLDER_SEPARATOR, DEFVALUE_FOLDER_SEPARATOR);
		PreferencesUpdater.setFolderSeparator(separator);
	}
	
	/*
	 * WRAPPERS
	 */
	public static boolean isListStyleMedium() {
		return listStyle.value == LIST_SIZE_MEDIUM;
	}
	public static boolean isListStyleSmall() {
		return listStyle.value == LIST_SIZE_SMALL;
	}
	
	/*
	 * extra handling on first call (i.e. after installation)
	 */
	public static void initialSetup() {
		
		if (BookmarkTreeContext.settings.contains(KEY_DISCLAIMER)) {
			// if this key is available, the disclaimer has already been shown
			return;
		}
		
		PreferencesUpdater.updateAndWrite(listStyle, LIST_SIZE_MEDIUM); // as of 1.12, default list style is "medium"
		
		// "init date" value
		Time t = new Time();
		t.setToNow();
		int value = Integer.parseInt(t.format("%Y%m%d"));
		PreferencesUpdater.writeIntPref(KEY_DATE_INITIALISED, value);
		
	}
	
}
