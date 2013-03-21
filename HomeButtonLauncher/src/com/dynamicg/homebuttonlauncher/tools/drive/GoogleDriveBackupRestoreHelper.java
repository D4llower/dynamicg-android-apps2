package com.dynamicg.homebuttonlauncher.tools.drive;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.widget.Toast;

import com.dynamicg.common.Logger;
import com.dynamicg.homebuttonlauncher.GlobalContext;
import com.dynamicg.homebuttonlauncher.HBLConstants;
import com.dynamicg.homebuttonlauncher.MainActivityHome;
import com.dynamicg.homebuttonlauncher.OnClickListenerDialogWrapper;
import com.dynamicg.homebuttonlauncher.R;
import com.dynamicg.homebuttonlauncher.dialog.PreferencesDialog;
import com.dynamicg.homebuttonlauncher.preferences.HomeLauncherBackupAgent;
import com.dynamicg.homebuttonlauncher.tools.DialogHelper;
import com.dynamicg.homebuttonlauncher.tools.icons.ShortcutHelper;

public class GoogleDriveBackupRestoreHelper {

	public static final String GOOGLE_DRIVE_FOLDER_NAME = "HomeButtonLauncher";
	public static final String GOOGLE_DRIVE_FILE_NAME = "settings.xml.gz";
	public static final String GROUP_ICONS = "icons";

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
		else if (what==HBLConstants.MENU_DRIVE_BACKUP) {
			OnClickListenerDialogWrapper okListener = new OnClickListenerDialogWrapper(context) {
				@Override
				public void onClickImpl(DialogInterface d, int which) {
					startBackup();
				}
			};
			DialogHelper.confirm(context, R.string.prefsDriveBackup, okListener);
		}
		else if (what==HBLConstants.MENU_DRIVE_RESTORE) {
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
		final ArrayList<String> localIcons = new ArrayList<String>();
		final ArrayList<String> sharedPrefNames = HomeLauncherBackupAgent.getSharedPrefNames(activity);
		final XmlWriter writer = new XmlWriter(file);
		for (String entryGroup:sharedPrefNames) {
			SharedPreferences sharedPreferences = context.getSharedPreferences(entryGroup, Context.MODE_PRIVATE);
			Map<String, ?> all = sharedPreferences.getAll();
			for (String entryKey:all.keySet()) {
				final String entryType = all.get(entryKey).getClass().getSimpleName();
				final String entryValue = all.get(entryKey).toString();
				log.debug("backup value", entryGroup, entryKey, entryType, entryValue);
				Map<String, String> entry = new TreeMap<String, String>();
				entry.put(XmlGlobals.ENTRY_GROUP, entryGroup);
				entry.put(XmlGlobals.ENTRY_KEY, entryKey);
				entry.put(XmlGlobals.ENTRY_TYPE, entryType);
				entry.put(XmlGlobals.ENTRY_VALUE, entryValue);
				writer.add(entry);
				if (ShortcutHelper.isShortcutWithLocalIcon(entryGroup, entryKey)) {
					localIcons.add(ShortcutHelper.getShortcutId(entryKey));
				}
			}
		}
		if (localIcons.size()>0) {
			ShortcutHelper.initIconDir(context);
			for (String shortcutId:localIcons) {
				Map<String, String> entry = new TreeMap<String, String>();
				entry.put(XmlGlobals.ENTRY_GROUP, GROUP_ICONS);
				entry.put(XmlGlobals.ENTRY_KEY, shortcutId);
				entry.put(XmlGlobals.ENTRY_ICON_DATA, ShortcutHelper.encodeIcon(shortcutId));
				writer.add(entry);
			}
		}

		writer.close();
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

	private static boolean isEmpty(String s) {
		return s==null || s.length()==0;
	}

	private static void restoreSettings(Context context, File file) throws Exception {
		final HashMap<String, Editor> editors = new HashMap<String, Editor>();
		XmlReader reader = new XmlReader(file);
		List<Map<String, String>> content = reader.getContent(context);
		if (content==null||content.size()==0) {
			return;
		}

		log.trace("restoreSettings", content);

		for (Map<String, String> map:content) {
			String entryGroup = map.get(XmlGlobals.ENTRY_GROUP);
			String entryKey = map.get(XmlGlobals.ENTRY_KEY);
			String entryType = map.get(XmlGlobals.ENTRY_TYPE);
			String entryValue = map.get(XmlGlobals.ENTRY_VALUE);

			if (isEmpty(entryGroup) || isEmpty(entryKey)) {
				continue;
			}

			if (!editors.containsKey(entryGroup)) {
				SharedPreferences prefs = context.getSharedPreferences(entryGroup, Context.MODE_PRIVATE);
				Editor edit = prefs.edit();
				edit.clear(); // initially reset all existing entries for each shared pref
				editors.put(entryGroup, edit);
			}

			log.trace("restore value", entryGroup, entryType, entryKey, entryValue);
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
				GlobalContext.resetCache(); // make sure we don't retain icons scaled to previous size
				refDialog.get().dismiss();
				activity.finish();
			}
			catch (Throwable t) {
				DialogHelper.showCrashReport(activity, t);
			}
		}
	}

}
