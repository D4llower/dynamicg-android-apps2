package com.dynamicg.bookmarkTree.bitmapScaler;

import android.graphics.Bitmap;

import com.dynamicg.bookmarkTree.bitmapScaler.BitmapScaleManager.ScaleWorker;

/*
 * Bitmap.set/getDensity is not available
 */
public class BitmapScalerAPI3 implements ScaleWorker {

	public BitmapScalerAPI3() {
	}
	
	@Override
	public Bitmap scaleForList(Bitmap b) {
		return b;
	}

	@Override
	public void setDensity(Bitmap b, int density) {
	}
	
	@Override
	public int getDensity(Bitmap b) {
		return DFLT_DENSITY;
	}

}
