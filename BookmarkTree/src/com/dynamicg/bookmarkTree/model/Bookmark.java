package com.dynamicg.bookmarkTree.model;

import java.util.Collection;
import java.util.HashSet;

import android.graphics.Bitmap;

import com.dynamicg.bookmarkTree.BookmarkTreeContext;
import com.dynamicg.common.main.Logger;

public abstract class Bookmark {

	private static final Logger log = new Logger(Bookmark.class);
	
	public static final int TYPE_BROWSER_BOOKMARK = 1;
	public static final int TYPE_FOLDER = 2;
	public static final int FILTER_ALL = 3;
	
	private String nodeTitle;
	private FolderBean parentFolder;
	private int level=0;
	private boolean dirtyFolderPath;
	
	public Bookmark() {
	}
	
	public Bookmark(String nodeTitle, int level) {
		this.level = level;
		this.nodeTitle = nodeTitle;
	}

	public abstract String getFullTitle();
	public abstract String getUrl();
	public abstract Integer getId();
	public abstract Bitmap getFavicon();
	public abstract boolean isExpanded();
	public abstract void setExpanded(boolean expanded);
	public abstract Collection<Bookmark> getChildren(); // only for this level - see also getTree()
	public abstract int getBookmarkType();
	
	public final boolean isFolder() {
		return getBookmarkType()==TYPE_FOLDER;
	}
	
	public final boolean isBrowserBookmark() {
		return getBookmarkType()==TYPE_BROWSER_BOOKMARK;
	}
	
	public boolean hasParentFolder() {
		return parentFolder!=null;
	}
	
	public String getDisplayTitle() {
		return this.nodeTitle!=null ? this.nodeTitle : getFullTitle(); 
	}
	
	public void setNodeTitle(String nodeTitle) {
		this.nodeTitle = nodeTitle!=null ? nodeTitle.trim() : "";
	}

	public FolderBean getParentFolder() {
		return parentFolder;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getLevel() {
		return level;
	}

	public void markTitleUpdatePending() {
		if (!isFolder()) {
			return;
		}
		this.dirtyFolderPath = true;
		for (Bookmark bm:getTree(FILTER_ALL)) {
			bm.dirtyFolderPath = true;
		}
	}

	public boolean isDirtyFolderPath() {
		return dirtyFolderPath;
	}
	
	public void setParentFolder(FolderBean newParentFolder) {
		FolderBean oldParentFolder = this.parentFolder;
		if (oldParentFolder!=null) {
			oldParentFolder.removeChild(this);
		}
		if (newParentFolder!=null) {
			newParentFolder.addChild(this);
		}
		this.parentFolder = newParentFolder;
	}

	public boolean isAllParentFoldersExpanded() {
		if (parentFolder==null) {
			// shortcut common case
			return true;
		}
		
		// need to recurse to to top level node
		Bookmark parent=this.parentFolder;
		while (parent!=null) {
			if (!parent.isExpanded()) {
				return false;
			}
			parent = parent.parentFolder;
		}
		return true;
	}

	private void shiftWithChildren(int shift) {
		if (log.isTraceEnabled()) {
			log.debug("shiftWithChildren", this.level, getFullTitle());
		}
		this.level = this.level + shift;
		if (isFolder()) {
			for (Bookmark child:getChildren() ) {
				child.shiftWithChildren(shift);
			}
		}
	}
	
	public void attachToGrandparent(BookmarkTreeContext ctx) {
		// merge title with parent and attach to grandparent 
		this.setNodeTitle(parentFolder.getDisplayTitle() + ctx.getNodeConcatenation() + this.getDisplayTitle() );
		this.setParentFolder(parentFolder.getParentFolder()); 
		// patch indention on item including children
		if (log.isDebugEnabled()) {
			log.debug("attachToGrandparent ----------> shift children", this.level, getFullTitle());
		}
		shiftWithChildren(-1);
	}
	
	private static void collectChildren(Bookmark bm, Collection<Bookmark> allChildren) {
		if (!bm.isFolder()) {
			return;
		}
		// add items on next level
		allChildren.addAll(bm.getChildren());
		// recurse to subfolder
		for ( Bookmark item:bm.getChildren()) {
			collectChildren(item,allChildren);
		}
	}
	
	public Collection<Bookmark> getTree(int typeFilter) {
		Collection<Bookmark> allChildren = new HashSet<Bookmark>();
		collectChildren(this,allChildren);
		
		if (typeFilter==FILTER_ALL) {
			return allChildren;
		}
		
		Collection<Bookmark> filteredChildren = new HashSet<Bookmark>();
		for (Bookmark bm:allChildren) {
			if (bm.getBookmarkType()==typeFilter) {
				filteredChildren.add(bm);
			}
		}
		return filteredChildren;
		
	}
	
	public String rebuildFullTitle(BookmarkTreeContext ctx) {
		
		StringBuffer buffer = new StringBuffer();
		
		/*
		 * navigate to top and build title
		 */
		Bookmark item=this;
		String insertText;
		while (item!=null) {
			insertText = item.getDisplayTitle() + ( buffer.length()>0 ? ctx.getNodeConcatenation() : "" ); 
			buffer.insert ( 0, insertText );
			if (log.isDebugEnabled()) {
				log.debug("prependParentTitle iteration", buffer.toString());
			}
			item = item.getParentFolder();
		}
		
		if (log.isDebugEnabled()) {
			log.debug("getFullTitle", buffer.toString());
		}
		return buffer.toString();
	}
	
}
