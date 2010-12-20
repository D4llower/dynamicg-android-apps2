package com.dynamicg.bookmarkTree.data.writer;

import java.util.ArrayList;

import android.content.ContentResolver;

import com.dynamicg.bookmarkTree.BookmarkTreeContext;
import com.dynamicg.bookmarkTree.data.BrowserBookmarkLoader;
import com.dynamicg.bookmarkTree.model.BrowserBookmarkBean;

public abstract class BookmarkWriterA {

	protected final BookmarkTreeContext ctx;
	protected final ContentResolver contenResolver;

	public BookmarkWriterA(BookmarkTreeContext ctx) {
		this.ctx = ctx;
		contenResolver = ctx.activity.getContentResolver(); 
	}
	
	public static ArrayList<BrowserBookmarkBean> getBrowserBookmarks(BookmarkTreeContext ctx) {
		 return BrowserBookmarkLoader.loadBrowserBookmarks(ctx.activity);
	}
	
}
