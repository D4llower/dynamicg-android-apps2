package com.dynamicg.bookmarkTree.ui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.dynamicg.bookmarkTree.R;

public class FaviconImageView extends ImageView {

	private static Bitmap background;
	public boolean isFolder = false;
	
	public FaviconImageView(Context context) {
		super(context);
	}

	public FaviconImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public FaviconImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	protected void onDraw(Canvas canvas) {
        if (!isFolder) {
        	canvas.drawBitmap(background, 0, 0, null);
        }
		super.onDraw(canvas);
	}

	public static void setBackground(Resources r) {
		if (background==null) {
			background = BitmapFactory.decodeResource(r, R.drawable.favicon_bg_round); 
		}
	}
	
}
