package com.dynamicg.bookmarkTree.data.writer;

import java.util.ArrayList;

import android.content.ContentValues;
import android.provider.Browser;

import com.dynamicg.bookmarkTree.BookmarkTreeContext;
import com.dynamicg.bookmarkTree.data.BrowserBookmarkLoader;
import com.dynamicg.bookmarkTree.model.RawDataBean;

public class AlphaSortWriter extends BookmarkWriterA {

	// same "created" values as "browser backup&restore"
	private static final int BASE      = 10000000;
	private static final int INCREMENT =  1000000;
	
	private int nextValue;
	
	public AlphaSortWriter(BookmarkTreeContext ctx) {
		super(ctx);
		ArrayList<RawDataBean> bookmarks = BrowserBookmarkLoader.forInternalOps(ctx);
		// set new "CREATED" values descending (i.e. newest has highest value)
		nextValue = BASE + bookmarks.size()*INCREMENT;
		for (RawDataBean bm:bookmarks) {
			nextValue = nextValue - INCREMENT;
			setCreationDate(bm.id);
		}
	}

	protected void setCreationDate(int bookmarkId) {
		ContentValues values = new ContentValues();
		values.put(Browser.BookmarkColumns.CREATED, nextValue);
		contentResolver.update ( Browser.BOOKMARKS_URI
				, values
				, Browser.BookmarkColumns._ID+"=?"
				, new String[]{Integer.toString(bookmarkId)}
		);
	}

}
