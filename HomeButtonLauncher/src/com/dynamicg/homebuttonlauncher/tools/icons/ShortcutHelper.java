package com.dynamicg.homebuttonlauncher.tools.icons;

import java.lang.ref.WeakReference;
import java.net.URISyntaxException;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.dynamicg.homebuttonlauncher.AppEntry;
import com.dynamicg.homebuttonlauncher.GlobalContext;
import com.dynamicg.homebuttonlauncher.HBLConstants;
import com.dynamicg.homebuttonlauncher.MainActivityHome;
import com.dynamicg.homebuttonlauncher.dialog.AppConfigDialog;

public class ShortcutHelper {

	private static final String KEY_ID = "id";

	private static WeakReference<MainActivityHome> activityRef;
	private static WeakReference<AppConfigDialog> dialogRef;

	public static void store(MainActivityHome activity, AppConfigDialog appConfigDialog) {
		activityRef = new WeakReference<MainActivityHome>(activity);
		dialogRef = new WeakReference<AppConfigDialog>(appConfigDialog);
	}

	public static Intent getIntent(Context context, AppEntry entry) throws URISyntaxException {
		SharedPreferences prefs = GlobalContext.getShortcutSettings(context);
		String key = entry.getComponent() + HBLConstants.SHORTCUT_SEPARATOR + entry.label;
		String uri = prefs.getString(key, null);
		return Intent.parseUri(uri, 0);
	}

	public static void shortcutSelected(Intent data) {
		Context context = activityRef!=null ? activityRef.get() : null;
		AppConfigDialog dialog = dialogRef!=null ? dialogRef.get() : null;

		if (context==null) {
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

		// TODO ## input label
		// TODO ## save Bitmap

		final SharedPreferences prefs = GlobalContext.getShortcutSettings(context);
		final int nextid = prefs.getInt(KEY_ID, 0) + 1;
		final String shortcutId = HBLConstants.SHORTCUT_PREFIX+nextid;

		final String intentString = intent.toUri(0);

		final String label = "{"+shortcutId+"}";
		Editor edit = prefs.edit();
		edit.putInt(KEY_ID, nextid);
		edit.putString(shortcutId, intentString+HBLConstants.SHORTCUT_SEPARATOR+label);
		edit.commit();

		dialog.saveShortcut(shortcutId+HBLConstants.SHORTCUT_SEPARATOR+label);

	}

}
