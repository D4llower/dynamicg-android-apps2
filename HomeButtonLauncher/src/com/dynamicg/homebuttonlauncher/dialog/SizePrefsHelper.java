package com.dynamicg.homebuttonlauncher.dialog;

import android.widget.SeekBar;

public class SizePrefsHelper {

	public static final int DEFAULT_LABEL_SIZE = 18;
	public static final int DEFAULT_ICON_SIZE = 48;

	private static final int[] LABEL_SIZES = new int[]{0, 12, 14, 16, 18, 20, 22, 24, 26};
	private static final int[] ICON_SIZES = new int[]{32, 36, 40, 48, 56, 64, 72}; // 48 is default so it should be "in the middle"

	public static int getLabelSize(SeekBar bar) {
		return LABEL_SIZES[bar.getProgress()];
	}

	public static void setLabelSize(SeekBar bar, int currentValue) {
		setSeekBar(bar, currentValue, LABEL_SIZES);
	}

	public static int getIconSize(SeekBar bar) {
		return ICON_SIZES[bar.getProgress()];
	}

	public static void setIconSize(SeekBar bar, int currentValue) {
		setSeekBar(bar, currentValue, ICON_SIZES);
	}

	private static void setSeekBar(SeekBar bar, int currentValue, int[] values) {
		int progress = indexOf(currentValue, values, values.length/2+1);
		bar.setProgress(progress);
		bar.setMax(values.length-1);
	}

	private static int indexOf(int currentValue, int[] values, int defaultIndex) {
		for (int i=0;i<values.length;i++) {
			if (currentValue==values[i]) {
				return i;
			}
		}
		return defaultIndex; // if not found
	}

}