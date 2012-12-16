package com.dynamicg.bookmarkTree.backup;

import java.io.File;
import java.lang.ref.WeakReference;

import android.content.Context;
import android.content.Intent;

import com.dynamicg.bookmarkTree.BookmarkTreeContext;
import com.dynamicg.bookmarkTree.backup.BackupManager.BackupEventListener;

public class BackupRestoreCloudHelper {

	private static WeakReference<BackupRestoreDialog> caller;

	public static void googleDriveBackup(final BookmarkTreeContext ctx) {
		final Context context = ctx.activity;
		BackupEventListener listener = new BackupEventListener() {
			@Override
			public void backupDone(File backupFile) {
				if (backupFile!=null) {
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
