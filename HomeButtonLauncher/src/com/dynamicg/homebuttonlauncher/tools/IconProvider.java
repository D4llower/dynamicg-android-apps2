package com.dynamicg.homebuttonlauncher.tools;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;

import com.dynamicg.common.Logger;

// see http://stackoverflow.com/questions/4609456/android-set-drawable-size-programatically
public class IconProvider {

	private static final Logger log = new Logger(IconProvider.class);

	private static final int ICON_SIZE_DP = 48;

	private static Resources resources;
	private static float density;

	public static void init(Context context) {
		resources = context.getResources();
		density = resources.getDisplayMetrics().density;
	}

	public static int getSizePX(int dp) {
		int px = (int)(dp * density);
		log.debug("getSizePX", density, dp, px);
		return px;
	}

	public static int getDefaultSizePX() {
		return getSizePX(ICON_SIZE_DP);
	}

	public static Drawable scale(Drawable icon, int sizePX) {

		if (icon==null) {
			return null;
		}

		if (icon.getIntrinsicHeight()==sizePX && icon.getIntrinsicWidth()==sizePX) {
			// icon is standard size, no scaling required
			log.debug("scale() - no scaling required");
			return icon;
		}

		log.debug("scale() - change", icon.getIntrinsicHeight(), icon.getIntrinsicWidth(), sizePX);
		try {
			Bitmap bitmap = ((BitmapDrawable) icon).getBitmap();
			return new BitmapDrawable(resources, Bitmap.createScaledBitmap(bitmap, sizePX, sizePX, true));
		}
		catch (ClassCastException e1) {
			/*
			 * error report
			 * java.lang.ClassCastException: android.graphics.drawable.StateListDrawable cannot be cast to android.graphics.drawable.BitmapDrawable
			 */
			if (icon instanceof StateListDrawable) {
				try {
					StateListDrawable sd = (StateListDrawable)icon;
					Bitmap bitmap = ((BitmapDrawable) sd.getCurrent()).getBitmap();
					return new BitmapDrawable(resources, Bitmap.createScaledBitmap(bitmap, sizePX, sizePX, true));
				}
				catch (Throwable e2) {
					//ignore all
				}
			}
			return null;
		}
	}

}
