package com.dynamicg.homebuttonlauncher.tools;

import android.content.Intent;

public class DirectDialWrapper {

	private static final String CALL_DFLT = Intent.ACTION_CALL; // "android.intent.action.CALL"
	private static final String CALL_PRIVILEGED = "android.intent.action.CALL_PRIVILEGED";

	public static void updateIntent(Intent intent) {
		if (intent==null) {
			return;
		}

		// action_call requires android.permission.CALL_PHONE so we change it to "dial" (which opens the dial pad with the given phone number)
		String action = intent!=null ? intent.getAction() : null;
		if (CALL_DFLT.equals(action) || CALL_PRIVILEGED.equals(action)) {
			intent.setAction(Intent.ACTION_DIAL);
		}

	}

}
