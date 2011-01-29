package com.dynamicg.bookmarkTree.prefs;

import android.content.SharedPreferences;

import com.dynamicg.bookmarkTree.BookmarkTreeContext;
import com.dynamicg.common.Logger;

public class PreferencesUpdater {

	private static final Logger log = new Logger(PreferencesUpdater.class);
	
	public static void write() {
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

}
