package com.dynamicg.bookmarkTree.chrome;

import android.content.Context;
import android.content.SharedPreferences;

import com.dynamicg.bookmarkTree.BookmarkTreeContext;
import com.dynamicg.bookmarkTree.model.BrowserBookmarkBean;
import com.dynamicg.bookmarkTree.prefs.PreferencesUpdater;
import com.dynamicg.bookmarkTree.prefs.PreferencesWrapper;
import com.dynamicg.common.Logger;

public abstract class ChromeWrapper {

	private static final Logger log = new Logger(ChromeWrapper.class);

	private static final String KEY_KK_MIGRATION = "kk.migration";
	private static final int KK_MIG_PENDING = 1;
	private static final int KK_MIG_DONE = 2;

	private static ChromeWrapper instance;
	private static boolean kitkat;

	public abstract void bmLoadStart(BookmarkTreeContext ctx);
	public abstract void bmLoadProcess(BrowserBookmarkBean bean);
	public abstract void bmLoadDone();

	public static void init(Context context) {
		kitkat = android.os.Build.VERSION.SDK_INT>=19 || log.isDebugEnabled;
		if (kitkat) {
			instance = new ChromeWrapperKK(context);
		}
		else {
			instance = new ChromeWrapperOff();
		}
	}

	public static ChromeWrapper getInstance() {
		return instance;
	}

	public static ChromeWrapperKK getKitKatInstance() {
		return (ChromeWrapperKK)instance;
	}

	public static boolean isKitKat() {
		return kitkat;
	}

	public static void markPendingMigration(SharedPreferences mainSettings) {
		// this kicks in if user opens app first time on KK after having it used before
		if (kitkat && mainSettings.contains(PreferencesWrapper.KEY_DISCLAIMER) && getKitKatInstance().isPrefsEmpty()) {
			PreferencesUpdater.writeIntPref(KEY_KK_MIGRATION, KK_MIG_PENDING);
		}
	}

	public static void forceMarkPendingMigration() {
		PreferencesUpdater.writeIntPref(KEY_KK_MIGRATION, KK_MIG_PENDING);
	}

	public static boolean kkMigrationPending() {
		int value = BookmarkTreeContext.settings.getInt(KEY_KK_MIGRATION, 0);
		if (value==KK_MIG_PENDING) {
			PreferencesUpdater.writeIntPref(KEY_KK_MIGRATION, KK_MIG_DONE);
			return true;
		}
		return false;
	}

}
