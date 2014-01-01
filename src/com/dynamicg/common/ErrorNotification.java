package com.dynamicg.common;

import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteException;

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

	private static boolean isInvalidBrowserContentUrl(Throwable exception) {
		// java.lang.IllegalArgumentException: Unknown URL content://browser/bookmarks
		// java.lang.IllegalArgumentException: Unknown URL content://com.android.browser/bookmarks
		String str = exception!=null ? exception.toString() : "";
		return exception instanceof IllegalArgumentException
				&& str.contains("Unknown URL")
				&& str.contains("/bookmarks")
				;
	}

	private static boolean isSqlSyntaxError(Throwable exception) {
		String str = exception!=null ? exception.toString() : "";
		return exception instanceof SQLiteException
				&& str.contains("syntax error")
				&& str.contains("while compiling")
				;
	}

	private static void plainError(Context context, String title, final String bodytext, final Throwable exception) {
		new SimpleAlertDialog(context, title, R.string.commonClose) {
			@Override
			public String getPlainBodyText() {
				String msg = bodytext;
				if (exception!=null) {
					msg += "\n\nException details:\n{"+exception.toString()+"}";
				}
				return msg;
			}
		};
	}

	public static void notifyError(final Context context, final String title, final Throwable exception) {
		if (isInvalidBrowserContentUrl(exception)) {
			plainError(context
					, "Cannot read bookmarks"
					, "Cannot access browser bookmarks.\nMake sure the default browser is enabled."
					, exception
					);
		}
		else if (isSqlSyntaxError(exception)) {
			plainError(context
					, "Unsupported Android Version"
					, "This custom ROM is not supported."
					, exception
					);
		}
		else {
			final String alertTitle =  "Error: "+title;
			new SimpleAlertDialog ( context, alertTitle, "Email DEV", context.getString(R.string.commonClose)) {
				@Override
				public String getScrollViewText() {
					return SystemUtil.getExceptionText(exception);
				}
				@Override
				public void onPositiveButton() {
					emailError(context, alertTitle, exception);
				}
			};
		}
		Logger.dumpIfDevelopment(exception);
	}

}
