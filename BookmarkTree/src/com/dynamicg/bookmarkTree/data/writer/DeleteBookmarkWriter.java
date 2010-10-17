package com.dynamicg.bookmarkTree.data.writer;

import com.dynamicg.bookmarkTree.BookmarkTreeContext;
import com.dynamicg.bookmarkTree.model.Bookmark;
import com.dynamicg.common.main.Logger;

public class DeleteBookmarkWriter extends BookmarkWriterA {

	private static final Logger log = new Logger(DeleteBookmarkWriter.class);
	
	public DeleteBookmarkWriter(BookmarkTreeContext ctx, Bookmark selectedBookmark) {
		super(ctx);
		delete(selectedBookmark);
	}

	private void delete(Bookmark selectedBookmark) {
		
		if (log.isDebugEnabled()) {
			log.debug("delete", selectedBookmark.getDisplayTitle());
		}
		
		if (selectedBookmark.isFolder()) {
			// folder => delete all browserBookmarks below
			for (Bookmark bm:selectedBookmark.getTree(Bookmark.TYPE_BROWSER_BOOKMARK)) {
				deleteBrowserBookmark(this,bm.getId());
			}
		}
		else {
			// browserBookmark => delete
			deleteBrowserBookmark(this,selectedBookmark.getId());
		}
	}
	
}
