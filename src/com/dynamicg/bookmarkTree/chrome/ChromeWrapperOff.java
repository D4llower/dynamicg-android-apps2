package com.dynamicg.bookmarkTree.chrome;

import com.dynamicg.bookmarkTree.BookmarkTreeContext;
import com.dynamicg.bookmarkTree.model.BrowserBookmarkBean;

public class ChromeWrapperOff extends ChromeWrapper {
	@Override
	public void bmLoadStart(BookmarkTreeContext ctx) {
	}
	@Override
	public void bmLoadProcess(BrowserBookmarkBean bean) {
	}
	@Override
	public void bmLoadDone() {
	}
}