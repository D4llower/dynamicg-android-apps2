package com.dynamicg.bookmarkTree;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.dynamicg.bookmarkTree.bitmapScaler.BitmapScaleManager;
import com.dynamicg.bookmarkTree.data.BookmarkManager;
import com.dynamicg.bookmarkTree.prefs.PreferencesWrapper;
import com.dynamicg.bookmarkTree.ui.BookmarkListAdapter;

public class BookmarkTreeContext {

	private static final String PREFS_NAME = "dynamicg.bookmarkTree";
	
	public static SharedPreferences settings;
	
	public final Activity activity;
	public final BookmarkManager bookmarkManager;
	public final BookmarkListAdapter bookmarkListAdapter;
	
	public BookmarkTreeContext(Activity activity) {
		
		if (settings==null) {
			settings = activity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		}
		
    	BitmapScaleManager.init();
    	
    	this.activity = activity;
    	this.bookmarkManager = new BookmarkManager(this);
    	this.bookmarkListAdapter = new BookmarkListAdapter(this);
	}
	
	public String getFolderSeparator() {
		return PreferencesWrapper.separatorPreference.getFolderSeparator();
	}
	
	public String getNodeConcatenation() {
		return PreferencesWrapper.separatorPreference.getNodeConcatenation();
	}
	
	public void reloadAndRefresh() {
		bookmarkManager.reloadData();
		bookmarkListAdapter.redraw();
	}

}
