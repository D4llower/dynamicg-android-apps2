package com.dynamicg.bookmarkTree.data.writer;

import java.util.ArrayList;

import android.content.ContentValues;
import android.provider.Browser;

import com.dynamicg.bookmarkTree.BookmarkTreeContext;
import com.dynamicg.bookmarkTree.data.BrowserBookmarkLoader;
import com.dynamicg.bookmarkTree.model.RawDataBean;

public class AlphaSortWriter extends BookmarkWriterA {

	// same "created" values as "browser backup&restore"
	private static final long BASE      = 11000000;
	private static final long INCREMENT =  1000000;
	
	private long nextValue;
	
	public AlphaSortWriter(BookmarkTreeContext ctx) {
		super(ctx);
		ArrayList<RawDataBean> bookmarks = BrowserBookmarkLoader.forInternalOps(ctx);
		// set new "CREATED" values descending (i.e. newest has highest value)
		nextValue = BASE + bookmarks.size()*INCREMENT;
		for (RawDataBean bm:bookmarks) {
			nextValue = nextValue - INCREMENT;
			setSortItems(bm.id);
		}
	}

	protected void setSortItems(int bookmarkId) {
		ContentValues values = new ContentValues();
		
		values.put(Browser.BookmarkColumns.CREATED, nextValue);
		values.put(Browser.BookmarkColumns.VISITS, nextValue);
		
		contentResolver.update ( UriProvider.UPDATE
				, values
				, Browser.BookmarkColumns._ID+"=?"
				, new String[]{Integer.toString(bookmarkId)}
		);
	}

}
