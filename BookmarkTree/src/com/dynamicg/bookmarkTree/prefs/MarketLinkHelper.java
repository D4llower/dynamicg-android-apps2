package com.dynamicg.bookmarkTree.prefs;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

import com.dynamicg.common.SystemUtil;

public class MarketLinkHelper {

	private static final String APP_PKG = "com.dynamicg.bookmarkTree";
	private static final String DONATION_PKG = "com.dynamicg.timerecording.pro";
	
	private static void openUrlIntent(Context context, String url) {
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse(url));
		context.startActivity(i);
	}
	
	private static void openMarketIntent(Context context, String pkg) {
		try {
			openUrlIntent(context, "market://details?id="+pkg);
		}
		catch (ActivityNotFoundException e) {
			// Android Market not installed? try with HTTP url
			SystemUtil.toastShort(context, "Cannot open Android Market app (?)");
			openUrlIntent(context, "http://market.android.com/details?id="+pkg);
		}
	}

	public static View.OnClickListener getMarketAppLink(final Context context) {
		return new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				openMarketIntent(context, APP_PKG);
			}
		};
	}

	public static View.OnClickListener getDonationLink(final Context context) {
		return new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				openMarketIntent(context, DONATION_PKG);
			}
		};
	}
	
}
