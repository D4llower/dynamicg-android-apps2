package com.dynamicg.bookmarkTree.backup;

import java.io.File;

import android.content.Context;
import android.os.Environment;

import com.dynamicg.bookmarkTree.R;
import com.dynamicg.common.ErrorNotification;
import com.dynamicg.common.Logger;
import com.dynamicg.common.SimpleAlertDialog;

public class SDCardCheck {

	private static final String exportSubdir = "dynamicg/bookmarks";
	
	private final File backupdir = getBackupDir();
	private final String sdCardState = Environment.getExternalStorageState();
	private final String errorTitle = "SD Card access error";
	
	private final Context context;

	private Throwable touchFileException;
	
	public SDCardCheck(Context context) {
		this.context = context;
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
	
	public File readyForWrite() {
		
		if (backupdir.exists() && backupdir.canWrite()) {
			return backupdir;
		}
		
		if (!Environment.MEDIA_MOUNTED.equals(sdCardState)) {
			alert(context, errorTitle, "SD Card is not mounted.\nCurrent state is '"+sdCardState+"'");
		}
		else if (backupdir.exists() && backupdir.canRead() && !backupdir.canWrite()) {
			alert(context, errorTitle, "Directory "+backupdir+" is read only");
		}
		else if (!backupdir.exists()) {
			alert(context, errorTitle, "Could not create backup directory:\n"+backupdir);
		}
		else if (touchFileException!=null) {
			ErrorNotification.notifyError(context, touchFileException);
		}
		else {
			// we got an uncaught error - see what happens with actual backup:
			return backupdir;
		}
		return null; // not okay
	}
	
	public boolean readyForRead() {
		if (backupdir.exists() && backupdir.canRead()) {
			return true;
		}
		
		if ( !Environment.MEDIA_MOUNTED.equals(sdCardState)
				|| !Environment.MEDIA_MOUNTED_READ_ONLY.equals(sdCardState) )
		{
			alert(context, errorTitle, "SD Card is not available.\nCurrent state is '"+sdCardState+"'");
		}
		else if (!backupdir.exists()) {
			alert(context, errorTitle, "Backup directory does not exist:\n"+backupdir);
		}
		
		return false;
	}
	
	public void checkMountedSdCard() {
		if ( !Environment.MEDIA_MOUNTED.equals(sdCardState)
				|| !Environment.MEDIA_MOUNTED_READ_ONLY.equals(sdCardState) )
		{
			alert(context, errorTitle, "SD Card is not available.\nCurrent state is '"+sdCardState+"'");
		}
	}
	
}
