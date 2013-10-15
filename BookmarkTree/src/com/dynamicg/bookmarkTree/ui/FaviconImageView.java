package com.dynamicg.bookmarkTree.ui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
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

	@Override
	protected void onDraw(Canvas canvas) {
		if (!isFolder) {
			canvas.drawBitmap(background, 0, 0, null);
		}
		super.onDraw(canvas);
	}

	//	public static void setBackground(Resources r) {
	//		if (background==null) {
	//			background = BitmapFactory.decodeResource(r, R.drawable.favicon_background);
	//		}
	//	}

	// see http://stackoverflow.com/questions/3035692/how-to-convert-a-drawable-to-a-bitmap
	public static void setBackground(Resources r) {
		if (background==null) {
			Drawable drawable = r.getDrawable(R.drawable.favicon_bg);
			int sizeDP = (int)r.getDimension(R.dimen.faviconSize);
			Bitmap copy = Bitmap.createBitmap(sizeDP, sizeDP, Bitmap.Config.ARGB_8888);
			Canvas canvas = new Canvas(copy);
			drawable.setBounds(0, 0, copy.getWidth(), copy.getHeight());
			drawable.draw(canvas);
			background = copy;
		}
	}

}
