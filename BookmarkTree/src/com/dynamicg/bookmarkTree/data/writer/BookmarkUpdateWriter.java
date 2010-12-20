package com.dynamicg.bookmarkTree.data.writer;

import com.dynamicg.bookmarkTree.BookmarkTreeContext;

import android.content.ContentValues;
import android.provider.Browser;

public class BookmarkUpdateWriter extends BookmarkWriterA {

	public BookmarkUpdateWriter(BookmarkTreeContext ctx) {
		super(ctx);
	}

	public void updateTitle ( Integer bookmarkId, String newTitle ) {
		ContentValues values = new ContentValues();
		values.put(Browser.BookmarkColumns.TITLE, newTitle.toString());
		contenResolver.update ( Browser.BOOKMARKS_URI
				, values
				, Browser.BookmarkColumns._ID+"=?"
				, new String[]{Integer.toString(bookmarkId)}
		);
	}


}
