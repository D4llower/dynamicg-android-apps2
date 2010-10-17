package com.dynamicg.bookmarkTree;

import android.content.Context;
import android.content.SharedPreferences;

import com.dynamicg.common.main.Logger;

public class PreferencesWrapper {

	private static final Logger log = new Logger(PreferencesWrapper.class);
	private static final String PREFS_NAME = "dynamicg.bookmarkTree";
	
	private static final String KEY_FOLDER_SEPARATOR = "separator";
	private static final String DEFVALUE_FOLDER_SEPARATOR = "-";
	
	private static final String KEY_DISCLAIMER = "disclaimerLastDisplayed";
	private static final String KEY_SHOW_DELETE_ICON = "showDeleteIcon";
	private static final String KEY_OPTIMISED_LAYOUT = "optimisedLayout";
	
	private final Context context;
	
	private String folderSeparator;
	private String nodeConcatenation;
	private int disclaimerLastDisplayed;
	private int showDeleteIcon;
	private int optimisedLayout;
	
	public PreferencesWrapper(Context context) {
		this.context = context;
		SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		setFolderSeparator ( settings.getString(KEY_FOLDER_SEPARATOR, DEFVALUE_FOLDER_SEPARATOR) );
		disclaimerLastDisplayed = settings.getInt(KEY_DISCLAIMER, 0);
		showDeleteIcon = settings.getInt(KEY_SHOW_DELETE_ICON, 1);
		
		int defaultOptimisation=0;
		if (!settings.contains(KEY_OPTIMISED_LAYOUT)) {
			defaultOptimisation = VersionAccessor.isEclairOrHigher() ? 1 : 0;
		}
		optimisedLayout = settings.getInt(KEY_OPTIMISED_LAYOUT, defaultOptimisation);
	}
	
	public void write() {
		SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		if (log.isDebugEnabled()) {
			log.debug("write prefs", folderSeparator);
		}
		editor.putString(KEY_FOLDER_SEPARATOR, folderSeparator);
		editor.putInt(KEY_DISCLAIMER, disclaimerLastDisplayed);
		editor.putInt(KEY_SHOW_DELETE_ICON, showDeleteIcon);
		editor.putInt(KEY_OPTIMISED_LAYOUT, optimisedLayout);
		editor.commit();
	}
	
	private void setFolderSeparator(String folderSeparator) {
		this.folderSeparator = folderSeparator;
		this.nodeConcatenation = " "+folderSeparator+" ";
	}
	
	public void setNewSeparator(String newSeparator) {
		setFolderSeparator(newSeparator.trim());
	}

	public String getFolderSeparator() {
		return folderSeparator;
	}
	
	public String getNodeConcatenation() {
		return nodeConcatenation;
	}
	
	public int getDisclaimerLastDisplayed() {
		return disclaimerLastDisplayed;
	}

	public boolean isShowDeleteIcon() {
		return showDeleteIcon==1;
	}
	
	public void setShowDeleteIcon(boolean checkboxCheckedState) {
		showDeleteIcon = checkboxCheckedState?1:0;
	}
	
	public boolean isOptimisedLayout() {
		return optimisedLayout==1;
	}
	
	public void setOptimisedLayout(boolean checkboxCheckedState) {
		optimisedLayout = checkboxCheckedState?1:0;
	}
	
	
	public void storeDisclaimerLastDisplayed(int newDisclaimerVersion) {
		disclaimerLastDisplayed = newDisclaimerVersion;
		write();
	}
	
}
