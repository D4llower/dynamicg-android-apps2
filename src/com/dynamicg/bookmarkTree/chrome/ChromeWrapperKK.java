package com.dynamicg.bookmarkTree.chrome;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.dynamicg.bookmarkTree.model.BrowserBookmarkBean;

public class ChromeWrapperKK extends ChromeWrapper {

	private final SharedPreferences prefs;
	private SharedPreferences.Editor editor;

	public ChromeWrapperKK(Context context) {
		prefs = context.getSharedPreferences("dynamicg.bmTitles", Context.MODE_PRIVATE);
	}

	@SuppressLint("CommitPrefEdits")
	@Override
	public void loaderStart() {
		editor = prefs.edit();
	}

	@Override
	public void loaderProcess(BrowserBookmarkBean bean) {
		String key = Integer.toString(bean.id);
		if (prefs.contains(key)) {
			bean.fullTitle = prefs.getString(key, bean.fullTitle);
		}
		else {
			// new entry
			editor.putString(key, bean.fullTitle);
		}
	}

	@Override
	public void loaderDone() {
		editor.apply();
	}

}