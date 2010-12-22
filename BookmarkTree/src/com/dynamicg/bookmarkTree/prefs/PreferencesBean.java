package com.dynamicg.bookmarkTree.prefs;

public class PreferencesBean {

	protected String folderSeparator;
	protected String nodeConcatenation;
	protected int disclaimerLastDisplayed;
	protected int optimisedLayout;
	protected int listStyle;
	protected int sortOption;
	protected int keepState;
	
	public String getFolderSeparator() {
		return folderSeparator;
	}
	public String getNodeConcatenation() {
		return nodeConcatenation;
	}
	public int getDisclaimerLastDisplayed() {
		return disclaimerLastDisplayed;
	}
	public int getListStyle() {
		return listStyle;
	}
	public int getSortOption() {
		return sortOption;
	}
	public void setListStyle(int listStyle) {
		this.listStyle = listStyle;
	}
	public void setSortOption(int sortOption) {
		this.sortOption = sortOption;
	}
	public void setKeepState(int keepState) {
		this.keepState = keepState;
	}
	
}
