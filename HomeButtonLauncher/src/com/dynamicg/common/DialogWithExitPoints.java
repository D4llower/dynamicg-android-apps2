package com.dynamicg.common;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.app.Dialog;

/*
 * there is this nasty 4.2 bug:
 * - dialog is started through menu (or popup menu)
 * - from the dialog we start an activity (e.g. link to play store or google drive upload)
 * - when the app is resumed after the sub activity, the main activity window is brought to front, with the
 * dialog still active but in the background
 * 
 * to catch that we manually set the main activity's visibility accordingly in activity.onResume and dialog.onStop
 */
public class DialogWithExitPoints extends Dialog {

	private static final Logger log = new Logger(DialogWithExitPoints.class);

	private static final boolean HANDLER_ACTIVE = android.os.Build.VERSION.SDK_INT == 17 || log.isDebugEnabled;

	private static WeakReference<Activity> parent;
	private static WeakReference<Dialog> mostRecentDialog;

	public DialogWithExitPoints(Activity activity) {
		super(activity);
		parent = new WeakReference<Activity>(activity);
		mostRecentDialog = new WeakReference<Dialog>(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		mostRecentDialog = null;
		Activity activity = parent!=null ? parent.get() : null;
		if (HANDLER_ACTIVE && activity!=null) {
			log.debug("dialogStopped() - set parent visible");
			activity.setVisible(true);
		}
	}

	public static void handleActivityResume(Activity activity) {
		if (HANDLER_ACTIVE) {
			boolean mainActivityVisible = true;
			Dialog dialog = mostRecentDialog!=null ? mostRecentDialog.get() : null;
			if (dialog!=null && dialog.isShowing()) {
				mainActivityVisible = false;
				dialog.show();
			}
			log.debug("handleActivityResume() - set parent visibility", mainActivityVisible);
			activity.setVisible(mainActivityVisible);
		}
	}

}
