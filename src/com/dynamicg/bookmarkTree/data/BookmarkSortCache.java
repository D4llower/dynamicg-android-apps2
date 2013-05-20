package com.dynamicg.bookmarkTree.data;

import java.util.ArrayList;
import java.util.Collection;

import com.dynamicg.bookmarkTree.model.Bookmark;
import com.dynamicg.bookmarkTree.model.BrowserBookmarkBean;
import com.dynamicg.bookmarkTree.model.FolderBean;
import com.dynamicg.bookmarkTree.prefs.PreferencesWrapper;
import com.dynamicg.common.Logger;

public abstract class BookmarkSortCache {

	private static final Logger log = new Logger(BookmarkSortCache.class);
	
	public abstract ArrayList<Bookmark> getList();
	public abstract void addBookmark(BrowserBookmarkBean bookmark);
	public abstract void addFolder(FolderBean folder);
	
	public static class AlphaOverall extends BookmarkSortCache {
		public ArrayList<Bookmark> alphaSortedBookmarks = new ArrayList<Bookmark>();
		
		public ArrayList<Bookmark> getList() {
			return alphaSortedBookmarks;
		}

		public void addBookmark(BrowserBookmarkBean bookmark) {
			alphaSortedBookmarks.add(bookmark);
		}

		public void addFolder(FolderBean folder) {
			alphaSortedBookmarks.add(folder);
		}
	}
	
	public static class ByType extends BookmarkSortCache {
		
		private final boolean putFoldersFirst;
		
		private ArrayList<FolderBean> folders = new ArrayList<FolderBean>();
		private ArrayList<BrowserBookmarkBean> topLevelBookmarks = new ArrayList<BrowserBookmarkBean>();
		
		private ArrayList<Bookmark> finalList = new ArrayList<Bookmark>();
		
		public ByType(boolean putFoldersFirst) {
			this.putFoldersFirst = putFoldersFirst;
		}
		
		private void addTopLevelItems() {
			for (BrowserBookmarkBean item:topLevelBookmarks) {
				finalList.add(item);
			}
		}
		
		private void processBookmarks(Collection<Bookmark> children) {
			for (Bookmark child:children) {
				if (child.isBrowserBookmark()) {
					finalList.add(child);
				}
			}
		}
		
		private void processFolders(Collection<Bookmark> children) {
			for (Bookmark child:children) {
				if (child.isFolder()) {
					if (log.isTraceEnabled) {
						log.debug("... do recursion for {"+child+"}");
					}
					recursiveAdd((FolderBean)child);
				}
			}
		}
		
		private void recursiveAdd(FolderBean folder) {
			if (finalList.contains(folder)) {
				return; // already processed through child recursion
			}
			if (log.isTraceEnabled) {
				log.debug("recursiveAdd - folder="+folder);
			}
			finalList.add(folder); // add "self"
			Collection<Bookmark> children = folder.getChildren();
			
			if (putFoldersFirst) {
				processFolders(children);
				processBookmarks(children);
			}
			else {
				processBookmarks(children);
				processFolders(children);
			}
		}
		
		public ArrayList<Bookmark> getList() {
			if (log.isDebugEnabled) {
				log.debug("number of folders", folders.size());
			}
			
			if (!putFoldersFirst) {
				addTopLevelItems();
			}
			for (FolderBean folder:folders) {
				if (log.isTraceEnabled) {
					log.debug("--- loop "+folder+" ["+folder.getChildren().size()+"]");
				}
				recursiveAdd(folder);
			}
			if (putFoldersFirst) {
				addTopLevelItems();
			}
			
			if (log.isDebugEnabled) {
				log.debug("getList()", finalList.size());
			}
			return finalList;
		}
		
		public void addBookmark(BrowserBookmarkBean bookmark) {
			if (log.isTraceEnabled) {
				System.err.println("ADD BK "+bookmark+"/"+bookmark.getParentFolder());
			}
			if (bookmark.getParentFolder()==null) {
				// special handling for top level items
				topLevelBookmarks.add(bookmark);
			}
		}

		public void addFolder(FolderBean folder) {
			if (log.isTraceEnabled) {
				log.debug("--> add folder", folder);
			}
			folders.add(folder);
		}
	}
	
	public static BookmarkSortCache createInstance() {
		int sortOption = PreferencesWrapper.sortOption.value;
		switch (sortOption) {
		case PreferencesWrapper.SORT_FOLDERS_FIRST: return new ByType(true);
		case PreferencesWrapper.SORT_BOOKMARKS_FIRST: return new ByType(false);
		default: return new AlphaOverall();
		}
	}
	
}
