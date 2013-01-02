package com.dynamicg.homebuttonlauncher.dialog;

import android.widget.SeekBar;

public class SizePrefsHelper {

	public static final int DEFAULT_LABEL_SIZE = 18;
	public static final int DEFAULT_ICON_SIZE = 48;

	public static int getLabelSize(SeekBar bar) {
		int value = bar.getProgress();
		return 12 + 2*value;
	}

	public static void setLabelSize(SeekBar bar, int prefvalue) {
		int value = (prefvalue-12)/2;
		bar.setProgress(value);
		bar.setMax(6); // i.e. 7 elements
	}

	public static int getIconSize(SeekBar bar) {
		int value = bar.getProgress();
		return 32 + 16*value;
	}

	public static void setIconSize(SeekBar bar, int prefvalue) {
		int value = (prefvalue-32)/16;
		bar.setProgress(value);
		bar.setMax(2);
	}
}