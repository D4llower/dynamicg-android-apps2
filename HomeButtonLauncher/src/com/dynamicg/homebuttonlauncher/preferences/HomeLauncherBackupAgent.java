package com.dynamicg.homebuttonlauncher.preferences;

import java.util.ArrayList;

import android.app.backup.BackupAgentHelper;
import android.app.backup.BackupManager;
import android.app.backup.SharedPreferencesBackupHelper;
import android.content.Context;

import com.dynamicg.common.Logger;
import com.dynamicg.homebuttonlauncher.MainActivityHome;

/*
 * see
 * http://developer.android.com/training/cloudsync/backupapi.html
 * http://developer.android.com/guide/topics/data/backup.html
 * 
 * https://developer.android.com/google/backup/signup.html
 * http://play.google.com/apps/publish/GetBackupApiKey?p=com.dynamicg.homebuttonlauncher
 * 
 * === testing in emulator ===
 * 
 * =BACKUP=

adb shell bmgr enable true
adb shell bmgr backup com.dynamicg.homebuttonlauncher
adb shell bmgr run

 * =UNINSTALL/REINSTALL=
 * adb uninstall com.dynamicg.homebuttonlauncher
 * 
 */
public class HomeLauncherBackupAgent extends BackupAgentHelper {

	private static final Logger log = new Logger(HomeLauncherBackupAgent.class);
	private static final String BACKUP_KEY = "HomeLauncherPrefs";

	public static ArrayList<String> getSharedPrefNames(MainActivityHome activity) {
		// TODO ##implement##
		return null;
	}

	private int getNumTabs() {
		int tabcount = 0;
		try {
			PrefSettings prefSettings = new PrefSettings(this);
			tabcount = prefSettings.getNumTabs();
		}
		catch (Throwable t) {
			log.debug("ERROR", t);
		}
		return tabcount==0 ? 1 : tabcount;
	}

	@Override
	public void onCreate() {
		ArrayList<String> list = new ArrayList<String>();
		list.add(PrefSettings.SHARED_PREFS_KEY);
		int tabcount = getNumTabs();
		for (int i=0;i<tabcount;i++) {
			list.add(PreferencesManager.getShortlistName(i));
		}

		log.debug("#### HomeLauncherBackupAgent ###", list);

		String[] prefs = list.toArray(new String[]{});
		SharedPreferencesBackupHelper helper = new SharedPreferencesBackupHelper(this, prefs);
		addHelper(BACKUP_KEY, helper);
	}

	public static void requestBackup(Context context) {
		BackupManager bm = new BackupManager(context);
		bm.dataChanged();
	}

}
