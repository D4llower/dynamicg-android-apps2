package com.dynamicg.bookmarkTree.bitmapScaler;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.dynamicg.bookmarkTree.prefs.PreferencesWrapper;

/*
 * Bitmap.setDensity() is API4 so we need to wrap
 */
public class BitmapScaleManager {

	private static ScaleWorker scaleWorker;
	private static boolean enabled; 
	private static boolean isApi3=false;
	
	public static void init() {
		enabled = PreferencesWrapper.scaleIcons.isOn();
		
		if (enabled && scaleWorker==null && !isApi3) {
			try {
				Class<?> scaleWorkerClass = Class.forName("com.dynamicg.bookmarkTree.bitmapScaler.BitmapScalerAPI4");
				scaleWorker = (ScaleWorker)scaleWorkerClass.newInstance();
			}
			catch (Throwable e) {
				isApi3 = true;
			}
		}
	}
	
	public static Bitmap getIcon(byte[] blob) {
		if (blob==null) {
			return null;
		}
		
		Bitmap b = BitmapFactory.decodeByteArray(blob, 0, blob.length);
		if (b==null) {
			// error report Dec 28, 2010 8:35:05 PM
			return null;
		}
		if (enabled && scaleWorker!=null) {
			return scaleWorker.scale(b);
		}
		return b;
	}
	
	public static interface ScaleWorker {
		public Bitmap scale(Bitmap b);
	}

}
