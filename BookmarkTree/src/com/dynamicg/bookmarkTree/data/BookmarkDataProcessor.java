package com.dynamicg.bookmarkTree.data;

import java.util.ArrayList;
import java.util.HashMap;

import com.dynamicg.bookmarkTree.BookmarkTreeContext;
import com.dynamicg.bookmarkTree.data.TitleTokenizer.TitleItem;
import com.dynamicg.bookmarkTree.model.Bookmark;
import com.dynamicg.bookmarkTree.model.BrowserBookmarkBean;
import com.dynamicg.bookmarkTree.model.FolderBean;
import com.dynamicg.common.Logger;

public class BookmarkDataProcessor {

	private static final Logger log = new Logger(BookmarkDataProcessor.class);
	
	private final BookmarkTreeContext ctx;
	private final BookmarkSortCache bookmarkSortCache;
	public final int numBrowserBookmarks;
	private int maxLevel;

	public BookmarkDataProcessor(BookmarkTreeContext ctx) {
		this.ctx = ctx;
		this.bookmarkSortCache = BookmarkSortCache.createInstance();
		if (log.isDebugEnabled) {
			log.debug("start");
		}
		ArrayList<BrowserBookmarkBean> rows = BrowserBookmarkLoader.forListAdapter(ctx);
		numBrowserBookmarks = rows.size();
		buildTree(rows);
	}
	
	public ArrayList<Bookmark> getBookmarks() {
		return bookmarkSortCache.getList();
	}
	
	private void buildTree(ArrayList<BrowserBookmarkBean> rows) {
		
		String bookmarkTitle;
		FolderBean currentParent;
		int currentLevel;
		final String folderSeparator = ctx.getFolderSeparator();
		final String nodeConcatenation = ctx.getNodeConcatenation();
		
		final FolderCache folderCache = new FolderCache();
		TitleTokenizer titleTokenizer;
		
		for (BrowserBookmarkBean bookmark:rows) {
			
			bookmarkTitle = bookmark.getFullTitle();
			
			if (bookmarkTitle==null) {
				// no title - skip
				continue;
			}
			
			if (bookmarkTitle.indexOf(folderSeparator)==-1) {
				// no hierarchy - add as "plain"
				bookmarkSortCache.addBookmark(bookmark);
				continue;
			}

			// all other cases - resolve folder structure:
			currentParent=null;
			currentLevel=-1;
			titleTokenizer = new TitleTokenizer(bookmarkTitle, folderSeparator, nodeConcatenation );
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
				bookmarkSortCache.addBookmark(bookmark);
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
		
		bookmarkSortCache.addFolder(folderBean);
		
		return folderBean;
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
