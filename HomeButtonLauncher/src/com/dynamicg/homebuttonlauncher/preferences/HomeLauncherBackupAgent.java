package com.dynamicg.homebuttonlauncher.preferences;

import android.app.backup.BackupAgentHelper;
import android.app.backup.BackupManager;
import android.app.backup.SharedPreferencesBackupHelper;
import android.content.Context;

/*
 * see
 * http://developer.android.com/training/cloudsync/backupapi.html
 * http://developer.android.com/guide/topics/data/backup.html
 * 
 * https://developer.android.com/google/backup/signup.html
 * http://play.google.com/apps/publish/GetBackupApiKey?p=com.dynamicg.homebuttonlauncher
 * 
 * === testing in emulator ===
 * =BACKUP=
 * adb shell bmgr enable true
 * adb shell bmgr backup com.dynamicg.homebuttonlauncher
 * adb shell bmgr run
 * =UNINSTALL/REINSTALL=
 * adb uninstall com.dynamicg.homebuttonlauncher
 * 
 */
public class HomeLauncherBackupAgent extends BackupAgentHelper {

	private static final String BACKUP_KEY = "HomeLauncherPrefs";

	@Override
	public void onCreate() {
		SharedPreferencesBackupHelper helper =
				new SharedPreferencesBackupHelper(this, PreferencesManager.PREF_SHORTLIST, PreferencesManager.PREF_SETTINGS);
		addHelper(BACKUP_KEY, helper);
	}

	public static void requestBackup(Context context) {
		BackupManager bm = new BackupManager(context);
		bm.dataChanged();
	}

}
