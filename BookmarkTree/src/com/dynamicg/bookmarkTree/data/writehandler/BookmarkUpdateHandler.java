package com.dynamicg.bookmarkTree.data.writehandler;

import java.util.Collection;

import com.dynamicg.bookmarkTree.BookmarkTreeContext;
import com.dynamicg.bookmarkTree.data.writer.BookmarkUpdateWriter;
import com.dynamicg.bookmarkTree.model.Bookmark;
import com.dynamicg.bookmarkTree.model.BrowserBookmarkBean;
import com.dynamicg.bookmarkTree.model.FolderBean;
import com.dynamicg.common.main.Logger;

public class BookmarkUpdateHandler {

	private static final Logger log = new Logger(BookmarkUpdateHandler.class);
	
	private final BookmarkTreeContext ctx;
	
	public BookmarkUpdateHandler(BookmarkTreeContext ctx) {
		this.ctx = ctx;
	}

	private void updateBookmark(Bookmark bm, boolean urlChanged) {
		String newTitle = bm.rebuildFullTitle(ctx);
		BookmarkUpdateWriter bookmarkUpdater = new BookmarkUpdateWriter(ctx);
		if (log.isDebugEnabled()) {
			log.debug("updateBookmark", urlChanged, newTitle, bm.getUrl());
		}
		if (urlChanged) {
			bookmarkUpdater.updateTitleAndUrl(bm.getId(), newTitle, bm.getUrl());
		}
		else {
			bookmarkUpdater.updateTitle(bm.getId(), newTitle);
		}
	}

	public void update(Bookmark bookmark, String newNodeTitle, FolderBean newParentFolder, String newUrl) {
		
		if (log.isDebugEnabled()) {
			log.debug("update", newNodeTitle, newParentFolder);
		}
		
		// prevent update to empty string
		if (newNodeTitle==null || newNodeTitle.trim().length()==0) {
			newNodeTitle=bookmark.getDisplayTitle();
		}
		
		newNodeTitle = newNodeTitle.trim();
		newUrl = newUrl.trim();
		
		boolean titleChanged = !newNodeTitle.equals(bookmark.getDisplayTitle());
		boolean parentFolderChanged = newParentFolder!=bookmark.getParentFolder();
		boolean urlChanged = bookmark.isBrowserBookmark() && !newUrl.equals(bookmark.getUrl());
		
		if (log.isDebugEnabled()) {
			log.debug("changeflags T/F", titleChanged, parentFolderChanged);
		}
		
		if ( !titleChanged && !parentFolderChanged && !urlChanged ) {
			if (log.isDebugEnabled()) {
				log.debug("no changes - quit");
			}
			return;
		}
		
		if (titleChanged) {
			bookmark.markTitleUpdatePending();
		}
		
		bookmark.setParentFolder(newParentFolder);
		bookmark.setNodeTitle(newNodeTitle);
		if (bookmark.isBrowserBookmark()) {
			((BrowserBookmarkBean)bookmark).setUrl(newUrl);
		}
		
		if (bookmark.isBrowserBookmark()) {
			// for plain bookmarks - recurse to parent to get full title, then update
			if (urlChanged) {
				updateBookmark(bookmark, true);
			}
			else {
				updateBookmark(bookmark, false);
			}
		}
		else if (bookmark.isFolder()){
			/*
			 * folder processing:
			 *  - fetch all children
			 *  - for each build full title and update
			 */
			Collection<Bookmark> children = bookmark.getTree(Bookmark.TYPE_BROWSER_BOOKMARK);
			for (Bookmark child:children) {
				// write back the full new title for each browser bookmark below given folder
				if (log.isDebugEnabled()) {
					log.debug("doUpdate for child", child.getDisplayTitle());
				}
				updateBookmark(child, false);
			}
		}
		
		
	}
	
}
