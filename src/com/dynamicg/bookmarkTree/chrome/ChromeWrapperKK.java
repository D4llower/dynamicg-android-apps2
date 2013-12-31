package com.dynamicg.bookmarkTree.chrome;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.dynamicg.bookmarkTree.BookmarkTreeContext;
import com.dynamicg.bookmarkTree.model.BrowserBookmarkBean;
import com.dynamicg.common.Logger;

/*
 * TODO backup/restore needs to handle the "local titles" prefs file
 * TODO catch "custom ROM sql error"
 * TODO import toggle: [x] Browser Bookmark Data, [x] App Settings, hint: do not import "Broswer Bookmark Data" when browser sync is enabled
 */
public class ChromeWrapperKK extends ChromeWrapper {

	private static final Logger log = new Logger(ChromeWrapperKK.class);

	private final BookmarkTreeContext ctx;
	private final SharedPreferences prefs;
	private SharedPreferences.Editor loaderEdit;
	private MigrationHelper migrationHelper;


	public ChromeWrapperKK(BookmarkTreeContext ctx) {
		this.ctx = ctx;
		this.prefs = ctx.activity.getSharedPreferences("dynamicg.bmTitles", Context.MODE_PRIVATE);
		if (log.isDebugEnabled) {
			log.debug("number of local prefs", prefs.getAll().size());
		}
	}

	private class MigrationHelper {
		final String separatorLegacy = ctx.getFolderSeparator(BookmarkTreeContext.SP_LEGACY);
		final String separatorKK = ctx.getFolderSeparator(BookmarkTreeContext.SP_CURRENT);
		String getNewTitle(BrowserBookmarkBean bean) {
			return bean.fullTitle.replace(separatorLegacy, separatorKK);
		}
	}

	@SuppressLint("CommitPrefEdits")
	@Override
	public void bmLoadStart() {
		loaderEdit = prefs.edit();
		if (kkMigrationPending()) {
			this.migrationHelper = new MigrationHelper();
		}
	}

	@Override
	public void bmLoadProcess(BrowserBookmarkBean bean) {
		String key = Integer.toString(bean.id);
		if (prefs.contains(key)) {
			bean.fullTitle = prefs.getString(key, bean.fullTitle);
		}
		else {
			// new entry
			if (migrationHelper!=null) {
				loaderEdit.putString(key, migrationHelper.getNewTitle(bean));
			}
			else {
				loaderEdit.putString(key, bean.fullTitle);
			}
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

}