package com.dynamicg.bookmarkTree.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.dynamicg.bookmarkTree.BookmarkTreeContext;
import com.dynamicg.bookmarkTree.data.TitleTokenizer.TitleItem;
import com.dynamicg.bookmarkTree.model.Bookmark;
import com.dynamicg.bookmarkTree.model.BrowserBookmarkBean;
import com.dynamicg.bookmarkTree.model.FolderBean;
import com.dynamicg.common.main.Logger;

public class BookmarkDataProcessor {

	private static final Logger log = new Logger(BookmarkDataProcessor.class);
	
	private static final boolean MERGE_SHALLOW_FOLDERS = false; // set to true if we want to skip folders with only one entry
	
	private final BookmarkTreeContext ctx;
	private ArrayList<Bookmark> bookmarks = new ArrayList<Bookmark>();
	private int maxLevel;

	public BookmarkDataProcessor(BookmarkTreeContext ctx) {
		this.ctx = ctx;
		log.debug("start");
		ArrayList<BrowserBookmarkBean> rows = BrowserBookmarkLoader.loadBrowserBookmarks(ctx.activity);
		buildTree(rows);
		if (MERGE_SHALLOW_FOLDERS) {
			cleanup();
		}
	}
	
	public ArrayList<Bookmark> getBookmarks() {
		return bookmarks;
	}
	
	private void buildTree(ArrayList<BrowserBookmarkBean> rows) {
		
		String bookmarkTitle;
		FolderBean currentParent;
		int currentLevel;
		final String folderSeparator = ctx.getFolderSeparator();
		
		TitleTokenizer titleTokenizer;
		FolderCache folderCache = new FolderCache();
		
		for (BrowserBookmarkBean bookmark:rows) {
			
			bookmarkTitle = bookmark.getFullTitle();
			
			if (bookmarkTitle==null) {
				// no title - skip
				continue;
			}
			
			if (bookmarkTitle.indexOf(folderSeparator)==-1) {
				// no hierarchy - add as "plain"
				bookmarks.add(bookmark);
				continue;
			}

			// all other cases - resolve folder structure:
			currentParent=null;
			currentLevel=-1;
			titleTokenizer = new TitleTokenizer(bookmarkTitle, folderSeparator, ctx.getNodeConcatenation() );
			for ( int i=0 ; i<titleTokenizer.size()-1 ; i++ ) {
				// loop all but last entries
				currentLevel++;
				currentParent = getOrCreateFolder ( folderCache, currentParent, titleTokenizer.get(i), currentLevel );
			}
			
			// add actual bookmark below last folder
			{
				currentLevel++;
				bookmark.setLevel(currentLevel);
				bookmark.setParentFolder(currentParent);
				bookmark.setNodeTitle(titleTokenizer.getLast().nodeTitle);
				bookmarks.add(bookmark);
				if (currentLevel>maxLevel) {
					maxLevel = currentLevel;
				}
			}

		}
		
	}
	
	public FolderBean getOrCreateFolder ( FolderCache folderCache, FolderBean currentParent
			, TitleTokenizer.TitleItem item, int level) {
		
		// use the "expanded" title for lookup (e.g. "News - SG" and "Weather - SG" has to mapped to different "SG" subfolders) 
		FolderBean folderBean = folderCache.get(item);
		if (folderBean!=null) {
			return folderBean;
		}

		folderBean = new FolderBean(item.fullTitle, item.nodeTitle, level);
		folderBean.setLevel(level);
		folderBean.setParentFolder(currentParent);
		folderCache.put(item, folderBean);
		
		bookmarks.add(folderBean);
		
		return folderBean;
	}
	
	public void cleanup() {
		
		// remove folders with only one child
		// iterate levels descending and (for each level) the bookmarks bottom to top
		HashSet<Bookmark> deletionMap; 
		
		Bookmark bookmark;
		FolderBean parent;
		for ( int ilevel=maxLevel;ilevel>=0;ilevel--) {

			deletionMap = new HashSet<Bookmark>(); 
			
			for ( int ibookm=bookmarks.size()-1;ibookm>=0;ibookm--) { 
				// loop bottom to top
				
				bookmark = bookmarks.get(ibookm);
				if (bookmark.getLevel()!=ilevel) {
					// different level => skip to next
					continue;
				}
				
				parent = bookmark.getParentFolder();
				if (parent!=null && parent.getChildren().size()==1) {
					// this is the only child => move one level up
					bookmark.attachToGrandparent(ctx);
					deletionMap.add(parent);
					log.debug("deletionMap", ilevel, parent.getDisplayTitle());
				}
				
			}
			
			bookmarks.removeAll(deletionMap);
			
		}
		
	}
	
	private class FolderCache {

		private HashMap<String,FolderBean> foldersByTitle = new HashMap<String, FolderBean>();

		public FolderBean get(TitleItem titleItem) {
			return foldersByTitle.get(titleItem.fullTitle);
		}
		
		public void put(TitleItem titleItem, FolderBean folderBean) {
			foldersByTitle.put(titleItem.fullTitle, folderBean);
		}
		
	}
	
}
