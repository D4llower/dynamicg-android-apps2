package com.dynamicg.bookmarkTree.data.writehandler;

import com.dynamicg.bookmarkTree.BookmarkTreeContext;
import com.dynamicg.bookmarkTree.data.writer.BookmarkDeletionWriter;
import com.dynamicg.bookmarkTree.model.Bookmark;
import com.dynamicg.common.main.Logger;

public class BookmarkDeletionHandler {

	private static final Logger log = new Logger(BookmarkDeletionHandler.class);
	private final BookmarkTreeContext ctx;
	
	public BookmarkDeletionHandler(BookmarkTreeContext ctx, Bookmark selectedBookmark) {
		this.ctx = ctx;
		delete(selectedBookmark);
	}

	private void delete(Bookmark selectedBookmark) {
		
		BookmarkDeletionWriter deletion = new BookmarkDeletionWriter(ctx);
		
		if (log.isDebugEnabled()) {
			log.debug("delete", selectedBookmark.getDisplayTitle());
		}
		
		if (selectedBookmark.isFolder()) {
			// folder => delete all browserBookmarks below
			for (Bookmark bm:selectedBookmark.getTree(Bookmark.TYPE_BROWSER_BOOKMARK)) {
				deletion.deleteBrowserBookmark(bm.getId());
			}
		}
		else {
			// browserBookmark => delete
			deletion.deleteBrowserBookmark(selectedBookmark.getId());
		}
	}
	
}
