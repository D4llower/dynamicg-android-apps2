package com.dynamicg.bookmarkTree.data.writer;

import android.content.ContentValues;
import android.provider.Browser;

import com.dynamicg.bookmarkTree.BookmarkTreeContext;

public class BookmarkUpdateWriter extends BookmarkWriterA {

	private final ContentValues values;

	public BookmarkUpdateWriter(BookmarkTreeContext ctx) {
		super(ctx);
		values = new ContentValues();
	}

	private void write(Integer bookmarkId) {
		contenResolver.update ( Browser.BOOKMARKS_URI
				, values
				, Browser.BookmarkColumns._ID+"=?"
				, new String[]{Integer.toString(bookmarkId)}
		);
	}
	
	public void updateTitle(Integer bookmarkId, String title) {
		values.put(Browser.BookmarkColumns.TITLE, title);
		write(bookmarkId);
	}

	public void updateTitleAndUrl(Integer bookmarkId, String title, String url) {
		values.put(Browser.BookmarkColumns.TITLE, title);
		values.put(Browser.BookmarkColumns.URL, url);
		write(bookmarkId);
	}
	
}
