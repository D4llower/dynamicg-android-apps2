package com.dynamicg.homebuttonlauncher.tools.icons;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;
import java.net.URISyntaxException;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.dynamicg.common.Logger;
import com.dynamicg.common.SystemUtil;
import com.dynamicg.homebuttonlauncher.AppEntry;
import com.dynamicg.homebuttonlauncher.GlobalContext;
import com.dynamicg.homebuttonlauncher.HBLConstants;
import com.dynamicg.homebuttonlauncher.MainActivityHome;
import com.dynamicg.homebuttonlauncher.dialog.AppConfigDialog;

public class ShortcutHelper {

	private static final Logger log = new Logger(ShortcutHelper.class);

	private static final String KEY_SC_MAXID = "sc-max";
	private static final String PNG = ".png";

	private static WeakReference<MainActivityHome> activityRef;
	private static WeakReference<AppConfigDialog> dialogRef;

	private static File iconDir;

	public static void store(MainActivityHome activity, AppConfigDialog appConfigDialog) {
		activityRef = new WeakReference<MainActivityHome>(activity);
		dialogRef = new WeakReference<AppConfigDialog>(appConfigDialog);
	}

	public static boolean isShortcutComponent(String component) {
		return component.startsWith(HBLConstants.SHORTCUT_PREFIX) && component.contains(HBLConstants.SHORTCUT_SEPARATOR);
	}

	public static String getShortcutId(String component) {
		return component.substring(0, component.indexOf(HBLConstants.SHORTCUT_SEPARATOR));
	}

	public static void shortcutSelected(Intent data) {
		Context context = activityRef!=null ? activityRef.get() : null;
		AppConfigDialog dialog = dialogRef!=null ? dialogRef.get() : null;

		if (context==null || dialog==null || data==null) {
			return;
		}

		Bundle bundle = data.getExtras();
		if (bundle==null) {
			return;
		}


		Intent intent = (Intent)bundle.getParcelable(Intent.EXTRA_SHORTCUT_INTENT);
		Bitmap bitmap = (Bitmap)bundle.getParcelable(Intent.EXTRA_SHORTCUT_ICON);
		String name = bundle.getString(Intent.EXTRA_SHORTCUT_NAME);
		if (intent==null) {
			return;
		}

		log.debug("SHORTCUT", name, bitmap, intent);

		// TODO ## input and save label
		// TODO ## icons: handle "load error" (empty icon)

		final SharedPreferences prefs = GlobalContext.prefSettings.sharedPrefs;
		final int nextid = prefs.getInt(KEY_SC_MAXID, 0) + 1;
		final String shortcutId = HBLConstants.SHORTCUT_PREFIX+nextid;
		final String label = "{"+shortcutId+"}";

		final String intentString = intent.toUri(0);
		Editor edit = prefs.edit();
		edit.putInt(KEY_SC_MAXID, nextid);
		edit.putString(shortcutId, intentString);
		edit.commit();

		if (bitmap!=null) {
			saveIcon(context, shortcutId, bitmap);
		}

		dialog.saveShortcut(shortcutId+HBLConstants.SHORTCUT_SEPARATOR+label);
	}

	public static Intent getIntent(AppEntry entry) throws URISyntaxException {
		SharedPreferences prefs = GlobalContext.prefSettings.sharedPrefs;
		String shortcutId = getShortcutId(entry.getComponent());
		log.debug("shortcut/getIntent", shortcutId);
		String uri = prefs.getString(shortcutId, null);
		return Intent.parseUri(uri, 0);
	}

	private static void initIconDir(Context context) {
		if (iconDir==null) {
			iconDir = new File(context.getFilesDir(), "icons");
			if (!iconDir.exists()) {
				iconDir.mkdir();
			}
		}
	}

	private static void saveIcon(Context context, String shortcutId, Bitmap icon) {
		initIconDir(context);
		try {
			File file = new File(iconDir, shortcutId+PNG);
			FileOutputStream out = new FileOutputStream(file);
			icon.compress(Bitmap.CompressFormat.PNG, 100, out);
		} catch (Throwable t) {
			SystemUtil.dumpIfDevelopment(t);
		}
	}

	public static Drawable loadIcon(Context context, AppEntry appEntry) {
		initIconDir(context);
		String shortcutId = getShortcutId(appEntry.getComponent());
		File file = new File(iconDir, shortcutId+PNG);
		return Drawable.createFromPath(file.getAbsolutePath());
	}

	public static void deleteIcon(String component) {
		if (iconDir==null) {
			// if we get here the icondir should already have been initialised
			// (since initial display of the according item has already occurred)
			return;
		}
		String shortcutId = getShortcutId(component);
		File file = new File(iconDir, shortcutId+PNG);
		file.delete();
	}

}
