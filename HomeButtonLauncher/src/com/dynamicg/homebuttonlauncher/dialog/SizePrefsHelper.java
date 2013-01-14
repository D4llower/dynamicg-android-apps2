package com.dynamicg.homebuttonlauncher.dialog;

import android.widget.SeekBar;

public class SizePrefsHelper {

	public static final int DEFAULT_LABEL_SIZE = 18;
	public static final int DEFAULT_ICON_SIZE = 48;

	private static final int LABEL_BASE = 12;
	private static final int LABEL_INCREMENT = 2;
	private static final int LABEL_MAX = 6; // i.e. 7 elements

	private static final int ICON_BASE = 32;
	private static final int ICON_INCREMENT = 8;
	private static final int ICON_MAX = 4;

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
		return ICON_BASE + ICON_INCREMENT*value;
	}

	public static void setIconSize(SeekBar bar, int prefvalue) {
		int value = (prefvalue-ICON_BASE) / ICON_INCREMENT;
		bar.setProgress(value);
		bar.setMax(ICON_MAX);
	}
}