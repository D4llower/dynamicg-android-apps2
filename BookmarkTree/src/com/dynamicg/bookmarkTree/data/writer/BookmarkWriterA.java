package com.dynamicg.bookmarkTree.data.writer;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.provider.Browser;

import com.dynamicg.bookmarkTree.BookmarkTreeContext;
import com.dynamicg.bookmarkTree.data.BrowserBookmarkLoader;
import com.dynamicg.bookmarkTree.model.BrowserBookmarkBean;

public abstract class BookmarkWriterA {

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

	public ArrayList<BrowserBookmarkBean> getBrowserBookmarks() {
		 return BrowserBookmarkLoader.loadBrowserBookmarks(ctx.activity);
	}

	
	
}
