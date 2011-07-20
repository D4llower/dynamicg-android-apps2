package com.dynamicg.bookmarkTree.dialogs;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.net.Uri;

import com.dynamicg.bookmarkTree.R;
import com.dynamicg.common.ContextUtil;
import com.dynamicg.common.Logger;

public class ShortcutCreateWorker {
	
	private static final Logger log = new Logger(ShortcutCreateWorker.class);
	private static final String LAUNCH_ACTION = "com.android.launcher.action.INSTALL_SHORTCUT";
	private static final int CORNER_DIM = 8;
	
	private final Context context;
	
	public ShortcutCreateWorker(Context context) {
		this.context = context;
	}
	
	public Bitmap getIcon(Bitmap originalFavicon, int bgcolor, final float faviconTargetDensity) {
		
		// see http://developer.android.com/guide/topics/graphics/index.html
		final int shortcutSizeScaled = ContextUtil.getDimension(context, R.dimen.shortcutIconSize);
		final Bitmap shortcutBitmap = Bitmap.createBitmap ( shortcutSizeScaled, shortcutSizeScaled, Bitmap.Config.ARGB_8888);
		final float shortcutDensity = shortcutBitmap.getDensity();
		
		final Canvas canvas = new Canvas();
		
		canvas.setBitmap(shortcutBitmap);
		
		// solid background
		RectF rect = new RectF(0,0,shortcutSizeScaled,shortcutSizeScaled);
		Paint paint = new Paint();
		paint.setColor(bgcolor);
		paint.setStyle(Style.FILL);
		
		int roundedCorner = ContextUtil.getScaledSizeInt(context, CORNER_DIM);
		canvas.drawRoundRect(rect, roundedCorner, roundedCorner, paint);
		
		if (originalFavicon==null) {
			// prevent NPE
			return shortcutBitmap;
		}
		
		// scale favicon
		// => note we copy the icon first as we're going to overwrite the density
		Bitmap favicon;
		try {
			favicon = originalFavicon.copy(originalFavicon.getConfig(), true);
		}
		catch (NullPointerException npe) {
			return shortcutBitmap; // we got a few NPE reports. probably getConfig returns null occasionally?
		}
		
		favicon.setDensity((int)faviconTargetDensity);
		
		/*
		 * draw centered
		 */
		final float faviconW = favicon.getWidth();
		final float faviconH = favicon.getHeight();
		final float scalePatch = shortcutDensity / faviconTargetDensity;
		final float faviconPatchedW = faviconW * scalePatch;
		final float faviconPatchedH = faviconH * scalePatch ;
		
		final float xOffset = (float)(shortcutSizeScaled - faviconPatchedW) / 2f;
		final float yOffset = (float)(shortcutSizeScaled - faviconPatchedH) / 2f;
		
		if (log.debugEnabled) {
			log.debug("--> dimensions");
			log.debug("shortcutSizeScaled", shortcutSizeScaled);
			log.debug("favicon width/height", faviconW, faviconH);
			log.debug("favicon patched width/height", faviconPatchedW, faviconPatchedH);
			log.debug("shortcutDensity, faviconTargetDensity, scalePatch ", shortcutDensity, faviconTargetDensity, scalePatch);
			log.debug("offset", xOffset, yOffset);
		}

		canvas.drawBitmap ( favicon
				, xOffset
				, yOffset
				, null
				);
		
		return shortcutBitmap;
		
	}
	
	public void create(Bitmap icon, String title, String url) {
		
		/*
		 * the actual shortcut action (i.e. open web link) 
		 */
		Intent shortcutIntent = new Intent(Intent.ACTION_VIEW);
		shortcutIntent.setData(Uri.parse(url));
		
		/*
		 * shortcut creation intent
		 */
		Intent intent = new Intent();
		intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
		intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, title);
		intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, icon);
		intent.setAction(LAUNCH_ACTION);
		context.sendBroadcast(intent);
		
	}

}
