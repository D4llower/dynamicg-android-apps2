package com.dynamicg.bookmarkTree.data.writer;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.provider.Browser;

import com.dynamicg.bookmarkTree.BookmarkTreeContext;
import com.dynamicg.bookmarkTree.data.BrowserBookmarkLoader;
import com.dynamicg.bookmarkTree.model.BrowserBookmarkBean;
import com.dynamicg.common.main.Logger;

public abstract class BookmarkWriterA {

	private static final Logger log = new Logger(BookmarkWriterA.class);
	
	protected final BookmarkTreeContext ctx;
	protected final ContentResolver contenResolver;

	public BookmarkWriterA(BookmarkTreeContext ctx) {
		this.ctx = ctx;
		contenResolver = ctx.activity.getContentResolver(); 
	}
	
	protected static void updateBookmarkTitle(BookmarkWriterA writer, Integer bookmarkId, String newTitle) {
		ContentValues values = new ContentValues();
		values.put(Browser.BookmarkColumns.TITLE, newTitle.toString());
		writer.contenResolver.update ( Browser.BOOKMARKS_URI
				, values
				, Browser.BookmarkColumns._ID+"=?"
				, new String[]{Integer.toString(bookmarkId)}
		);
	}

	protected static void deleteBrowserBookmark(BookmarkWriterA writer, Integer id) {
		if (log.isDebugEnabled()) {
			log.debug("delete bookmark", id);
		}
		writer.contenResolver.delete ( Browser.BOOKMARKS_URI
				, Browser.BookmarkColumns._ID+"=?"
				, new String[]{Integer.toString(id)}
				);
	}
	
	public ArrayList<BrowserBookmarkBean> getBrowserBookmarks() {
		 return BrowserBookmarkLoader.loadBrowserBookmarks(ctx.activity);
	}

	
	
}
