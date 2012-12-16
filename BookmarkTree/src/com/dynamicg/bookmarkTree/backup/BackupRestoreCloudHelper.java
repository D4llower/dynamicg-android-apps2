package com.dynamicg.bookmarkTree.backup;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.DecimalFormat;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.dynamicg.bookmarkTree.BookmarkTreeContext;
import com.dynamicg.bookmarkTree.R;
import com.dynamicg.bookmarkTree.backup.BackupManager.BackupEventListener;
import com.dynamicg.common.SimpleAlertDialog;
import com.dynamicg.common.StringUtil;

public class BackupRestoreCloudHelper {

	//private static final Logger log = new Logger(BackupRestoreCloudHelper.class);

	private static final long UPLOAD_ALERT_SIZE = 2l*1024l*1024l; // alert if >n MB
	private static final double ONE_MB = 1024l * 1024l;

	private static WeakReference<BackupRestoreDialog> caller;

	public static void googleDriveBackup(final BookmarkTreeContext ctx) {
		final Context context = ctx.activity;

		BackupEventListener listener = new BackupEventListener() {
			@Override
			public void backupDone(final File backupFile) {
				if (backupFile==null) {
					return;
				}

				final long size = backupFile.length();

				if (size>UPLOAD_ALERT_SIZE) {
					new SimpleAlertDialog(context, R.string.largeFileTitle, R.string.commonOK, R.string.commonCancel) {
						@Override
						public View getBody() {
							DecimalFormat fmt = new DecimalFormat("###,###.##");
							String sizeMB = fmt.format(size/ONE_MB);
							String label = StringUtil.textWithParam(context, R.string.largeFileBody, sizeMB);
							return createTextView(label);
						}
						@Override
						public void onPositiveButton() {
							GoogleDriveUtil.upload(context, backupFile);
						}
					};
				}
				else {
					GoogleDriveUtil.upload(context, backupFile);
				}
			}
			@Override
			public void restoreDone() {
			}
		};

		if (GoogleDriveUtil.isPluginAvailable(context)) {
			BackupManager.createBackup(ctx, listener, true);
		}
		else {
			GoogleDriveUtil.alertMissingPlugin(context);
		}
	}

	public static void googleDriveRestore(final BackupRestoreDialog dialog, final BookmarkTreeContext ctx) {
		final Context context = ctx.activity;
		if (GoogleDriveUtil.isPluginAvailable(context)) {
			caller = new WeakReference<BackupRestoreDialog>(dialog);
			GoogleDriveUtil.startDownload(ctx.activity); // result gets wrapped through onActivityResult
		}
		else {
			GoogleDriveUtil.alertMissingPlugin(context);
		}
	}

	public static void confirmGoogleDriveRestore(final Intent data) {
		String path = data!=null ? data.getStringExtra(GoogleDriveGlobals.KEY_FNAME_ABS) : null;
		if (path==null || path.length()==0) {
			return;
		}
		BackupRestoreDialog dialog = caller!=null ? caller.get() : null;
		if (caller==null) {
			return;
		}
		File file = new File(path);
		dialog.restore(null, file);
	}

}
