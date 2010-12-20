package com.dynamicg.bookmarkTree.data.writehandler;

import java.util.ArrayList;

import com.dynamicg.bookmarkTree.BookmarkTreeContext;
import com.dynamicg.bookmarkTree.data.writer.BookmarkWriterA;
import com.dynamicg.bookmarkTree.data.writer.UpdateBookmarkWriter;
import com.dynamicg.bookmarkTree.model.BrowserBookmarkBean;
import com.dynamicg.common.main.Logger;
import com.dynamicg.common.main.StringUtil;

public class SeparatorChangedHandler {

	private static final Logger log = new Logger(SeparatorChangedHandler.class);
	
	private final BookmarkTreeContext ctx;
	private final String oldSeparator;
	private final String newSeparator;
	private final UpdateBookmarkWriter updadateWriter;
	
	public SeparatorChangedHandler(BookmarkTreeContext ctx, String oldSeparator, String newSeparator) {
		this.ctx = ctx;
		this.oldSeparator = oldSeparator;
		this.newSeparator = newSeparator;
		this.updadateWriter = new UpdateBookmarkWriter(ctx);
		fullProcess();
	}

	private void fullProcess() {
		ArrayList<BrowserBookmarkBean> bookmarks = BookmarkWriterA.getBrowserBookmarks(ctx);
		for (BrowserBookmarkBean bm:bookmarks) {
			update(bm);
		}
	}

	private void update(BrowserBookmarkBean bm) {
		String oldTitle = bm.getFullTitle();
		String newTitle = StringUtil.replaceAll(oldTitle, oldSeparator, newSeparator);
		if (newTitle==null||newTitle.trim().length()==0||newTitle.equals(oldTitle)) {
			// skip
			if (log.isDebugEnabled()) {
				log.debug("update - skip", oldTitle);
			}
			return;
		}
		if (log.isDebugEnabled()) {
			log.debug("update - write", oldTitle, newTitle);
		}
		updadateWriter.updateBookmarkTitle(bm.getId(), newTitle);
	}
	
}
