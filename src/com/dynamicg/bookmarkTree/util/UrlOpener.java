package com.dynamicg.bookmarkTree.util;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import com.dynamicg.bookmarkTree.BookmarkTreeContext;
import com.dynamicg.bookmarkTree.R;
import com.dynamicg.common.SimpleAlertDialog;

public class UrlOpener {

	private static final String MIME_TYPE_ALL = "*/*";
	private static final String MIME_TYPE_HTML = "text/html";
	private static final MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

	private final Activity context;
	private final String bookmarkUrl;

	public UrlOpener(BookmarkTreeContext ctx, String url) {
		this.context = ctx.activity;
		this.bookmarkUrl = url!=null ? url.trim() : "";
		open();
	}
	
	private String getDefaultMimeType(String url) {
		String suffix = MimeTypeMap.getFileExtensionFromUrl(url);
		if (suffix==null) {
			return null;
		}
		
		String mimeType = mimeTypeMap.getMimeTypeFromExtension(suffix);
		if (mimeType!=null) {
			return mimeType;
		}
		else if ( suffix.equals("html") || suffix.equals("htm") ) {
			// html is not in the mime type list (?) 
			return MIME_TYPE_HTML;
		}
		else {
			return null;
		}
		
	}
	
	private void startPlainIntent(String url) 
	throws ActivityNotFoundException {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse(url));
		context.startActivity(intent);
	}
	
	private Intent getIntent(String url, String mimeType) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.parse(url), mimeType);
		return intent;
	}
	
	private void open() {
		
		if (bookmarkUrl.length()==0) {
			SimpleAlertDialog.plainInfo(context, R.string.hintEmptyUrl);
			return;
		}
		
		// 1. try default intent
		try {
			startPlainIntent(bookmarkUrl);
			return;
		}
		catch (ActivityNotFoundException e) {}
		
		// 2. prepend "http://" if no protocol provided
		if (!BookmarkUtil.hasProtocol(bookmarkUrl)) {
			try {
				startPlainIntent ( BookmarkUtil.patchProtocol(bookmarkUrl) );
				return;
			}
			catch (ActivityNotFoundException e) {}
		}
		
		// 3. link open issues -> try to derive mime type
		String mimeType = getDefaultMimeType(bookmarkUrl);
		if (mimeType!=null) {
			try {
				context.startActivity(getIntent(bookmarkUrl, mimeType));
				return;
			}
			catch (ActivityNotFoundException e) {}
		}
		
		// 4. last try - open with "*/*" mime type and intent chooser
		context.startActivity(Intent.createChooser ( getIntent(bookmarkUrl, MIME_TYPE_ALL), "Open:" ));
		
	}
	
}
