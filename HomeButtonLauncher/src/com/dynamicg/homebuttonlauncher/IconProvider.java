package com.dynamicg.homebuttonlauncher;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.dynamicg.common.Logger;

// see http://stackoverflow.com/questions/4609456/android-set-drawable-size-programatically
public class IconProvider {

	private static final Logger log = new Logger(IconProvider.class);

	private static final int ICON_SIZE_DP = 48;

	private static Resources resources;
	private static int sizePX;

	public static void init(Context context) {
		resources = context.getResources();
		float density = resources.getDisplayMetrics().density;
		sizePX = (int)(ICON_SIZE_DP * density);
		log.debug("IconProvider init", density, sizePX);
	}

	public static Drawable scale(Drawable icon) {

		if (icon.getIntrinsicHeight()==sizePX && icon.getIntrinsicWidth()==sizePX) {
			// icon is standard size, no scaling required
			log.debug("scale() - no scaling required");
			return icon;
		}

		log.debug("scale() - change", icon.getIntrinsicHeight(), icon.getIntrinsicWidth(), sizePX);
		Bitmap bitmap = ((BitmapDrawable) icon).getBitmap();
		return new BitmapDrawable(resources, Bitmap.createScaledBitmap(bitmap, sizePX, sizePX, true));
	}

}
