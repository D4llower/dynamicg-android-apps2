package com.dynamicg.bookmarkTree.chrome;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.dynamicg.bookmarkTree.BookmarkTreeContext;
import com.dynamicg.bookmarkTree.model.BrowserBookmarkBean;
import com.dynamicg.common.Logger;

/*
 * TODO: set 19 as minSdk on production roll out
 * TODO: add as new project to trunk - https://dynamicg-android-apps2.googlecode.com/svn/trunk/BookmarkTreeKK
 */
public class ChromeWrapperKK extends ChromeWrapper {

	private static final Logger log = new Logger(ChromeWrapperKK.class);
	private static final boolean TEST_MIGRATION = false;

	private final SharedPreferences prefs;
	private SharedPreferences.Editor loaderEdit;
	private MigrationHelper migrationHelper;

	public ChromeWrapperKK(Context context) {
		this.prefs = context.getSharedPreferences("dynamicg.bmTitles", Context.MODE_PRIVATE);

		if (TEST_MIGRATION) {
			Editor edit = prefs.edit();
			edit.clear();
			edit.commit();
		}

		if (log.isDebugEnabled) {
			log.debug("number of local prefs", prefs.getAll().size());
		}
	}

	private class MigrationHelper {
		final String separatorLegacy;
		final String separatorKK;
		MigrationHelper(BookmarkTreeContext ctx) {
			separatorLegacy = ctx.getNodeConcatenation(BookmarkTreeContext.SP_LEGACY);
			separatorKK = ctx.getNodeConcatenation(BookmarkTreeContext.SP_CURRENT);
		}
		String getNewTitle(BrowserBookmarkBean bean) {
			if (!separatorLegacy.equals(separatorKK)) {
				return bean.fullTitle.replace(separatorLegacy, separatorKK);
			}
			return bean.fullTitle;
		}
	}

	@SuppressLint("CommitPrefEdits")
	@Override
	public void bmLoadStart(BookmarkTreeContext ctx) {
		loaderEdit = prefs.edit();
		if (kkMigrationPending()) {
			this.migrationHelper = new MigrationHelper(ctx);
		}
		else {
			this.migrationHelper = null;
		}
	}

	@Override
	public void bmLoadProcess(BrowserBookmarkBean bean) {
		String key = Integer.toString(bean.id);
		if (prefs.contains(key)) {
			bean.fullTitle = prefs.getString(key, bean.fullTitle);
		}
		else if (migrationHelper!=null) {
			// first run on KK after upgrade
			log.debug("MIGRATION", bean.fullTitle, migrationHelper.getNewTitle(bean));
			String migratedTitle = migrationHelper.getNewTitle(bean);
			loaderEdit.putString(key, migratedTitle);
			bean.fullTitle = migratedTitle;
		}
		else {
			// new bookmark
			loaderEdit.putString(key, bean.fullTitle);
		}
	}

	@Override
	public void bmLoadDone() {
		loaderEdit.apply();
		loaderEdit = null;
	}

	public void saveTitle(int id, String title) {
		Editor edit = prefs.edit();
		edit.putString(Integer.toString(id), title);
		edit.apply();
	}

	public void delete(int id) {
		Editor edit = prefs.edit();
		edit.remove(Integer.toString(id));
		edit.apply();
	}

	public boolean isPrefsEmpty() {
		return prefs.getAll().size()==0;
	}

	public SharedPreferences getSharedPrefs() {
		return prefs;
	}

	public String getLabel(int id, String title) {
		return prefs.getString(Integer.toString(id), title);
	}

	public void resetPrefs() {
		Editor edit = prefs.edit();
		edit.clear();
		edit.apply();
	}

}