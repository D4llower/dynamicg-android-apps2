package com.dynamicg.bookmarkTree.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.database.Cursor;
import android.provider.Browser;

import com.dynamicg.bookmarkTree.BookmarkTreeContext;
import com.dynamicg.bookmarkTree.chrome.ChromeWrapper;
import com.dynamicg.bookmarkTree.data.writer.UriProvider;
import com.dynamicg.bookmarkTree.model.BrowserBookmarkBean;
import com.dynamicg.bookmarkTree.model.RawDataBean;
import com.dynamicg.bookmarkTree.prefs.PreferencesWrapper;
import com.dynamicg.bookmarkTree.util.BitmapScaleManager;
import com.dynamicg.common.ErrorNotification;
import com.dynamicg.common.Logger;

public class BrowserBookmarkLoader {

	private static final Logger log = new Logger(BrowserBookmarkLoader.class);

	private static final int FOR_DISPLAY = 1;
	private static final int FOR_INTERNAL_OP = 2; // only for pre KK "change separator"
	private static final int FOR_BACKUP = 3;

	private static String EMPTY = "";

	private static final String SORT_STD = Browser.BookmarkColumns.TITLE;
	private static final String SORT_CASE_INSENSITIVE = Browser.BookmarkColumns.TITLE+" COLLATE NOCASE";

	private static String nvl(String value) {
		// mask nulls - we got one error report with an NPE on bookmark title (?)
		return value==null?EMPTY:value;
	}

	public static List<BrowserBookmarkBean> forListAdapter(BookmarkTreeContext ctx) {
		return readBrowserBookmarks(ctx, FOR_DISPLAY);
	}

	public static List<RawDataBean> forInternalOps(BookmarkTreeContext ctx) {
		return readBrowserBookmarks(ctx, FOR_INTERNAL_OP);
	}

	public static List<RawDataBean> forBackup(BookmarkTreeContext ctx) {
		return readBrowserBookmarks(ctx, FOR_BACKUP);
	}

	private static <E> List<E> readBrowserBookmarks(BookmarkTreeContext ctx, int what) {
		ChromeWrapper chromeWrapper = ChromeWrapper.getInstance();
		chromeWrapper.bmLoadStart(ctx);
		try {
			return readBrowserBookmarksImpl(ctx.activity, what, chromeWrapper);
		}
		catch (Throwable t) {
			if (what==FOR_BACKUP) {
				// backup has its own thread with separate exception handler
				RuntimeException rt = t instanceof RuntimeException ? (RuntimeException)t : new RuntimeException(t);
				throw rt;
			}
			ErrorNotification.notifyError(ctx.activity, "Cannot read bookmarks", t);
			return new ArrayList<E>();
		}
		finally {
			chromeWrapper.bmLoadDone();
		}
	}

	@SuppressWarnings("unchecked")
	private static <E> ArrayList<E> readBrowserBookmarksImpl(Activity main, int what, ChromeWrapper chromeWrapper) {

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
		String sortOrder = ChromeWrapper.isKitKat() ? null : PreferencesWrapper.sortCaseInsensitive.isOn() ? SORT_CASE_INSENSITIVE : SORT_STD;

		Cursor crs = main.managedQuery ( UriProvider.QUERY
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

		if (what==FOR_BACKUP || what==FOR_INTERNAL_OP) {
			RawDataBean bean;
			while ( crs.moveToNext() ) {
				bean = new RawDataBean();
				bean.id = crs.getInt(0);
				bean.created = crs.getLong(1);
				bean.fullTitle = nvl(crs.getString(2));
				bean.url = nvl(crs.getString(3));
				bean.favicon = crs.getBlob(4);

				rows.add((E)bean);
				if (log.isTraceEnabled) {
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
				chromeWrapper.bmLoadProcess(bean);
				if (what==FOR_DISPLAY) {
					try {
						bean.favicon = BitmapScaleManager.getIcon(crs.getBlob(4));
					}
					catch (java.lang.OutOfMemoryError e) {
						// ignore, leave icon empty (got the occasional report ... maybe some very large favicon?)
					}
				}

				rows.add((E)bean);
				if (log.isTraceEnabled) {
					log.debug("loadBrowserBookmarks", bean.id, bean.fullTitle, bean.url);
				}
			}
			if (ChromeWrapper.isKitKat()) {
				kkSort((List<BrowserBookmarkBean>)rows, PreferencesWrapper.sortCaseInsensitive.isOn());
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

	private static void kkSort(final List<BrowserBookmarkBean> rows, final boolean sortCaseInsensitive) {
		Comparator<BrowserBookmarkBean> comparator = new Comparator<BrowserBookmarkBean>() {
			@Override
			public int compare(BrowserBookmarkBean lhs, BrowserBookmarkBean rhs) {
				if (sortCaseInsensitive) {
					return lhs.fullTitle.compareToIgnoreCase(rhs.fullTitle);
				}
				return lhs.fullTitle.compareTo(rhs.fullTitle);
			}
		};
		Collections.sort(rows, comparator);
	}

}
