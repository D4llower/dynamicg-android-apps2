package com.dynamicg.homebuttonlauncher.tools;

import android.app.AlertDialog;
import android.content.Context;

import com.dynamicg.common.ErrorSender;
import com.dynamicg.common.Logger;

public class ErrorHandler {

	private static final Logger log = new Logger(ErrorHandler.class);

	public static void showError(Context context, String title, String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setMessage(message);
		builder.show();
	}

	public static void showCrashReport(Context context, Throwable t) {
		if (log.isDebugEnabled) {
			t.printStackTrace();
		}
		ErrorSender.notifyError(context, "ERROR", t);
	}

}
