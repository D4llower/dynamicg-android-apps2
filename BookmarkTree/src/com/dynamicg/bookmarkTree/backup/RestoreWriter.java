package com.dynamicg.bookmarkTree.backup;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.provider.Browser;

import com.dynamicg.bookmarkTree.BookmarkTreeContext;
import com.dynamicg.bookmarkTree.model.RawDataBean;
import com.dynamicg.common.Logger;

public class RestoreWriter {

	private static final Logger log = new Logger(RestoreWriter.class);

	private static final Uri BOOKMARKS_URI = android.provider.Browser.BOOKMARKS_URI;
	
	private static ContentValues[] transform(ArrayList<RawDataBean> rows) {
		ArrayList<ContentValues> list = new ArrayList<ContentValues>();
		ContentValues entry;
		for (RawDataBean b:rows) {
			entry = new ContentValues();
			
			entry.put(Browser.BookmarkColumns.BOOKMARK, 1);
			entry.put(Browser.BookmarkColumns.CREATED, b.created);
			entry.put(Browser.BookmarkColumns.TITLE, b.fullTitle);
			entry.put(Browser.BookmarkColumns.URL, b.url);
			entry.put(Browser.BookmarkColumns.FAVICON, b.favicon);
			
			if (log.debugEnabled) {
				log.debug("put item", b.fullTitle, b.url, b.favicon!=null?b.favicon.length:"-1");
			}
			list.add(entry);
		}
		return list.toArray(new ContentValues[]{});
	}
	
	public static void replaceFull(BookmarkTreeContext ctx, ArrayList<RawDataBean> rows) 
	throws Exception {
		
		ContentResolver contentResolver = ctx.activity.getContentResolver(); 
		
		// prepare new rows
		ContentValues[] newValues = transform(rows);
		
		// delete existing entries
		contentResolver.delete ( BOOKMARKS_URI
				, Browser.BookmarkColumns.BOOKMARK+"=1"
				, new String[]{}
		);
		
		// insert
		contentResolver.bulkInsert(BOOKMARKS_URI, newValues);
		
	}
	
}
