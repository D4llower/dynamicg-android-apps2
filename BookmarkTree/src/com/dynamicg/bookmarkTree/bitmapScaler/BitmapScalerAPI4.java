package com.dynamicg.bookmarkTree.bitmapScaler;

import android.graphics.Bitmap;

import com.dynamicg.bookmarkTree.bitmapScaler.BitmapScaleManager.ScaleWorker;

public class BitmapScalerAPI4 implements ScaleWorker {

	public BitmapScalerAPI4() {
	}
	
	@Override
	public Bitmap scale(Bitmap b) {
		b.setDensity(160);
		return b;
	}

}
