package com.dynamicg.bookmarkTree.data.writer;

import com.dynamicg.bookmarkTree.BookmarkTreeContext;
import com.dynamicg.bookmarkTree.model.BrowserBookmarkBean;
import com.dynamicg.common.main.Logger;
import com.dynamicg.common.main.StringUtil;

public class SeparatorChangedBookmarkWriter extends BookmarkWriterA {

	private static final Logger log = new Logger(SeparatorChangedBookmarkWriter.class);
	
	private final String oldSeparator;
	private final String newSeparator;
	
	public SeparatorChangedBookmarkWriter(BookmarkTreeContext ctx, String oldSeparator, String newSeparator) {
		super(ctx);
		this.oldSeparator = oldSeparator;
		this.newSeparator = newSeparator;
		fullProcess();
	}

	private void fullProcess() {
		for (BrowserBookmarkBean bm:getBrowserBookmarks()) {
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
		updateBookmarkTitle(this, bm.getId(), newTitle);
	}
	
}
