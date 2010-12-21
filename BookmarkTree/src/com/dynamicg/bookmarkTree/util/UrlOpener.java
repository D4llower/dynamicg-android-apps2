package com.dynamicg.bookmarkTree.util;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;

import com.dynamicg.bookmarkTree.BookmarkTreeContext;
import com.dynamicg.bookmarkTree.R;
import com.dynamicg.common.ui.SimpleAlertDialog;

public class UrlOpener {

	private final Activity context;
	private final String bookmarkUrl;

	public UrlOpener(BookmarkTreeContext ctx, String url) {
		this.bookmarkUrl = url!=null ? url.trim() : "";
		this.context = ctx.activity;
		open();
	}
	
	private void alert(final String url) {
		new SimpleAlertDialog(context, R.string.hintNoIntent, R.string.commonClose) {
			@Override
			public String getPlainBodyText() {
				return context.getString(R.string.editUrl) + (url.length()==0?"-":url);
			}
		};
	}
	
	private Intent getIntent(String url) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse(url));
		return intent;
	}
	
	private void open() {
		if (bookmarkUrl.length()==0) {
			alert(bookmarkUrl);
			return;
		}
		
		String url = bookmarkUrl;
		try {
			context.startActivity(getIntent(url));
		}
		catch (ActivityNotFoundException e1) {
			if (!BookmarkUtil.startsWithProtocol(url)) {
				url = BookmarkUtil.patchProtocol(bookmarkUrl);
				try {
					context.startActivity(getIntent(url));
				}
				catch (ActivityNotFoundException e2) {
					alert(url);
				}
			}
		}
		
	}
	
}
