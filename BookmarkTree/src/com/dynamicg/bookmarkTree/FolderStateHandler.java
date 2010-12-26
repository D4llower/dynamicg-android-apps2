package com.dynamicg.bookmarkTree;

import java.util.ArrayList;
import java.util.HashSet;

import com.dynamicg.bookmarkTree.model.Bookmark;
import com.dynamicg.common.Logger;

public class FolderStateHandler {

	private static final Logger log = new Logger(FolderStateHandler.class);

	private static HashSet<String> expandedFolders;
	
	static {
		clear();
	}
	
	public static void saveExpandedFolders ( BookmarkTreeContext ctx, ArrayList<Bookmark> bookmarksCache ) {
		HashSet<String> expandedFolders = new HashSet<String>();
		for ( Bookmark bm:bookmarksCache ) {
			if (bm.isExpanded()) {
				if (log.traceEnabled) {
					log.debug("save folderState", bm.getFullTitle());
				}
				expandedFolders.add(bm.getFullTitle());
				if (bm.isDirtyFolderPath()) {
					// if self or parent folder was renamed, rebuild the full text and add
					expandedFolders.add(bm.rebuildFullTitle(ctx));
				}
			}
		}
		FolderStateHandler.expandedFolders = expandedFolders;
	}
	
	public static void clear() {
		expandedFolders = new HashSet<String>();
	}
	
	public static void restore ( ArrayList<Bookmark> bookmarksCache ) {
		System.err.println("expandedFolders:"+expandedFolders.size());
		for ( Bookmark item:bookmarksCache ) {
			if ( item.isFolder() && expandedFolders.contains(item.getFullTitle()) ) {
				if (log.traceEnabled) {
					log.debug("restore folderState", item.getFullTitle());
				}
				item.setExpanded(true);
			}
		}
	}

	public static void folderClicked(Bookmark bm) {
		if (bm.isExpanded()) {
			expandedFolders.add(bm.getFullTitle());
		}
		else {
			expandedFolders.remove(bm.getFullTitle());
		}
	}
	
}
