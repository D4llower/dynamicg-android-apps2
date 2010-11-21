package com.dynamicg.bookmarkTree.data;

import java.util.ArrayList;
import java.util.HashSet;

import com.dynamicg.bookmarkTree.BookmarkTreeContext;
import com.dynamicg.bookmarkTree.Main;
import com.dynamicg.bookmarkTree.model.Bookmark;
import com.dynamicg.bookmarkTree.model.FolderBean;
import com.dynamicg.common.main.Logger;

public class BookmarkManager {

	private static final Logger log = new Logger(BookmarkManager.class);
	
	private final BookmarkTreeContext ctx;
	private ArrayList<Bookmark> bookmarksCache;
	private ArrayList<FolderBean> foldersCache;

	public BookmarkManager(BookmarkTreeContext ctx) {
		this.ctx = ctx;
		if (bookmarksCache==null) {
			loadBookmarks();
		}
		else {
			if (log.isDebugEnabled()) {
				log.debug("reuse cache");
			}
		}
	}
	
	private void loadBookmarks() {
		bookmarksCache = new BookmarkDataProcessor(ctx).getBookmarks();
		
		foldersCache = new ArrayList<FolderBean>();
		for (Bookmark bm:bookmarksCache) {
			if (bm.isFolder()) {
				foldersCache.add((FolderBean)bm);
			}
		}
		
		if (log.isTraceEnabled()) {
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
		}
		else if (action==Main.ACTION_COLLAPSE_ALL) {
			setAllFolderStates(false);
		}
		else {
			log.warn("undefined toggle action", action);
		}
	}
	
	
	private HashSet<String> saveExpandedFolders() {
		HashSet<String> expandedFolders = new HashSet<String>();
		for ( Bookmark bm:bookmarksCache ) {
			if (bm.isExpanded()) {
				if (log.isTraceEnabled()) {
					log.debug("save folderState", bm.getFullTitle());
				}
				expandedFolders.add(bm.getFullTitle());
				if (bm.isDirtyFolderPath()) {
					// if self or parent folder was renamed, rebuild the full text and add
					expandedFolders.add(bm.rebuildFullTitle(ctx));
				}
			}
		}
		
		return expandedFolders;
	}
	
	public void reloadData() {
		HashSet<String> expandedFolders = saveExpandedFolders();
		loadBookmarks();
		
		// restore folder state
		for ( Bookmark item:bookmarksCache ) {
			if ( item.isFolder() && expandedFolders.contains(item.getFullTitle()) ) {
				if (log.isTraceEnabled()) {
					log.debug("restore folderState", item.getFullTitle());
				}
				item.setExpanded(true);
			}
		}
		
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

}
