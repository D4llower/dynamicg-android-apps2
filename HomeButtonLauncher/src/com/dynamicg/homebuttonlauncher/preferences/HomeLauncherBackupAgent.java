package com.dynamicg.homebuttonlauncher.preferences;

import java.util.ArrayList;

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

	private void attachExtraTabs(ArrayList<String> list) {
		try {
			PrefSettings prefSettings = new PrefSettings(this.getSharedPreferences(PreferencesManager.PREF_SETTINGS, Context.MODE_PRIVATE));
			int numTabs = prefSettings.getNumTabs();
			if (numTabs==0) {
				return;
			}
			for (int i=1;i<numTabs;i++) {
				// add extra tabs (tab0 is already on list)
				list.add(PreferencesManager.getShortlistName(i));
			}
		}
		catch (Throwable t) {
			// ignore
			return;
		}
	}

	@Override
	public void onCreate() {
		ArrayList<String> list = new ArrayList<String>();
		list.add(PreferencesManager.PREF_SHORTLIST);
		list.add(PreferencesManager.PREF_SETTINGS);
		attachExtraTabs(list);

		String[] prefs = list.toArray(new String[]{});
		SharedPreferencesBackupHelper helper = new SharedPreferencesBackupHelper(this, prefs);
		addHelper(BACKUP_KEY, helper);
	}

	public static void requestBackup(Context context) {
		BackupManager bm = new BackupManager(context);
		bm.dataChanged();
	}

}
