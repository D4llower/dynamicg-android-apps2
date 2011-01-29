package com.dynamicg.bookmarkTree.prefs;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.dynamicg.bookmarkTree.BookmarkTreeContext;
import com.dynamicg.common.Logger;

public class PreferencesUpdater {

	private static final Logger log = new Logger(PreferencesUpdater.class);
	
	public static void writeAll() {
		SharedPreferences.Editor editor = BookmarkTreeContext.settings.edit();
		
		// separator handled separately
		if (log.debugEnabled) {
			log.debug("write prefs - folderSeparator", PreferencesWrapper.separatorPreference.folderSeparator);
		}
		editor.putString ( PreferencesWrapper.KEY_FOLDER_SEPARATOR, PreferencesWrapper.separatorPreference.folderSeparator);
		
		// all the other "int" items
		PrefEntryInt.pushNewValue();
		for (PrefEntryInt item:PrefEntryInt.cache) {
			editor.putInt(item.name, item.value);
			if (log.debugEnabled) {
				log.debug("PrefEntryInt", item.name, item.value);
			}
		}
		
		editor.commit();
	}
	
	public static void setFolderSeparator(String folderSeparator) {
		PreferencesWrapper.separatorPreference.folderSeparator = folderSeparator;
		PreferencesWrapper.separatorPreference.nodeConcatenation = " "+folderSeparator+" ";
	}
	
	public static void setNewSeparator(String newSeparator) {
		setFolderSeparator(newSeparator.trim());
	}

	public static void writeIntPref(String key, int value) {
		if (log.debugEnabled) {
			log.debug("write int pref", key, value);
		}
		Editor edit = BookmarkTreeContext.settings.edit();
		edit.putInt(key, value);
		edit.commit();
	}
	
	public static void updateAndWrite(PrefEntryInt item, int newValue) {
		item.value = newValue;
		writeIntPref(item.name, item.value);
	}
	public static void write(PrefEntryInt item) {
		writeIntPref(item.name, item.value);
	}
	
}
