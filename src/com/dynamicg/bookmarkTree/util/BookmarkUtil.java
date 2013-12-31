package com.dynamicg.bookmarkTree.util;

public class BookmarkUtil {

	private static final String IDENTIFIER = "://";
	private static final String PROTOCOL = "http://";
	
	public static String patchProtocol(String url) {
		if (url==null) {
			return null;
		}
		url = url.trim();
		if (!hasProtocol(url)) {
			return PROTOCOL+url;
		}
		else {
			return url;
		}
	}
	
	public static boolean hasProtocol(String url) {
		return url!=null && url.indexOf(IDENTIFIER)>0;
	}
	
}
