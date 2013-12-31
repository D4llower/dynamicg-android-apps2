package com.dynamicg.bookmarkTree;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.dynamicg.bookmarkTree.chrome.ChromeWrapper;
import com.dynamicg.bookmarkTree.data.BookmarkManager;
import com.dynamicg.bookmarkTree.prefs.PreferencesWrapper;
import com.dynamicg.bookmarkTree.ui.BookmarkListAdapter;
import com.dynamicg.bookmarkTree.util.BitmapScaleManager;

public class BookmarkTreeContext {

	private static final String PREFS_NAME = "dynamicg.bookmarkTree";

	public static final int SP_LEGACY = 1;
	public static final int SP_CURRENT = 2;

	public static SharedPreferences settings;

	public final Activity activity;
	public final BookmarkManager bookmarkManager;
	public final BookmarkListAdapter bookmarkListAdapter;

	public BookmarkTreeContext(Activity activity) {

		if (settings==null) {
			settings = activity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		}

		PreferencesWrapper.initialSetup();
		BitmapScaleManager.init();
		ChromeWrapper.markPendingMigration(settings);

		this.activity = activity;
		this.bookmarkManager = new BookmarkManager(this);
		this.bookmarkListAdapter = new BookmarkListAdapter(this);
	}

	private String getMatchingToken(String tokenLegacy, String tokenKK, int what) {
		if (what==SP_LEGACY) {
			return tokenLegacy;
		}
		return ChromeWrapper.isKitKat() ? tokenKK : tokenLegacy;
	}

	public String getFolderSeparator(int what) {
		return getMatchingToken(PreferencesWrapper.separatorPreference.getFolderSeparator()
				, PreferencesWrapper.VALUE_KK_SEPARATOR
				, what);
	}

	public String getNodeConcatenation(int what) {
		return getMatchingToken(PreferencesWrapper.separatorPreference.getNodeConcatenation()
				, PreferencesWrapper.VALUE_KK_CONCAT
				, what);
	}

	public void reloadAndRefresh() {
		bookmarkManager.reloadData();
		bookmarkListAdapter.redraw();
	}

}
