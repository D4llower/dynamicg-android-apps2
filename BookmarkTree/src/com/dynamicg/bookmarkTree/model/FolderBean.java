package com.dynamicg.bookmarkTree.model;

import java.util.ArrayList;
import java.util.Collection;

import android.graphics.Bitmap;

public class FolderBean extends Bookmark {

	public static final FolderBean ROOT;
	static {
		ROOT = new FolderBean("<no folder>","",-1);
	}
	
	private String fullTitle;
	private boolean expanded = false;
	private ArrayList<Bookmark> children = new ArrayList<Bookmark>();
	
	public FolderBean(String fullTitle, String nodeTitle, int level) {
		super(nodeTitle, level);
		this.fullTitle = fullTitle;
	}
	
	public String toString() {
		return fullTitle; // for Spinner
	}
	
	public void addChild(Bookmark child) {
		children.add(child);
	}
	
	public void removeChild(Bookmark child) {
		children.remove(child);
	}
	
	@Override
	public Bitmap getFavicon() {
		return null; // icon is hardcoded in layout
	}
	@Override
	public String getUrl() {
		return null; // no url
	}

	@Override
	public boolean isExpanded() {
		return expanded;
	}

	@Override
	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}

	@Override
	public String getFullTitle() {
		return fullTitle;
	}
	
	@Override
	public Integer getId() {
		throw new UnsupportedOperationException();
	}
	
	public Collection<Bookmark> getChildren() {
		return children;
	}

	@Override
	public int getBookmarkType() {
		return TYPE_FOLDER;
	}

}
