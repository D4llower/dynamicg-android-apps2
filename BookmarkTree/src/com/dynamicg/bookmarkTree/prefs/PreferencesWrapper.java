package com.dynamicg.bookmarkTree.prefs;

import android.graphics.Color;

import com.dynamicg.bookmarkTree.BookmarkTreeContext;
import com.dynamicg.bookmarkTree.VersionAccessor;

public class PreferencesWrapper {
	
	protected static final String KEY_FOLDER_SEPARATOR = "separator";
	protected static final String DEFVALUE_FOLDER_SEPARATOR = "-";
	
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
	
	public static final int LIST_STYLE_CLASSIC = 0;
	public static final int LIST_STYLE_COMPACT = 1;
	
	public static boolean isCompact() {
		return listStyle.value == LIST_STYLE_COMPACT;
	}
	
}
