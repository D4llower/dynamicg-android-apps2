package com.dynamicg.bookmarkTree.data;

import java.util.ArrayList;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.Browser;

import com.dynamicg.bookmarkTree.model.BrowserBookmarkBean;
import com.dynamicg.common.main.Logger;

public class BrowserBookmarkLoader {

	private static final Logger log = new Logger(BrowserBookmarkLoader.class);

	private static Bitmap getFavicon(byte[] blob) {
		if (blob==null) {
			return null;
		}
		return BitmapFactory.decodeByteArray(blob, 0, blob.length); 
	}

	public static ArrayList<BrowserBookmarkBean> loadBrowserBookmarks(Activity main) {

		Uri bookmarksURI = android.provider.Browser.BOOKMARKS_URI;
		String[] columns = new String[] { Browser.BookmarkColumns._ID
				, Browser.BookmarkColumns.TITLE
				, Browser.BookmarkColumns.URL
				, Browser.BookmarkColumns.FAVICON 
		};
		String query = Browser.BookmarkColumns.BOOKMARK+"=1"; // query on bookmarks only, skip history
		Cursor crs = main.managedQuery ( bookmarksURI
				, columns
				, query
				, null
				, Browser.BookmarkColumns.TITLE
		);

		ArrayList<BrowserBookmarkBean> rows = new ArrayList<BrowserBookmarkBean>();
		
		// see error report "Aug 13, 2010 10:19:37 PM"
		if (crs==null) {
			return rows;
		}
		
		BrowserBookmarkBean row;
		while ( crs.moveToNext() ) {
			row = new BrowserBookmarkBean (  crs.getInt(0)
					, crs.getString(1)
					, crs.getString(2)
					, getFavicon(crs.getBlob(3))
			);
			if (log.isTraceEnabled()) {
				log.debug("loadBrowserBookmarks",row.getFullTitle(),row.getUrl());
			}
			rows.add(row);
		}
		
		if (!crs.isClosed()) {
			crs.close();
		}
		
		return rows;
	}

}
