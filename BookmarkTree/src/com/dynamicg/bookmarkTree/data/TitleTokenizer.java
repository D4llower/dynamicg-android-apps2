package com.dynamicg.bookmarkTree.data;

import java.util.ArrayList;

import com.dynamicg.common.Logger;
import com.dynamicg.common.StringUtil;

public class TitleTokenizer {

	private static final Logger log = new Logger(TitleTokenizer.class);
	
	static class TitleItem {
		String fullTitle;
		String nodeTitle;
	}
	
	private ArrayList<TitleItem> items = new ArrayList<TitleItem>();
	
	public TitleTokenizer(String title, String separator, String titleConcatenation) {
		
		if (title==null || title.indexOf(separator)==-1) {
			return;
		}
		
		// remove empty folders
		title = StringUtil.replaceAll(title, separator+separator, separator);
		
		TitleItem item, parent=null;
		String[] tokens = StringUtil.split(title,separator);
		for ( String token:tokens ) {
			if (token==null || token.trim().length()==0 ) {
				continue;
			}
			item = new TitleItem();
			item.nodeTitle = token.trim();
			if (parent==null) {
				item.fullTitle = item.nodeTitle;
			}
			else {
				item.fullTitle = parent.fullTitle + titleConcatenation + item.nodeTitle;
			}
			items.add(item);
			parent = item;
			
			if (log.traceEnabled) {
				log.debug("titleItem", item.nodeTitle, item.fullTitle);
			}
		}
		
		
	}
	
	public ArrayList<TitleItem> getItems() {
		return items;
	}
	
	public int size() {
		return items.size();
	}

	public TitleItem get(int i) {
		return items.get(i);
	}
	
	public TitleItem getLast() {
		return items.get(items.size()-1); // TODO - fix ArrayIndexOutOfBoundsException (?)
	}
	
}
