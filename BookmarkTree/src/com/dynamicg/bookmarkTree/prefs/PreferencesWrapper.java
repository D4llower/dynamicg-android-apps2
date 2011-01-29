package com.dynamicg.bookmarkTree.prefs;

import android.graphics.Color;
import android.text.format.Time;

import com.dynamicg.bookmarkTree.BookmarkTreeContext;
import com.dynamicg.bookmarkTree.VersionAccessor;

public class PreferencesWrapper {
	
	protected static final String KEY_FOLDER_SEPARATOR = "separator";
	protected static final String DEFVALUE_FOLDER_SEPARATOR = "-";
	
	public static final String KEY_DISCLAIMER = "disclaimerLastDisplayed";
	private static final String KEY_DATE_INITIALISED = "dateInitialised";
	private static final String KEY_OPTIMISED_LAYOUT = "optimisedLayout";
	
	public static final PrefEntryInt listStyle = new PrefEntryInt("listStyle", 0);
	public static final PrefEntryInt sortOption = new PrefEntryInt("sortOption", 0);
	public static final PrefEntryInt keepState = new PrefEntryInt("keepState", 1);
	public static final PrefEntryInt scaleIcons = new PrefEntryInt("scaleIcons", 1);
	public static final PrefEntryInt sortCaseInsensitive = new PrefEntryInt("sortCaseInsensitive", 1);

	public static final PrefEntryInt colorFolder = new PrefEntryInt("color.folder", Color.WHITE);
	public static final PrefEntryInt colorBookmarkTitle = new PrefEntryInt("color.bookmarkTitle", Color.WHITE);
	public static final PrefEntryInt colorBookmarkUrl = new PrefEntryInt("color.bookmarkUrl", Color.WHITE);
	
	public static final PrefEntryInt optimisedLayout;
	static {
		int defaultOptimisation=0;
		if (!BookmarkTreeContext.settings.contains(KEY_OPTIMISED_LAYOUT)) {
			defaultOptimisation = VersionAccessor.isEclairOrHigher() ? 1 : 0;
		}
		optimisedLayout = new PrefEntryInt(KEY_OPTIMISED_LAYOUT, defaultOptimisation);
	}
	
	public static final SeparatorPreference separatorPreference = new SeparatorPreference();
	static {
		String separator = BookmarkTreeContext.settings.getString(KEY_FOLDER_SEPARATOR, DEFVALUE_FOLDER_SEPARATOR);
		PreferencesUpdater.setFolderSeparator(separator);
	}
	
	public static final int SORT_ALPHA = 0;
	public static final int SORT_FOLDERS_FIRST = 1;
	public static final int SORT_BOOKMARKS_FIRST = 2;
	
	public static final int LIST_SIZE_LARGE = 0;
	public static final int LIST_SIZE_MEDIUM = 1;
	public static final int LIST_SIZE_SMALL = 2;
	
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
		PreferencesUpdater.write(optimisedLayout); // no need to calculate the default every time
		
		// "init date" value
		Time t = new Time();
		t.setToNow();
		int value = Integer.parseInt(t.format("%Y%m%d"));
		PreferencesUpdater.writeIntPref(KEY_DATE_INITIALISED, value);
		
	}
	
}
