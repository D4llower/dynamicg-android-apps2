package com.dynamicg.bookmarkTree.data.writer;

import android.content.ContentValues;
import android.provider.Browser;

import com.dynamicg.bookmarkTree.BookmarkTreeContext;
import com.dynamicg.common.main.Logger;

public class BookmarkWriter extends BookmarkWriterA {

	private static final Logger log = new Logger(BookmarkWriter.class);
	
	private final ContentValues values;

	public BookmarkWriter(BookmarkTreeContext ctx) {
		super(ctx);
		values = new ContentValues();
	}

	private void doUpdate(Integer bookmarkId) {
		contenResolver.update ( Browser.BOOKMARKS_URI
				, values
				, Browser.BookmarkColumns._ID+"=?"
				, new String[]{Integer.toString(bookmarkId)}
		);
	}
	
	public void updateTitle(Integer bookmarkId, String title) {
		values.put(Browser.BookmarkColumns.TITLE, title);
		doUpdate(bookmarkId);
	}

	public void updateTitleAndUrl(Integer bookmarkId, String title, String url) {
		values.put(Browser.BookmarkColumns.TITLE, title);
		values.put(Browser.BookmarkColumns.URL, url);
		doUpdate(bookmarkId);
	}
	
	public void deleteBrowserBookmark(Integer id) {
		if (log.isDebugEnabled()) {
			log.debug("delete bookmark", id);
		}
		contenResolver.delete ( Browser.BOOKMARKS_URI
				, Browser.BookmarkColumns._ID+"=?"
				, new String[]{Integer.toString(id)}
				);
	}
	
}
