package com.dynamicg.bookmarkTree.chrome;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.dynamicg.bookmarkTree.model.BrowserBookmarkBean;
import com.dynamicg.common.Logger;

/*
 * TODO dedicated prefs page for KK without tabs
 * TODO use "//" as separator on KK
 * TODO backup/restore needs to handle the "local titles" prefs file
 */
public class ChromeWrapperKK extends ChromeWrapper {

	private static final Logger log = new Logger(ChromeWrapperKK.class);

	private final SharedPreferences prefs;
	private SharedPreferences.Editor loaderEdit;

	public ChromeWrapperKK(Context context) {
		prefs = context.getSharedPreferences("dynamicg.bmTitles", Context.MODE_PRIVATE);
		if (log.isDebugEnabled) {
			log.debug("number of local prefs", prefs.getAll().size());
		}
	}

	@SuppressLint("CommitPrefEdits")
	@Override
	public void loaderStart() {
		loaderEdit = prefs.edit();
	}

	@Override
	public void loaderProcess(BrowserBookmarkBean bean) {
		String key = Integer.toString(bean.id);
		if (prefs.contains(key)) {
			bean.fullTitle = prefs.getString(key, bean.fullTitle);
		}
		else {
			// new entry
			loaderEdit.putString(key, bean.fullTitle);
		}
	}

	@Override
	public void loaderDone() {
		loaderEdit.apply();
		loaderEdit = null;
	}

	public void saveTitle(int id, String title) {
		Editor edit = prefs.edit();
		edit.putString(Integer.toString(id), title);
		edit.commit();
	}

	public void delete(int id) {
		Editor edit = prefs.edit();
		edit.remove(Integer.toString(id));
		edit.commit();
	}

}