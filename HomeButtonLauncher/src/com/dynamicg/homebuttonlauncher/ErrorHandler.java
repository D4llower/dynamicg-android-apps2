package com.dynamicg.homebuttonlauncher;

import android.app.AlertDialog;
import android.content.Context;

import com.dynamicg.common.ErrorSender;

public class ErrorHandler {

	public static void showError(Context context, String title, String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setMessage(message);
		builder.show();
	}

	public static void showCrashReport(Context context, Throwable t) {
		ErrorSender.notifyError(context, "ERROR", t);
	}

}
