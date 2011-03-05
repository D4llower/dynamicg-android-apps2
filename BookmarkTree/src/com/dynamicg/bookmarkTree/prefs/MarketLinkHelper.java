package com.dynamicg.bookmarkTree.prefs;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

import com.dynamicg.common.SystemUtil;

public class MarketLinkHelper {

	public static final String APP_URL_MARKET = "market://details?id=com.dynamicg.bookmarkTree";
	public static final String APP_URL_FALLBACK = "http://market.android.com/details?id=com.dynamicg.bookmarkTree";
	
	private static void openUrlIntent(Context context, String url) {
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse(url));
		context.startActivity(i);
	}
	
	private static void openMarketIntent(Context context, String url, String fallbackUrl) {
		try {
			openUrlIntent(context, url);
		}
		catch (ActivityNotFoundException e) {
			// Android Market not installed? try with HTTP url
			SystemUtil.toastShort(context, "Cannot open Android Market app (?)");
			openUrlIntent(context, fallbackUrl);
		}
	}

	public static View.OnClickListener getMarketAppLink(final Context context) {
		return new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				openMarketIntent(context, APP_URL_MARKET, APP_URL_FALLBACK);
			}
		};
	}
	
}
