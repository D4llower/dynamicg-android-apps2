package com.dynamicg.bookmarkTree.data.writer;

import android.content.ContentResolver;

import com.dynamicg.bookmarkTree.BookmarkTreeContext;

public abstract class BookmarkWriterA {

	protected final BookmarkTreeContext ctx;
	protected final ContentResolver contentResolver;

	public BookmarkWriterA(BookmarkTreeContext ctx) {
		this.ctx = ctx;
		contentResolver = ctx.activity.getContentResolver(); 
	}
	
}
