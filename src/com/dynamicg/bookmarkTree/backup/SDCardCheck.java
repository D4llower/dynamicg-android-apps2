package com.dynamicg.bookmarkTree.backup;

import java.io.File;

import android.content.Context;
import android.os.Environment;

import com.dynamicg.bookmarkTree.R;
import com.dynamicg.common.Logger;
import com.dynamicg.common.SimpleAlertDialog;
import com.dynamicg.common.StringUtil;

public class SDCardCheck {

	private static final String exportSubdir = "dynamicg/bookmarks";
	
	private final File backupdir = getBackupDir();
	private final String sdCardState = Environment.getExternalStorageState();
	private final String errorTitle;
	
	private final Context context;

	public SDCardCheck(Context context) {
		this.context = context;
		errorTitle = context.getString(R.string.sdcardErrorTitle);
	}
	
	public static final File getBackupDir() {
		File extdir = Environment.getExternalStorageDirectory();
		File backupdir = new File(extdir, exportSubdir);
		if (!backupdir.exists()) {
			try {
				backupdir.mkdirs();
			}
			catch (Throwable e) {
				Logger.dumpIfDevelopment(e);
			}
		}
		return backupdir;
	}
	
	private static void alert(Context context,String title, final String body) {
		new SimpleAlertDialog(context, title, R.string.commonClose) {
			@Override
			public String getPlainBodyText() {
				return body;
			}
		};
	}
	private static void alert(Context context,String title, int res, String param) {
		String body = StringUtil.textWithParam(context, res, param);
		alert(context, title, body);
	}
	private static void alert(Context context,String title, int res, File param) {
		String body = StringUtil.textWithParam(context, res, param.getAbsolutePath());
		alert(context, title, body);
	}
	
	public File readyForWrite() {
		
		if (backupdir.exists() && backupdir.isDirectory() && backupdir.canWrite()) {
			return backupdir;
		}
		
		if (!Environment.MEDIA_MOUNTED.equals(sdCardState)) {
			alert(context, errorTitle, R.string.sdcardErrorNotAvailable, sdCardState);
			return null;
		}
		else if (backupdir.exists() && backupdir.canRead() && !backupdir.canWrite()) {
			alert(context, errorTitle, R.string.sdcardErrorDirReadOnly, backupdir);
			return null;
		}
		else if (!backupdir.exists() || !backupdir.isDirectory()) {
			alert(context, errorTitle, R.string.sdcardErrorDirCreateFailed, backupdir);
			return null;
		}
		else {
			// we got an uncaught error - see what happens with actual backup:
			return backupdir;
		}
		
	}
	
	public boolean readyForRead() {
		if (backupdir.exists() && backupdir.canRead()) {
			return true;
		}
		
		if (!checkMountedSdCard()) {
			return false;
		}
		else if (!backupdir.exists()) {
			alert(context, errorTitle, R.string.sdcardErrorDirMissing, backupdir);
			return false;
		}
		
		return true; // listing of backup files will probably run into issues?
	}
	
	public boolean checkMountedSdCard() {
		if ( !Environment.MEDIA_MOUNTED.equals(sdCardState)
				&& !Environment.MEDIA_MOUNTED_READ_ONLY.equals(sdCardState) )
		{
			alert(context, errorTitle, R.string.sdcardErrorNotAvailable, sdCardState);
			return false;
		}
		return true;
	}
	
}
