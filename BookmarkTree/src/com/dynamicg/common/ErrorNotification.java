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
	
	private static void emailError(Context context, Throwable exception) {
		String version = ContextUtil.getVersion(context)[0];
		String title = "Bookmark Tree "+version+" - Error ("+Locale.getDefault().getLanguage()+")";
		String body = SystemUtil.getExceptionText(exception);
		createIntent(context, title, body);
	}
	
	public static void notifyError(final Context context, final String title, final Throwable e) {
		new SimpleAlertDialog ( context, "Error: "+title
				, "Email DEV", context.getString(R.string.commonClose) 
				) 
		{
			
			@Override
			public String getScrollViewText() {
				return SystemUtil.getExceptionText(e);
			}
	
			@Override
			public void onPositiveButton() {
				emailError(context, e);
			}
			
		};
		Logger.dumpIfDevelopment(e);
	}
	
}
