package com.dynamicg.bookmarkTree.data.writer;

import android.provider.Browser;

import com.dynamicg.bookmarkTree.BookmarkTreeContext;
import com.dynamicg.common.main.Logger;

public class BookmarkDeletionWriter extends BookmarkWriterA {

	private static final Logger log = new Logger(BookmarkDeletionWriter.class);

	public BookmarkDeletionWriter(BookmarkTreeContext ctx) {
		super(ctx);
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
