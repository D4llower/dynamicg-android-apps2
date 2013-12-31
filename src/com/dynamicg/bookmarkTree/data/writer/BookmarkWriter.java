package com.dynamicg.bookmarkTree.data.writer;

import android.content.ContentValues;
import android.net.Uri;
import android.provider.Browser;

import com.dynamicg.bookmarkTree.BookmarkTreeContext;
import com.dynamicg.bookmarkTree.chrome.ChromeWrapper;
import com.dynamicg.common.Logger;

public class BookmarkWriter extends BookmarkWriterA {

	private static final Logger log = new Logger(BookmarkWriter.class);

	private final ContentValues values = new ContentValues();

	public BookmarkWriter(BookmarkTreeContext ctx) {
		super(ctx);
	}

	private void doUpdate(int bookmarkId) {

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

	public void updateTitle(int bookmarkId, String title) {
		if (ChromeWrapper.isKitKat()) {
			ChromeWrapper.getKitKatInstance().saveTitle(bookmarkId, title);
		}
		else {
			values.put(Browser.BookmarkColumns.TITLE, title);
			doUpdate(bookmarkId);
		}
	}

	public void updateTitleAndUrl(int bookmarkId, String title, String url) {
		values.put(Browser.BookmarkColumns.URL, url);
		if (ChromeWrapper.isKitKat()) {
			ChromeWrapper.getKitKatInstance().saveTitle(bookmarkId, title);
		}
		else {
			values.put(Browser.BookmarkColumns.TITLE, title);
		}
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
		if (ChromeWrapper.isKitKat()) {
			String newId = result.getLastPathSegment();
			ChromeWrapper.getKitKatInstance().saveTitle(Integer.parseInt(newId), title);;
		}
	}

	public void deleteBrowserBookmark(int id) {
		if (log.isDebugEnabled) {
			log.debug("delete bookmark", id);
		}
		contentResolver.delete (  UriProvider.DELETE
				, Browser.BookmarkColumns._ID+"=?"
				, new String[]{Integer.toString(id)}
				);
		if (ChromeWrapper.isKitKat()) {
			ChromeWrapper.getKitKatInstance().delete(id);
		}
	}

}
