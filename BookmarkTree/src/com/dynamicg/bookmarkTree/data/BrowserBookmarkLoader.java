package com.dynamicg.bookmarkTree.data;

import java.util.ArrayList;

import android.app.Activity;
import android.database.Cursor;
import android.provider.Browser;

import com.dynamicg.bookmarkTree.BookmarkTreeContext;
import com.dynamicg.bookmarkTree.model.BrowserBookmarkBean;
import com.dynamicg.bookmarkTree.model.RawDataBean;
import com.dynamicg.bookmarkTree.prefs.PreferencesWrapper;
import com.dynamicg.bookmarkTree.util.BitmapScaleManager;
import com.dynamicg.common.ErrorNotification;
import com.dynamicg.common.Logger;

public class BrowserBookmarkLoader {

	private static final Logger log = new Logger(BrowserBookmarkLoader.class);

	private static final int FOR_DISPLAY = 1;
	private static final int FOR_INTERNAL_OP = 2;
	private static final int FOR_BACKUP_RESTORE = 3;
	
	private static String EMPTY = "";
	
	private static final String SORT_STD = Browser.BookmarkColumns.TITLE;
	private static final String SORT_CASE_INSENSITIVE = Browser.BookmarkColumns.TITLE+" COLLATE NOCASE";
	
	private static String nvl(String value) {
		// mask nulls - we got one error report with an NPE on bookmark title (?)
		return value==null?EMPTY:value;
	}
	
	public static ArrayList<BrowserBookmarkBean> forListAdapter(BookmarkTreeContext ctx) {
		return readBrowserBookmarks(ctx.activity, FOR_DISPLAY);
	}
	
	public static ArrayList<RawDataBean> forInternalOps(BookmarkTreeContext ctx) {
		return readBrowserBookmarks(ctx.activity, FOR_INTERNAL_OP);
	}
	
	public static ArrayList<RawDataBean> forBackup(BookmarkTreeContext ctx) {
		return readBrowserBookmarks(ctx.activity, FOR_BACKUP_RESTORE);
	}
	
	private static <E> ArrayList<E> readBrowserBookmarks(Activity main, int what) {
		try {
			return readBrowserBookmarksImpl(main, what);
		}
		catch (Throwable t) {
			ErrorNotification.notifyError(main, "Error: Cannot read bookmarks", t);
			return new ArrayList<E>();
		}
	}
	
	@SuppressWarnings("unchecked")
	private static <E> ArrayList<E> readBrowserBookmarksImpl(Activity main, int what) {

		String[] columns = new String[] {
				Browser.BookmarkColumns._ID
				, Browser.BookmarkColumns.CREATED
				, Browser.BookmarkColumns.TITLE
				, Browser.BookmarkColumns.URL
				, Browser.BookmarkColumns.FAVICON
		};
		
		// query on bookmarks only, skip history
		String query = Browser.BookmarkColumns.BOOKMARK+"=1"; 
		
		// order by, optionally case-insensitive
		String sortOrder = PreferencesWrapper.sortCaseInsensitive.isOn() ? SORT_CASE_INSENSITIVE : SORT_STD;
		
		Cursor crs = main.managedQuery ( android.provider.Browser.BOOKMARKS_URI
				, columns
				, query
				, null
				, sortOrder
		);

		ArrayList<E> rows = new ArrayList<E>();
		
		// see error report "Aug 13, 2010 10:19:37 PM"
		if (crs==null) {
			return rows;
		}
		
		if (what==FOR_BACKUP_RESTORE || what==FOR_INTERNAL_OP) {
			RawDataBean bean;
			while ( crs.moveToNext() ) {
				bean = new RawDataBean();
				bean.id = crs.getInt(0);
				bean.created = crs.getLong(1);
				bean.fullTitle = nvl(crs.getString(2));
				bean.url = nvl(crs.getString(3));
				bean.favicon = crs.getBlob(4);
				
				rows.add((E)bean);
				if (log.traceEnabled) {
					log.debug("loadBrowserBookmarks", bean.fullTitle, bean.url, bean.created);
				}
			}
		}
		else {
			// display
			BrowserBookmarkBean bean;
			while ( crs.moveToNext() ) {
				bean = new BrowserBookmarkBean(); 
				bean.id = crs.getInt(0);
				bean.fullTitle = nvl(crs.getString(2));
				bean.url = nvl(crs.getString(3));
				if (what==FOR_DISPLAY) {
					bean.favicon = BitmapScaleManager.getIcon(crs.getBlob(4));
				}
				
				rows.add((E)bean);
				if (log.traceEnabled) {
					log.debug("loadBrowserBookmarks", bean.id, bean.fullTitle, bean.url);
				}
			}
		}
		
		/*
		 * we don't close the cursor, this will hopefully solve this one:
		 * java.lang.RuntimeException: Unable to resume activity {com.dynamicg.bookmarkTree/com.dynamicg.bookmarkTree.Main}: java.lang.IllegalStateException: trying to requery an already closed cursor
		 * 
		 * if this does not work we should change from "managedQuery" to ContentResolver
		 */
//		if (!crs.isClosed()) {
//			crs.close();
//		}
		
		
		return rows;
	}
	
}
