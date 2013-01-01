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

	private static final boolean HANDLER_ACTIVE = android.os.Build.VERSION.SDK_INT == 17;

	private static WeakReference<Activity> parent;
	private static WeakReference<Dialog> mostRecentDialog;

	public DialogWithExitPoints(Activity activity) {
		super(activity);
		parent = new WeakReference<Activity>(activity);
		if (getMostRecentDialog()==null) {
			// only keep the first if we have stacked dialogs
			mostRecentDialog = new WeakReference<Dialog>(this);
		}
	}

	private static Dialog getMostRecentDialog() {
		return mostRecentDialog!=null ? mostRecentDialog.get() : null;
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (getMostRecentDialog()==this) {
			mostRecentDialog = null;
		}
		Activity activity = parent!=null ? parent.get() : null;
		if (HANDLER_ACTIVE && activity!=null) {
			log.debug("dialogStopped() - set parent visible");
			activity.setVisible(true);
		}
	}

	public static void handleActivityResume(Activity activity) {
		if (HANDLER_ACTIVE) {
			boolean mainActivityVisible = true;
			Dialog d = getMostRecentDialog();
			if (d!=null && d.isShowing()) {
				mainActivityVisible = false;
			}
			log.debug("handleActivityResume() - set parent visibility", mainActivityVisible);
			activity.setVisible(mainActivityVisible);
		}
	}

}
