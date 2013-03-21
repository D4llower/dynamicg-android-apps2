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
import android.text.InputType;

import com.dynamicg.common.Logger;
import com.dynamicg.common.SystemUtil;
import com.dynamicg.homebuttonlauncher.AppEntry;
import com.dynamicg.homebuttonlauncher.GlobalContext;
import com.dynamicg.homebuttonlauncher.HBLConstants;
import com.dynamicg.homebuttonlauncher.dialog.AppConfigDialog;
import com.dynamicg.homebuttonlauncher.tools.DialogHelper;
import com.dynamicg.homebuttonlauncher.tools.DialogHelper.TextEditorListener;

// TODO ## icons: handle "load error" (empty icon)
public class ShortcutHelper {

	private static final Logger log = new Logger(ShortcutHelper.class);

	private static final String KEY_SC_MAXID = "sc-max";
	private static final String PNG = ".png";

	private static WeakReference<AppConfigDialog> dialogRef;

	private static File iconDir;

	public static void storeRef(AppConfigDialog appConfigDialog) {
		dialogRef = new WeakReference<AppConfigDialog>(appConfigDialog);
	}

	public static boolean isShortcutComponent(String component) {
		return component.startsWith(HBLConstants.SHORTCUT_PREFIX) && component.contains(HBLConstants.SHORTCUT_SEPARATOR);
	}

	public static String getShortcutId(String component) {
		return component.substring(0, component.indexOf(HBLConstants.SHORTCUT_SEPARATOR));
	}

	public static void shortcutSelected(Intent data) {
		final AppConfigDialog dialog = dialogRef!=null ? dialogRef.get() : null;
		final Context context = dialog!=null ? dialog.getContext() : null;

		if (context==null || data==null) {
			return;
		}

		Bundle bundle = data.getExtras();
		if (bundle==null) {
			return;
		}

		final Intent intent = (Intent)bundle.getParcelable(Intent.EXTRA_SHORTCUT_INTENT);
		final Bitmap bitmap = (Bitmap)bundle.getParcelable(Intent.EXTRA_SHORTCUT_ICON);
		final String name = bundle.getString(Intent.EXTRA_SHORTCUT_NAME);
		if (intent==null) {
			return;
		}

		log.debug("SHORTCUT", name, bitmap, intent);

		TextEditorListener callback = new DialogHelper.TextEditorListener() {
			@Override
			public void onTextChanged(String text) {
				save(dialog, intent, bitmap, text);
			}
		};
		DialogHelper.openLabelEditor(context, name, InputType.TYPE_TEXT_FLAG_CAP_WORDS, callback);
	}

	private static void save(AppConfigDialog dialog, Intent intent, Bitmap bitmap, String label) {
		final SharedPreferences prefs = GlobalContext.prefSettings.sharedPrefs;
		final int nextid = prefs.getInt(KEY_SC_MAXID, 0) + 1;
		final String shortcutId = HBLConstants.SHORTCUT_PREFIX+nextid;

		final String intentString = intent.toUri(0);
		Editor edit = prefs.edit();
		edit.putInt(KEY_SC_MAXID, nextid);
		edit.putString(shortcutId, intentString);
		edit.commit();

		if (bitmap!=null) {
			saveIcon(dialog.getContext(), shortcutId, bitmap);
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
