package com.dynamicg.homebuttonlauncher.dialog;

import android.widget.SeekBar;

public class SizePrefsHelper {

	public static final int DEFAULT_LABEL_SIZE = 18;
	public static final int DEFAULT_ICON_SIZE = 48;

	private static final int LABEL_BASE = 12;
	private static final int LABEL_INCREMENT = 2;
	private static final int LABEL_MAX = 6; // i.e. 7 elements

	private static final int[] ICON_SIZES = new int[]{32, 36, 40, 48, 56, 64, 72}; // 48 is default so it should be "in the middle"

	public static int getLabelSize(SeekBar bar) {
		int value = bar.getProgress();
		return LABEL_BASE + LABEL_INCREMENT*value;
	}

	public static void setLabelSize(SeekBar bar, int prefvalue) {
		int value = (prefvalue-LABEL_BASE) / LABEL_INCREMENT;
		bar.setProgress(value);
		bar.setMax(LABEL_MAX);
	}

	public static int getIconSize(SeekBar bar) {
		int value = bar.getProgress();
		return ICON_SIZES[value];
	}

	public static void setIconSize(SeekBar bar, int prefvalue) {
		int value = indexOf(ICON_SIZES, prefvalue, ICON_SIZES.length/2+1);
		bar.setProgress(value);
		bar.setMax(ICON_SIZES.length-1);
	}

	private static int indexOf(int[] array, int value, int defaultIndex) {
		for (int i=0;i<array.length;i++) {
			if (value==array[i]) {
				return i;
			}
		}
		return defaultIndex; // if not found
	}

}