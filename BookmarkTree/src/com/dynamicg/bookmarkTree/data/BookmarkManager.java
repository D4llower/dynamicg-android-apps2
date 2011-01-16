package com.dynamicg.bookmarkTree.data;

import java.util.ArrayList;

import com.dynamicg.bookmarkTree.BookmarkTreeContext;
import com.dynamicg.bookmarkTree.FolderStateHandler;
import com.dynamicg.bookmarkTree.Main;
import com.dynamicg.bookmarkTree.model.Bookmark;
import com.dynamicg.bookmarkTree.model.FolderBean;
import com.dynamicg.bookmarkTree.prefs.PreferencesWrapper;
import com.dynamicg.common.Logger;

public class BookmarkManager {

	private static final Logger log = new Logger(BookmarkManager.class);
	
	private final BookmarkTreeContext ctx;
	private ArrayList<Bookmark> bookmarksCache;
	private ArrayList<FolderBean> foldersCache;
	private int numBrowserBokmarks;

	public BookmarkManager(BookmarkTreeContext ctx) {
		this.ctx = ctx;
		if (bookmarksCache==null) {
			loadBookmarks();
		}
		else {
			if (log.debugEnabled) {
				log.debug("reuse cache");
			}
		}
		
		if (PreferencesWrapper.keepState.isOn()) {
			if (log.debugEnabled) {
				log.debug("restore folder state");
			}
			FolderStateHandler.restore(this.bookmarksCache);
		}
		
	}
	
	private void loadBookmarks() {
		BookmarkDataProcessor processor = new BookmarkDataProcessor(ctx);
		bookmarksCache = processor.getBookmarks();
		numBrowserBokmarks = processor.numBrowserBookmarks;
		
		foldersCache = new ArrayList<FolderBean>();
		for (Bookmark bm:bookmarksCache) {
			if (bm.isFolder()) {
				foldersCache.add((FolderBean)bm);
			}
		}
		
		if (log.traceEnabled) {
			log.debug("############# bookmarksCache size", bookmarksCache.size());
			
			for (Bookmark item:bookmarksCache) {
				System.err.println("=> "+item);
			}
			
		}
		
	}
	
	public ArrayList<Bookmark> getAllBookmarks() {
		return bookmarksCache;
	}

	public ArrayList<FolderBean> getAllFolders() {
		return foldersCache;
	}

	private void setAllFolderStates(boolean doExpand) {
		for ( Bookmark bm:bookmarksCache ) {
			if (bm.isFolder() && bm.isExpanded()!=doExpand) {
				bm.setExpanded(doExpand);
			}
		}
	}

	public void toggleFolders(int action) {
		if (action==Main.ACTION_EXPAND_ALL) {
			setAllFolderStates(true);
			if (PreferencesWrapper.keepState.isOn()) {
				FolderStateHandler.saveExpandedFolders(ctx, bookmarksCache);
			}
		}
		else if (action==Main.ACTION_COLLAPSE_ALL) {
			setAllFolderStates(false);
			FolderStateHandler.clear();
		}
		else {
			log.warn("undefined toggle action", action);
		}
	}
	
	// on reload we need to save/restore the folder state regardless of the prefs
	public void reloadData() {
		FolderStateHandler.saveExpandedFolders(ctx, this.bookmarksCache);
		loadBookmarks();
		FolderStateHandler.restore(this.bookmarksCache);
	}

	public ArrayList<Bookmark> getPresentationList() {
		ArrayList<Bookmark> list = new ArrayList<Bookmark>();
		for (Bookmark bm:bookmarksCache) {
			if ( bm.getLevel()==0 || bm.isAllParentFoldersExpanded() ) {
				list.add(bm);
			}
		}
		return list;
	}

	public int getNumberOfBookmarks() {
		return numBrowserBokmarks;
	}
}
