package com.dynamicg.bookmarkTree.backup;

import android.content.SharedPreferences;

import com.dynamicg.bookmarkTree.BookmarkTreeContext;
import com.dynamicg.bookmarkTree.prefs.PreferencesUpdater;
import com.dynamicg.common.Logger;

public class BackupPrefs {

	private static final Logger log = new Logger(BackupPrefs.class);
	
	public static final int BCK_OFF = 0;
	public static final int BCK_5 = 3;
	public static final int BCK_10 = 2;
	public static final int BCK_20 = 1;
	
	private static final String KEY_LAST_BACKUP = "backup.last";
    private static final String KEY_AUTO_BACKUP = "backup.auto";
	
	private static final SharedPreferences settings = BookmarkTreeContext.settings;
	
	public static void onStartup(BookmarkTreeContext ctx) {
		if (getAutoPrefValue()>0) {
			checkPeriodicBackup(ctx);
		}
	}
	
	private static int getDayNr() {
		long now = System.currentTimeMillis();
		long miliPerDay = 60l * 60l * 24l * 1000l;
		return (int) (now/miliPerDay);
	}
	
	private static int getBackupDaysInterval() {
		final int autoBackupValue = getAutoPrefValue();
		switch (autoBackupValue) {
		case BCK_5: return 5;
		case BCK_10: return 10;
		case BCK_20: return 20;
		default: return 0;
		}
	}
	
	private static void checkPeriodicBackup(BookmarkTreeContext ctx) {
		int daynr = getDayNr();
		int lastBackup = settings.getInt(KEY_LAST_BACKUP, 0);
		int daysBetween = getBackupDaysInterval();
		boolean required = daynr-lastBackup >= daysBetween;
		if (log.debugEnabled) {
			log.debug("checkPeriodicBackup", daynr, lastBackup, required);
		}
		if (required) {
			startBackup(ctx);
		}
	}
	
	public static void registerBackup() {
		writePref(KEY_LAST_BACKUP, getDayNr());
	}
	
	private static void startBackup(BookmarkTreeContext ctx) {
		BackupManager.createBackup(ctx, null); // no callback
	}
	
	private static void writePref(String key, int value) {
		PreferencesUpdater.writeIntPref(key, value);
	}

	public static int getAutoPrefValue() {
		return BookmarkTreeContext.settings.getInt(KEY_AUTO_BACKUP, BCK_20);
	}
	
	public static void writeBackupPref(int newPrefValue) {
		writePref(KEY_AUTO_BACKUP, newPrefValue);
	}

}
