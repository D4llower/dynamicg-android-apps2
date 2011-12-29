package com.dynamicg.bookmarkTree.data.writer;

import android.net.Uri;
import android.provider.Browser;

import com.dynamicg.common.SystemUtil;

// see https://gist.github.com/CyanogenMod/android_frameworks_base/blob/ics/core/java/android/provider/Browser.java
public class UriProvider {

	public static final Uri QUERY;
	public static final Uri INSERT;
	public static final Uri UPDATE;
	public static final Uri DELETE;
	
	static {
		if (SystemUtil.isHoneycombOrNewer()) {
			// honeycomb + ICS
			Uri plain = Uri.parse("content://browser/bookmarks");
			Uri qualified = Uri.parse("content://com.android.browser/bookmarks");
			QUERY = plain;
			INSERT = plain;
			UPDATE = qualified;
			DELETE = qualified;
		}
		else {
			QUERY = Browser.BOOKMARKS_URI;
			INSERT = Browser.BOOKMARKS_URI;
			UPDATE = Browser.BOOKMARKS_URI;
			DELETE = Browser.BOOKMARKS_URI;
		}
	}
	
}
