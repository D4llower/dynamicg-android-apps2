package com.dynamicg.bookmarkTree.data.writehandler;

import java.util.List;

import com.dynamicg.bookmarkTree.BookmarkTreeContext;
import com.dynamicg.bookmarkTree.data.BrowserBookmarkLoader;
import com.dynamicg.bookmarkTree.data.writer.BookmarkWriter;
import com.dynamicg.bookmarkTree.model.RawDataBean;
import com.dynamicg.common.Logger;
import com.dynamicg.common.StringUtil;

public class SeparatorChangedHandler {

	private static final Logger log = new Logger(SeparatorChangedHandler.class);

	private final BookmarkTreeContext ctx;
	private final String oldSeparator;
	private final String newSeparator;
	private final BookmarkWriter bookmarkUpdater;

	public SeparatorChangedHandler(BookmarkTreeContext ctx, String oldSeparator, String newSeparator) {
		this.ctx = ctx;
		this.oldSeparator = oldSeparator;
		this.newSeparator = newSeparator;
		this.bookmarkUpdater = new BookmarkWriter(ctx);
		fullProcess();
	}

	private void fullProcess() {
		List<RawDataBean> browserBookmarks = BrowserBookmarkLoader.forInternalOps(ctx);
		for (RawDataBean bm:browserBookmarks) {
			update(bm);
		}
	}

	private void update(RawDataBean bm) {
		String oldTitle = bm.fullTitle;
		String newTitle = StringUtil.replaceAll(oldTitle, oldSeparator, newSeparator);
		if (newTitle==null||newTitle.trim().length()==0||newTitle.equals(oldTitle)) {
			// skip
			if (log.isDebugEnabled) {
				log.debug("update - skip", oldTitle);
			}
			return;
		}
		if (log.isDebugEnabled) {
			log.debug("update - write", oldTitle, newTitle);
		}
		bookmarkUpdater.updateTitle ( bm.id, newTitle );
	}

}
