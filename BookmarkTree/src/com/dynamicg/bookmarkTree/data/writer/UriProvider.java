package com.dynamicg.bookmarkTree.data.writer;

import android.net.Uri;
import android.provider.Browser;

// see https://gist.github.com/CyanogenMod/android_frameworks_base/blob/ics/core/java/android/provider/Browser.java
public class UriProvider {

	public static final int INSERT = 1;
	public static final int UPDATE = 2;
	public static final int DELETE = 3;
	
	private static final Uri URI_PLAIN;
	private static final Uri URI_QUALIFIED;
	
	static {
		if (android.os.Build.VERSION.SDK_INT >= 11) {
			// honeycomb + ICS
			URI_PLAIN = Uri.parse("content://browser/bookmarks");
			URI_QUALIFIED = Uri.parse("content://com.android.browser/bookmarks");
		}
		else {
			URI_PLAIN = Browser.BOOKMARKS_URI;
			URI_QUALIFIED = Browser.BOOKMARKS_URI;
		}
	}
	
	public static Uri getURI(int action) {
		switch (action) {
		case INSERT: return URI_PLAIN;
		case UPDATE: return URI_QUALIFIED;
		case DELETE: return URI_QUALIFIED;
		default: return URI_PLAIN;
		}
	}
	
}
