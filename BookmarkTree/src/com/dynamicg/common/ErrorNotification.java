package com.dynamicg.common;

import java.util.Locale;

import android.content.Context;
import android.content.Intent;

import com.dynamicg.bookmarkTree.R;
import com.dynamicg.bookmarkTree.dialogs.AboutDialog;

public class ErrorNotification {

	private static void createIntent ( Context context, String title, String body) {
		Intent msg = new Intent(Intent.ACTION_SEND);
		msg.setType("text/plain");
		msg.putExtra(Intent.EXTRA_SUBJECT, title);
		msg.putExtra(Intent.EXTRA_TEXT, body);
		msg.putExtra(Intent.EXTRA_EMAIL, new String[]{AboutDialog.AUTHOR} );
		context.startActivity(Intent.createChooser(msg, "Send error report"));
	}

	private static void emailError(Context context, String alertTitle, Throwable exception) {
		String version = ContextUtil.getVersion(context)[0];
		String title = context.getString(R.string.app_name)+" "+version+" Error ("+Locale.getDefault().getLanguage()+")";
		String body = alertTitle+"\n\n"
				+ DeviceInfo.getDeviceInfo()+"\n\n"
				+ SystemUtil.getExceptionText(exception);
		createIntent(context, title, body);
	}

	public static void notifyError(final Context context, final String title, final Throwable e) {
		final String alertTitle =  "Error: "+title;
		new SimpleAlertDialog ( context, alertTitle, "Email DEV", context.getString(R.string.commonClose)) {

			@Override
			public String getScrollViewText() {
				return SystemUtil.getExceptionText(e);
			}

			@Override
			public void onPositiveButton() {
				emailError(context, alertTitle, e);
			}

		};
		Logger.dumpIfDevelopment(e);
	}

}
