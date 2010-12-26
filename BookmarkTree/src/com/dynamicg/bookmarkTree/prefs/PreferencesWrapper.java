package com.dynamicg.bookmarkTree.prefs;

import android.content.SharedPreferences;

import com.dynamicg.bookmarkTree.BookmarkTreeContext;
import com.dynamicg.bookmarkTree.VersionAccessor;
import com.dynamicg.common.Logger;

public class PreferencesWrapper {

	private static final Logger log = new Logger(PreferencesWrapper.class);
	
	private static final String KEY_FOLDER_SEPARATOR = "separator";
	private static final String DEFVALUE_FOLDER_SEPARATOR = "-";
	
	private static final String KEY_DISCLAIMER = "disclaimerLastDisplayed";
	private static final String KEY_OPTIMISED_LAYOUT = "optimisedLayout";
	private static final String KEY_LIST_STYLE = "listStyle";
	private static final String KEY_SORT_OPTION = "sortOption";
	private static final String KEY_KEEP_STATE = "keepState";
	private static final String KEY_SCALE_ICONS = "scaleIcons";
	
	public final PreferencesBean prefsBean;
	
	public PreferencesWrapper() {
		final SharedPreferences settings = BookmarkTreeContext.settings;
		this.prefsBean = new PreferencesBean();
		
		setFolderSeparator ( settings.getString(KEY_FOLDER_SEPARATOR, DEFVALUE_FOLDER_SEPARATOR) );
		prefsBean.disclaimerLastDisplayed = settings.getInt(KEY_DISCLAIMER, 0);
		
		int defaultOptimisation=0;
		if (!settings.contains(KEY_OPTIMISED_LAYOUT)) {
			defaultOptimisation = VersionAccessor.isEclairOrHigher() ? 1 : 0;
		}
		prefsBean.optimisedLayout = settings.getInt(KEY_OPTIMISED_LAYOUT, defaultOptimisation);
		
		prefsBean.listStyle = settings.getInt(KEY_LIST_STYLE,0);
		prefsBean.sortOption = settings.getInt(KEY_SORT_OPTION,0);
		prefsBean.keepState = settings.getInt(KEY_KEEP_STATE,1); // default "ON"
		prefsBean.scaleIcons = settings.getInt(KEY_SCALE_ICONS,1); // default "ON"
	}
	
	public void write() {
		SharedPreferences.Editor editor = BookmarkTreeContext.settings.edit();
		if (log.debugEnabled) {
			log.debug("write prefs - folderSeparator", prefsBean.folderSeparator);
			log.debug("write prefs - listStyle", prefsBean.listStyle);
			log.debug("write prefs - sortOption", prefsBean.sortOption);
		}
		editor.putString(KEY_FOLDER_SEPARATOR, prefsBean.folderSeparator);
		editor.putInt(KEY_DISCLAIMER, prefsBean.disclaimerLastDisplayed);
		editor.putInt(KEY_OPTIMISED_LAYOUT, prefsBean.optimisedLayout);
		editor.putInt(KEY_LIST_STYLE, prefsBean.listStyle);
		editor.putInt(KEY_SORT_OPTION, prefsBean.sortOption);
		editor.putInt(KEY_KEEP_STATE, prefsBean.keepState);
		editor.putInt(KEY_SCALE_ICONS, prefsBean.scaleIcons);
		editor.commit();
	}
	
	public PreferencesBean getPrefsBean() {
		return prefsBean;
	}
	
	private void setFolderSeparator(String folderSeparator) {
		prefsBean.folderSeparator = folderSeparator;
		prefsBean.nodeConcatenation = " "+folderSeparator+" ";
	}
	
	public void setNewSeparator(String newSeparator) {
		setFolderSeparator(newSeparator.trim());
	}

	public boolean isOptimisedLayout() {
		return prefsBean.optimisedLayout==1;
	}
	public void setOptimisedLayout(boolean checkboxCheckedState) {
		prefsBean.optimisedLayout = checkboxCheckedState?1:0;
	}
	
	public void storeDisclaimerLastDisplayed(int newDisclaimerVersion) {
		prefsBean.disclaimerLastDisplayed = newDisclaimerVersion;
		write();
	}
	
	public static final int SORT_ALPHA = 0;
	public static final int SORT_FOLDERS_FIRST = 1;
	public static final int SORT_BOOKMARKS_FIRST = 2;
	
	public static final int LIST_STYLE_CLASSIC = 0;
	public static final int LIST_STYLE_COMPACT = 1;
	
	public boolean isCompact() {
		return prefsBean.listStyle==LIST_STYLE_COMPACT;
	}
	
	public boolean isKeepState() {
		return prefsBean.keepState == 1;
	}
	
	public boolean isScaleIcons() {
		return prefsBean.scaleIcons == 1;
	}
	
}
