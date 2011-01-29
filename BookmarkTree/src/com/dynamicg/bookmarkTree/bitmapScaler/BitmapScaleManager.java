package com.dynamicg.bookmarkTree.bitmapScaler;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.dynamicg.bookmarkTree.prefs.PreferencesWrapper;

/*
 * Bitmap.setDensity() is API4 so we need to wrap
 */
public class BitmapScaleManager {

	public static interface ScaleWorker {
		public final int DFLT_DENSITY = 160;
		public Bitmap scaleForList(Bitmap b); // returns 'b' for chaining
		public void setDensity(Bitmap b, int density);
		public int getDensity(Bitmap b);
	}
	
	private static final ScaleWorker scaleWorker;
	private static final boolean api3;
	
	static {
		ScaleWorker localScaleWorker;
		try {
			Class<?> scaleWorkerClass = Class.forName("com.dynamicg.bookmarkTree.bitmapScaler.BitmapScalerAPI4");
			localScaleWorker = (ScaleWorker)scaleWorkerClass.newInstance();
		}
		catch (Throwable e) {
			localScaleWorker = new BitmapScalerAPI3();
		}
		scaleWorker = localScaleWorker;
		api3 = localScaleWorker instanceof BitmapScalerAPI3;
	}
	
	private static boolean enabledForList; 
	
	public static void init() {
		enabledForList = PreferencesWrapper.scaleIcons.isOn();
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
		if (enabledForList && !api3) {
			return scaleWorker.scaleForList(b);
		}
		return b;
	}
	
	// for shortcut creation
	public static void scale(Bitmap b, int density) {
		scaleWorker.setDensity(b, density);
	}
	
	// for shortcut creation
	public static int getDensity(Bitmap b) {
		return scaleWorker.getDensity(b);
	}
	
}
