package com.dynamicg.bookmarkTree.data.writer;

import java.util.Collection;

import com.dynamicg.bookmarkTree.BookmarkTreeContext;
import com.dynamicg.bookmarkTree.model.Bookmark;
import com.dynamicg.bookmarkTree.model.FolderBean;
import com.dynamicg.common.main.Logger;

public class UpdateBookmarkWriter extends BookmarkWriterA {

	private static final Logger log = new Logger(UpdateBookmarkWriter.class);
	
	public UpdateBookmarkWriter(BookmarkTreeContext ctx) {
		super(ctx);
	}

	private void updateBookmarkTitle(Bookmark bm) {
		String newTitle = bm.rebuildFullTitle(ctx);
		updateBookmarkTitle(this, bm.getId(), newTitle);
	}

	public void update(Bookmark bookmark, String newNodeTitle, FolderBean newParentFolder) {
		
		if (log.isDebugEnabled()) {
			log.debug("update", newNodeTitle, newParentFolder);
		}
		
		// prevent update to empty string
		if (newNodeTitle==null || newNodeTitle.trim().length()==0) {
			newNodeTitle=bookmark.getDisplayTitle();
		}
		
		newNodeTitle = newNodeTitle.trim();
		
		boolean titleChanged = !newNodeTitle.equals(bookmark.getDisplayTitle());
		boolean parentFolderChanged = newParentFolder!=bookmark.getParentFolder();
		
		if (log.isDebugEnabled()) {
			log.debug("changeflags T/F", titleChanged, parentFolderChanged);
		}
		
		if ( !titleChanged && !parentFolderChanged) {
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
			// for plain bookmarks - recurse to parent to get full title, then update
			updateBookmarkTitle(bookmark);
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
				updateBookmarkTitle(child);
			}
		}
		
		
	}
	
}
