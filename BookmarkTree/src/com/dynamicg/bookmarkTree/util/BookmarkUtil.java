package com.dynamicg.bookmarkTree.util;

public class BookmarkUtil {

	private static final String PROTOCOL = "http://";
	
	public static String patchProtocol(String url) {
		if (url==null) {
			return null;
		}
		url = url.trim();
		if (url.length()>0 && !url.startsWith(PROTOCOL)) {
			return PROTOCOL+url;
		}
		else {
			return url;
		}
	}
	
}
