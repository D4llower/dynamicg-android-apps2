package com.dynamicg.bookmarkTree.chrome;

import android.content.Context;

import com.dynamicg.bookmarkTree.model.BrowserBookmarkBean;
import com.dynamicg.common.Logger;

public abstract class ChromeWrapper {

	private static final Logger log = new Logger(ChromeWrapper.class);

	private static ChromeWrapper instance;
	public static boolean enabled;

	public abstract void loaderStart();
	public abstract void loaderProcess(BrowserBookmarkBean bean);
	public abstract void loaderDone();

	public static void init(Context context) {
		enabled = android.os.Build.VERSION.SDK_INT>=19 || log.isDebugEnabled;
		instance = enabled ? new ChromeWrapperKK(context) : new ChromeWrapperOff();
	}

	public static ChromeWrapper getInstance() {
		return instance;
	}

}
