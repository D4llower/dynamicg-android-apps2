package com.dynamicg.bookmarkTree.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.dynamicg.bookmarkTree.prefs.PreferencesWrapper;

public class BitmapScaleManager {

	private static final int MAX_SMALL_ICON_SIZE = 24;
	private static int selection; 
	
	public static void init() {
		selection = PreferencesWrapper.iconScaling.value;
	}
	
	// for list view provider 
	public static Bitmap getIcon(byte[] blob) {
		
		if (blob==null) {
			return null;
		}
		
		Bitmap b = BitmapFactory.decodeByteArray(blob, 0, blob.length);
		if (b==null) {
			// error report Dec 28, 2010 8:35:05 PM
			return null;
		}
		
//		// FOR TESTING
//		if (true) {
//			int w = b.getWidth();
//			if (w<=24) {
//				b.setDensity(120);
//				return b;
//			}
//			else {
//				return null;
//			}
//		}
		
		if (selection==PreferencesWrapper.ICON_SCALING_NONE) {
			return b;
		}
		else if (selection==PreferencesWrapper.ICON_SCALING_160) {
			b.setDensity(160);
			return b;
		}
		else if (selection==PreferencesWrapper.ICON_SCALING_240) {
			b.setDensity(240);
			return b;
		}
		else if (selection==PreferencesWrapper.ICON_SCALING_160_120) {
			b.setDensity ( b.getWidth()>MAX_SMALL_ICON_SIZE ? 160 : 120 );
			return b;
		}
		else if (selection==PreferencesWrapper.ICON_SCALING_160_100) {
			b.setDensity ( b.getWidth()>MAX_SMALL_ICON_SIZE ? 160 : 100 );
			return b;
		}
		else if (selection==PreferencesWrapper.ICON_SCALING_160_80) {
			b.setDensity ( b.getWidth()>MAX_SMALL_ICON_SIZE ? 160 : 80 );
			return b;
		}
		else if (selection==PreferencesWrapper.ICON_SCALING_240_160) {
			b.setDensity ( b.getWidth()>MAX_SMALL_ICON_SIZE ? 240 : 160 );
			return b;
		}
		else {
			return b;
		}
		
	}
	
}
