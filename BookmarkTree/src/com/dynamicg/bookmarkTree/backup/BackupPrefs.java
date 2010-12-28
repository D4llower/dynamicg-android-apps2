package com.dynamicg.bookmarkTree.backup;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.dynamicg.bookmarkTree.BookmarkTreeContext;
import com.dynamicg.common.Logger;
import com.dynamicg.common.SimpleAlertDialog;

public class BackupPrefs {

	private static final Logger log = new Logger(BackupPrefs.class);
	
	public static final int DAYS_BETWEEN = log.debugEnabled ? 5 : 20;
	
	private static final String KEY_LAST_BACKUP = "backup.last";
	private static final String KEY_AUTO_ENABLED = "backup.auto";
	
	private static final SharedPreferences settings = BookmarkTreeContext.settings;
	
	public static void onStartup(BookmarkTreeContext ctx) {
		//cleanup();
		int autoEnabled = settings.getInt(KEY_AUTO_ENABLED, -1);
		if (autoEnabled==1) {
			checkPeriodicBackup(ctx);
		}
		
		// initial confirmation for "enable auto backup":
//		if (autoEnabled==-1) {
//			// first call - init to 0 and ask for "auto enable"
//			writePref(KEY_AUTO_ENABLED, 0);
//			BackupPrefs.initialBackupConfirmation(ctx);
//		}
//		else if (autoEnabled==1) {
//			checkPeriodicBackup(ctx);
//		}
		
	}
	
	@SuppressWarnings("unused")
	private static void initialBackupConfirmation(final BookmarkTreeContext ctx) {
		new SimpleAlertDialog.OkCancelDialog(ctx.activity, Messages.brEnableAutoBackup) {
			@Override
			public void onPositiveButton() {
				writePref(KEY_AUTO_ENABLED, 1);
				startBackup(ctx);
			}
		};
	}
	
	private static int getDayNr() {
		long now = System.currentTimeMillis();
		long miliPerDay = 60l * 60l * 24l * 1000l;
		return (int) (now/miliPerDay);
	}
	
	private static void checkPeriodicBackup(BookmarkTreeContext ctx) {
		int daynr = getDayNr();
		int lastBackup = settings.getInt(KEY_LAST_BACKUP, 0);
		boolean required = daynr-lastBackup >= DAYS_BETWEEN;
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
		Editor edit = settings.edit();
		edit.putInt(key, value);
		edit.commit();
		if (log.debugEnabled) {
			log.debug("write pref", key, value);
		}
	}

	public static boolean isAutoBackupEnabled() {
		if (log.debugEnabled) {
			log.debug("isAutoBackupEnabled", settings.getInt(KEY_AUTO_ENABLED, 0) == 1 );
		}
		return settings.getInt(KEY_AUTO_ENABLED, 0) == 1;
	}

	public static void writeAutoBackupEnabled(boolean isChecked) {
		writePref(KEY_AUTO_ENABLED, isChecked?1:0);
	}

//	@SuppressWarnings("unused")
//	private static void cleanup() {
//		Editor edit = settings.edit();
//		edit.remove(KEY_AUTO_ENABLED);
//		edit.remove(KEY_LAST_BACKUP);
//		edit.commit();
//	}
	
}
