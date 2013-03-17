package com.dynamicg.homebuttonlauncher.tools.drive;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.SpannableString;
import android.view.View;
import android.widget.TextView;

import com.dynamicg.common.MarketLinkHelper;
import com.dynamicg.homebuttonlauncher.OnClickListenerWrapper;
import com.dynamicg.homebuttonlauncher.R;
import com.dynamicg.homebuttonlauncher.tools.DialogHelper;

public class GoogleDriveUtil {

	public static final String PLUGIN_APP = "com.dynamicg.timerec.plugin3";
	private static final String PLUGIN_ACTIVITY = "com.dynamicg.timerec.plugin3.gdrive.FileProviderActivity";

	public static final String MSG_MISSING_APP_TITLE = "Google Drive Plugin required";
	public static final String MSG_MISSING_APP_BODY = "Click here to install";
	public static final String MSG_TOAST_DONE = "Restore done. Please restart the app";

	private static Intent getBaseIntent(int requestCode) {
		ComponentName component = new ComponentName(PLUGIN_APP, PLUGIN_ACTIVITY);
		Intent intent = new Intent();
		intent.setComponent(component);
		//intent.putExtra(GoogleDriveGlobals.KEY_APP_INSTANCE, "F"); // F=free see com.dynamicg.timerecording.util.GoogleDriveUtil.setIntentBasics(Intent, int)
		intent.putExtra(GoogleDriveGlobals.KEY_CUSTOM_MAIN_FOLDER, GoogleDriveBackupRestoreHelper.GOOGLE_DRIVE_FOLDER_NAME);
		intent.putExtra(GoogleDriveGlobals.KEY_REQUEST_CODE, requestCode);
		return intent;
	}

	public static void upload(Context context, File file) {

		final int requestCode = GoogleDriveGlobals.ACTION_CUSTOM_PUT;

		if (file==null) {
			return;
		}
		Intent intent = getBaseIntent(requestCode);
		intent.putExtra(GoogleDriveGlobals.KEY_FNAME_ABS, file.getAbsolutePath());
		intent.putExtra(GoogleDriveGlobals.KEY_DELETE_FILE_AFTER_UPLOAD, 1);

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

	public static void startDownload(Activity context, File file) {

		final int requestCode = GoogleDriveGlobals.ACTION_CUSTOM_GET;

		Intent intent = getBaseIntent(requestCode);
		intent.putExtra(GoogleDriveGlobals.KEY_FNAME_DRIVE, GoogleDriveBackupRestoreHelper.GOOGLE_DRIVE_FILE_NAME);
		intent.putExtra(GoogleDriveGlobals.KEY_FNAME_LOCAL, file.getAbsolutePath());

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
		DialogHelper.showCrashReport(context, e);
	}

	public static void alertMissingPlugin(final Context context) {
		String titleLabel = MSG_MISSING_APP_TITLE;
		String bodyLabel = MSG_MISSING_APP_BODY;

		SpannableString bodyString = new SpannableString(bodyLabel);
		DialogHelper.underline(bodyString, 0, bodyLabel.length());
		DialogHelper.bold(bodyString, 0, bodyLabel.length());

		int padding = DialogHelper.getDimension(context, R.dimen.headerPadding);
		TextView body = new TextView(context);
		body.setText(bodyString);
		body.setTextColor(context.getResources().getColorStateList(R.color.applink));
		body.setPadding(padding, padding, padding, padding);
		body.setOnClickListener(new OnClickListenerWrapper() {
			@Override
			public void onClickImpl(View view) {
				MarketLinkHelper.openMarketIntent(context, PLUGIN_APP);
			}
		});

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setPositiveButton(R.string.buttonCancel, null);
		builder.setTitle(titleLabel);
		builder.setView(body);

		builder.show();
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
