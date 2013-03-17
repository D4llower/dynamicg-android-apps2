package com.dynamicg.homebuttonlauncher.tools.drive;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.widget.Toast;

import com.dynamicg.common.FileUtil;
import com.dynamicg.common.Logger;
import com.dynamicg.homebuttonlauncher.MainActivityHome;
import com.dynamicg.homebuttonlauncher.MenuGlobals;
import com.dynamicg.homebuttonlauncher.OnClickListenerDialogWrapper;
import com.dynamicg.homebuttonlauncher.R;
import com.dynamicg.homebuttonlauncher.dialog.PreferencesDialog;
import com.dynamicg.homebuttonlauncher.preferences.HomeLauncherBackupAgent;
import com.dynamicg.homebuttonlauncher.tools.DialogHelper;

/*
 * TODO use gz not txt
 */
public class GoogleDriveBackupRestoreHelper {

	public static final String GOOGLE_DRIVE_FOLDER_NAME = "HomeButtonLauncher";
	public static final String GOOGLE_DRIVE_FILE_NAME = "settings.txt";
	public static final String SEPARATOR = "|";
	public static final String NL = "\n";
	public static final String WINNL = "\r\n";

	private static final Logger log = new Logger(GoogleDriveBackupRestoreHelper.class);

	private static WeakReference<MainActivityHome> refActivity;
	private static WeakReference<Dialog> refDialog;

	private final MainActivityHome activity;
	private final Context context;
	private final PreferencesDialog dialog;

	public GoogleDriveBackupRestoreHelper(MainActivityHome activity, PreferencesDialog dialog) {
		this.activity = activity;
		this.context = activity;
		this.dialog = dialog;
	}

	public void dispatch(int what) {
		if (!GoogleDriveUtil.isPluginAvailable(context)) {
			GoogleDriveUtil.alertMissingPlugin(context);
		}
		else if (what==MenuGlobals.DRIVE_BACKUP) {
			OnClickListenerDialogWrapper okListener = new OnClickListenerDialogWrapper(context) {
				@Override
				public void onClickImpl(DialogInterface d, int which) {
					startBackup();
				}
			};
			DialogHelper.confirm(context, R.string.prefsDriveBackup, okListener);
		}
		else if (what==MenuGlobals.DRIVE_RESTORE) {
			OnClickListenerDialogWrapper okListener = new OnClickListenerDialogWrapper(context) {
				@Override
				public void onClickImpl(DialogInterface d, int which) {
					triggerImport();
				}
			};
			DialogHelper.confirm(context, R.string.prefsDriveRestore, okListener);
		}
	}

	private File prepareBackupFile() throws Exception {
		File file = new File(context.getFilesDir(), GOOGLE_DRIVE_FILE_NAME);
		if (!file.exists()) {
			file.createNewFile();
		}
		file.setReadable(true, false); // read=true, owner=false
		file.setWritable(true, false);
		return file;
	}

	private void exportToFile(File file) throws Exception {
		final StringBuilder sb = new StringBuilder();
		final ArrayList<String> sharedPrefNames = HomeLauncherBackupAgent.getSharedPrefNames(activity);
		for (String entryGroup:sharedPrefNames) {
			SharedPreferences sharedPreferences = context.getSharedPreferences(entryGroup, Context.MODE_PRIVATE);
			Map<String, ?> all = sharedPreferences.getAll();
			for (String entryKey:all.keySet()) {

				String entryType = all.get(entryKey).getClass().getSimpleName();

				String entryValue = all.get(entryKey).toString();
				if (entryValue.contains(SEPARATOR)) {
					entryValue = entryValue.replace(SEPARATOR, "_");
				}

				log.debug("backup value", entryGroup, entryKey, entryType, entryValue);

				sb.append(entryGroup);
				sb.append(SEPARATOR);
				sb.append(entryKey);
				sb.append(SEPARATOR);
				sb.append(entryType);
				sb.append(SEPARATOR);
				sb.append(entryValue);
				sb.append(NL);
			}
		}

		FileUtil.writePlain(file, sb.toString());
	}

	private void startBackup() {
		try {
			File file = prepareBackupFile();
			exportToFile(file);
			log.debug("BACKUP", file);
			GoogleDriveUtil.upload(context, file);
		}
		catch (Throwable t) {
			DialogHelper.showCrashReport(context, t);
		}
	}

	private void triggerImport() {
		try {
			refActivity = new WeakReference<MainActivityHome>(activity);
			refDialog = new WeakReference<Dialog>(dialog);
			GoogleDriveUtil.startDownload(activity, prepareBackupFile());
		}
		catch (Throwable t) {
			DialogHelper.showCrashReport(context, t);
		}
	}

	private static String getToken(String[] tokens, int index) {
		return tokens!=null && tokens.length>index ? tokens[index] : "";
	}

	private static void restoreSettings(Context context, File file) throws Exception {
		final HashMap<String, Editor> editors = new HashMap<String, Editor>();
		String body = FileUtil.getContentAsString(file);
		if (body==null||body.length()==0) {
			return;
		}
		body = body.replace(WINNL, NL);

		final String[] lines = body.split(NL);
		if (lines==null||lines.length==0) {
			return;
		}

		for (String line:lines) {
			if (line==null || line.length()==0 || line.startsWith("#")) {
				continue;
			}
			String[] tokens = line.split("\\"+SEPARATOR);
			String entryGroup = getToken(tokens, 0);
			String entryKey = getToken(tokens, 1);
			String entryType = getToken(tokens, 2);
			String entryValue = getToken(tokens, 3);

			if (entryGroup.length()==0) {
				continue;
			}

			if (!editors.containsKey(entryGroup)) {
				SharedPreferences prefs = context.getSharedPreferences(entryGroup, Context.MODE_PRIVATE);
				Editor edit = prefs.edit();
				edit.clear(); // initially reset all existing entries for each shared pref
				editors.put(entryGroup, edit);
			}

			log.debug("restore value", entryGroup, entryType, entryKey, entryValue);
			Editor edit = editors.get(entryGroup);
			if ("Integer".equals(entryType)) {
				edit.putInt(entryKey, Integer.valueOf(entryValue));
			}
			else if ("Boolean".equals(entryType)) {
				edit.putBoolean(entryKey, Boolean.valueOf(entryValue));
			}
			else if ("String".equals(entryType)) {
				edit.putString(entryKey, entryValue);
			}
		}

		// commit all
		for (Editor edit:editors.values()) {
			edit.commit();
		}

		file.delete();
	}

	public static void restoreFromFile(Intent data) {
		String path = data!=null ? data.getStringExtra(GoogleDriveGlobals.KEY_FNAME_ABS) : null;
		if (path==null || path.length()==0) {
			return;
		}
		File file = new File(path);
		MainActivityHome activity = refActivity!=null ? refActivity.get() : null;

		if (file!=null && activity!=null) {
			try {
				restoreSettings(activity, file);
				Toast.makeText(activity, GoogleDriveUtil.MSG_TOAST_DONE, Toast.LENGTH_SHORT).show();
				refDialog.get().dismiss();
				activity.finish();
			}
			catch (Throwable t) {
				DialogHelper.showCrashReport(activity, t);
			}
		}
	}

}
