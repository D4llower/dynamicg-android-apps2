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

	private static final String KEY_ID = "id";
	private static final String PNG = ".png";

	private static WeakReference<MainActivityHome> activityRef;
	private static WeakReference<AppConfigDialog> dialogRef;

	private static File iconDir;

	public static void store(MainActivityHome activity, AppConfigDialog appConfigDialog) {
		activityRef = new WeakReference<MainActivityHome>(activity);
		dialogRef = new WeakReference<AppConfigDialog>(appConfigDialog);
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
		// TODO ## implement "remove" (delete prefsShortcut and remove icon file)

		final SharedPreferences prefs = GlobalContext.getShortcutSettings(context);
		final int nextid = prefs.getInt(KEY_ID, 0) + 1;
		final String shortcutId = HBLConstants.SHORTCUT_PREFIX+nextid;
		final String label = "{"+shortcutId+"}";

		final String intentString = intent.toUri(0);
		Editor edit = prefs.edit();
		edit.putInt(KEY_ID, nextid);
		edit.putString(shortcutId, intentString);
		edit.commit();

		if (bitmap!=null) {
			saveIcon(context, shortcutId, bitmap);
		}

		dialog.saveShortcut(shortcutId+HBLConstants.SHORTCUT_SEPARATOR+label);
	}

	public static Intent getIntent(Context context, AppEntry entry) throws URISyntaxException {
		SharedPreferences prefs = GlobalContext.getShortcutSettings(context);
		String key = entry.getShortcutId();
		log.debug("shortcut/getIntent", key);
		String uri = prefs.getString(key, null);
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
		File file = new File(iconDir, appEntry.getShortcutId()+PNG);
		return Drawable.createFromPath(file.getAbsolutePath());
	}

}
