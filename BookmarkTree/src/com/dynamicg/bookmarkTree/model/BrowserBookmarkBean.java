package com.dynamicg.bookmarkTree.model;

import java.util.Collection;

import android.graphics.Bitmap;

public class BrowserBookmarkBean extends Bookmark {

	protected Integer id;
	protected String fullTitle;
	protected String url;
	protected Bitmap favicon;
	
	public BrowserBookmarkBean(Integer id, String fullTitle, String url, Bitmap favicon) {
		super();
		this.id = id;
		this.fullTitle = fullTitle;
		this.url = url;
		this.favicon = favicon;
	}

	public Integer getId() {
		return id;
	}

	public String getFullTitle() {
		return fullTitle;
	}

	public String getUrl() {
		return url;
	}

	public Bitmap getFavicon() {
		return favicon;
	}

	@Override
	public boolean isExpanded() {
		return false;
	}

	@Override
	public void setExpanded(boolean expanded) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection<Bookmark> getChildren() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getBookmarkType() {
		return TYPE_BROWSER_BOOKMARK;
	}
	
}
