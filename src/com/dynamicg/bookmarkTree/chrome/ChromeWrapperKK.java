package com.dynamicg.bookmarkTree.chrome;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.dynamicg.bookmarkTree.model.BrowserBookmarkBean;

/*
 * TODO google drive/set title
 * TODO dedicated prefs page for KK without tabs
 * TODO use "//" as separator on KK
 * TODO test "insert" - which is the new bookmark id?
 */
public class ChromeWrapperKK extends ChromeWrapper {

	private final SharedPreferences prefs;
	private SharedPreferences.Editor loaderEdit;

	public ChromeWrapperKK(Context context) {
		prefs = context.getSharedPreferences("dynamicg.bmTitles", Context.MODE_PRIVATE);
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