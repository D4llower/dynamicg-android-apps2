package com.dynamicg.bookmarkTree.backup;

import java.io.File;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;

import com.dynamicg.bookmarkTree.R;
import com.dynamicg.bookmarkTree.prefs.MarketLinkHelper;
import com.dynamicg.common.ContextUtil;
import com.dynamicg.common.ErrorNotification;
import com.dynamicg.common.SimpleAlertDialog;

public class GoogleDriveUtil {

	public static final String PLUGIN_APP = "com.dynamicg.timerec.plugin3";
	private static final String PLUGIN_ACTIVITY = "com.dynamicg.timerec.plugin3.gdrive.FileProviderActivity";

	private static Intent getBaseIntent(int requestCode) {
		ComponentName component = new ComponentName(PLUGIN_APP, PLUGIN_ACTIVITY);
		Intent intent = new Intent();
		intent.setComponent(component);
		intent.putExtra(GoogleDriveGlobals.KEY_APP_INSTANCE, "F"); // F=free see com.dynamicg.timerecording.util.GoogleDriveUtil.setIntentBasics(Intent, int)
		intent.putExtra(GoogleDriveGlobals.KEY_REQUEST_CODE, requestCode);
		return intent;
	}

	public static void upload(Context context, File file) {

		final int requestCode = GoogleDriveGlobals.ACTION_BACKUP;

		if (file==null) {
			return;
		}
		Intent intent = getBaseIntent(requestCode);
		intent.putExtra(GoogleDriveGlobals.KEY_FNAME_ABS, file.getAbsolutePath());

		try {
			context.startActivity(intent);
		}
		catch (ActivityNotFoundException e) {
			alertMissingPlugin(context);
		}
		catch (SecurityException e) {
			showPermissionError(context, e);
		}
	}

	public static void startDownload(Activity context) {

		final int requestCode = GoogleDriveGlobals.ACTION_RESTORE;

		Intent intent = getBaseIntent(requestCode);
		intent.putExtra(GoogleDriveGlobals.KEY_FNAME_DRIVE, BackupManager.GOOGLE_DRIVE_FILE_NAME);
		intent.putExtra(GoogleDriveGlobals.KEY_FNAME_LOCAL, BackupManager.GOOGLE_DRIVE_FILE_NAME);

		try {
			context.startActivityForResult(intent, requestCode);
		}
		catch (ActivityNotFoundException e) {
			alertMissingPlugin(context);
		}
		catch (SecurityException e) {
			showPermissionError(context, e);
		}
	}

	private static void showPermissionError(final Context context, final SecurityException e) {
		// TODO link this to the timerec online help (error27)
		ErrorNotification.notifyError(context, "SecurityException", e);
	}

	public static void alertMissingPlugin(final Context context) {
		new SimpleAlertDialog(context, R.string.missingAppTitle, R.string.commonClose) {
			@Override
			public View getBody() {
				String text = context.getString(R.string.missingAppBody);
				TextView node = createTextView(text);
				node.setTextColor(context.getResources().getColorStateList(R.color.linksabout));
				node.setTypeface(Typeface.DEFAULT_BOLD, Typeface.BOLD);
				node.setOnClickListener(MarketLinkHelper.getGoogleDrivePluginLink(context));

				int padlr = ContextUtil.getScaledSizeInt(context, 6);
				int padtb = ContextUtil.getScaledSizeInt(context, 20);
				node.setPadding(padlr, padtb, padlr, padtb);

				return node;
			}
		};
	}

	public static boolean isPluginAvailable(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager().getPackageInfo(PLUGIN_APP, PackageManager.GET_ACTIVITIES);
			return packageInfo!=null;
		} catch (NameNotFoundException e) {
			return false;
		}
	}

}
