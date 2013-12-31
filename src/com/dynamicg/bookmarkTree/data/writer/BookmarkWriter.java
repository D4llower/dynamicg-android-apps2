package com.dynamicg.bookmarkTree.data.writer;

import android.content.ContentValues;
import android.net.Uri;
import android.provider.Browser;

import com.dynamicg.bookmarkTree.BookmarkTreeContext;
import com.dynamicg.common.Logger;

public class BookmarkWriter extends BookmarkWriterA {

	private static final Logger log = new Logger(BookmarkWriter.class);

	private final ContentValues values = new ContentValues();

	public BookmarkWriter(BookmarkTreeContext ctx) {
		super(ctx);
	}

	private void doUpdate(Integer bookmarkId) {

		if (log.isDebugEnabled) {
			log.debug("doUpdate ...", bookmarkId, values);
		}
		int result = contentResolver.update ( UriProvider.UPDATE
				, values
				, Browser.BookmarkColumns._ID+"=?"
				, new String[]{Integer.toString(bookmarkId)}
				);
		if (log.isDebugEnabled) {
			log.debug("doUpdate done", result);
		}
	}

	public void updateTitle(Integer bookmarkId, String title) {
		values.put(Browser.BookmarkColumns.TITLE, title);
		doUpdate(bookmarkId);
	}

	public void updateTitleAndUrl(Integer bookmarkId, String title, String url) {
		values.put(Browser.BookmarkColumns.TITLE, title);
		values.put(Browser.BookmarkColumns.URL, url);
		doUpdate(bookmarkId);
	}

	public void insert(String title, String url) {
		if (log.isDebugEnabled) {
			log.debug("create bookmark", title, url);
		}
		values.put(Browser.BookmarkColumns.TITLE, title);
		values.put(Browser.BookmarkColumns.URL, url);
		values.put(Browser.BookmarkColumns.BOOKMARK, 1);
		Uri result = contentResolver.insert (  UriProvider.INSERT, values );
		if (log.isDebugEnabled) {
			log.debug("row created", result);
		}
	}

	public void deleteBrowserBookmark(Integer id) {
		if (log.isDebugEnabled) {
			log.debug("delete bookmark", id);
		}
		contentResolver.delete (  UriProvider.DELETE
				, Browser.BookmarkColumns._ID+"=?"
				, new String[]{Integer.toString(id)}
				);
	}

}
