package com.dynamicg.homebuttonlauncher.tools.icons;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;
import java.net.URISyntaxException;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;

import com.dynamicg.common.FileUtil;
import com.dynamicg.common.Logger;
import com.dynamicg.common.SystemUtil;
import com.dynamicg.homebuttonlauncher.AppEntry;
import com.dynamicg.homebuttonlauncher.GlobalContext;
import com.dynamicg.homebuttonlauncher.HBLConstants;
import com.dynamicg.homebuttonlauncher.dialog.AppConfigDialog;
import com.dynamicg.homebuttonlauncher.tools.DialogHelper;
import com.dynamicg.homebuttonlauncher.tools.DialogHelper.TextEditorListener;
import com.dynamicg.homebuttonlauncher.tools.drive.Hex;

/*
 * shortcut data:
 * "sc-<id>|<iconres>#<label>" is the component, used as key on the 'shortlist' settings
 * "sc-<id>" is the shortcut id, used as key on 'prefSettings' to save the intent and to write the icon to the disk
 * "<iconres>" is something like "com.google.android.apps.docs/drawable/ic_type_folder_big" according to shortcut_icon_resource
 */
public class ShortcutHelper {

	private static final Logger log = new Logger(ShortcutHelper.class);

	private static final String SHORTCUT_PREFIX = "sc-";
	private static final String SEPARATOR_RES = "|";
	private static final String SEPARATOR_LABEL = "#";
	private static final String RESOURCE_URI_PREFIX = "android.resource://";

	private static final String KEY_SC_MAXID = "sc-max";
	private static final String PNG = ".png";

	private static WeakReference<AppConfigDialog> dialogRef;

	private static File iconDir;

	public static void storeRef(AppConfigDialog appConfigDialog) {
		dialogRef = new WeakReference<AppConfigDialog>(appConfigDialog);
	}

	public static boolean isShortcutComponent(String component) {
		return component.startsWith(SHORTCUT_PREFIX) && component.contains(SEPARATOR_RES);
	}

	public static String getShortcutId(String component) {
		return component.substring(0, component.indexOf(SEPARATOR_RES));
	}

	public static String getLabel(String component) {
		try {
			return component.substring(component.indexOf(SEPARATOR_LABEL)+1, component.length());
		}
		catch (IndexOutOfBoundsException e) {
			return component; // corrupt prefs?
		}
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
		final Bitmap icon = (Bitmap)bundle.getParcelable(Intent.EXTRA_SHORTCUT_ICON);
		final Intent.ShortcutIconResource iconResource = bundle.getParcelable(Intent.EXTRA_SHORTCUT_ICON_RESOURCE);
		final String name = bundle.getString(Intent.EXTRA_SHORTCUT_NAME);
		if (intent==null) {
			return;
		}

		log.debug("SHORTCUT", name, icon, iconResource, intent);

		TextEditorListener callback = new DialogHelper.TextEditorListener() {
			@Override
			public void onTextChanged(String text) {
				save(dialog, icon, iconResource, intent, text);
			}
		};
		DialogHelper.openLabelEditor(context, name, InputType.TYPE_TEXT_FLAG_CAP_WORDS, callback);
	}

	private static void save(AppConfigDialog dialog, Bitmap bitmap, Intent.ShortcutIconResource iconResource, Intent intent, String label) {
		final SharedPreferences prefs = GlobalContext.prefSettings.sharedPrefs;
		final int nextid = prefs.getInt(KEY_SC_MAXID, 0) + 1;
		final String shortcutId = SHORTCUT_PREFIX+nextid;

		final String intentString = intent.toUri(0);
		Editor edit = prefs.edit();
		edit.putInt(KEY_SC_MAXID, nextid);
		edit.putString(shortcutId, intentString);
		edit.apply();

		String iconpath = iconResource!=null ? iconResource.resourceName.replace(":", "/") : "";
		String componentToSave = shortcutId + SEPARATOR_RES + iconpath + SEPARATOR_LABEL + label;
		dialog.saveShortcut(componentToSave);

		if (bitmap!=null) {
			saveIcon(dialog.getContext(), shortcutId, bitmap);
		}
	}

	public static Intent getIntent(AppEntry entry) throws URISyntaxException {
		SharedPreferences prefs = GlobalContext.prefSettings.sharedPrefs;
		String shortcutId = getShortcutId(entry.getComponent());
		log.debug("shortcut/getIntent", shortcutId);
		String uri = prefs.getString(shortcutId, null);
		return Intent.parseUri(uri, 0);
	}

	public static void initIconDir(Context context) {
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
			SystemUtil.dumpError(t);
		}
	}

	public static Drawable loadIcon(Context context, AppEntry appEntry, LargeIconLoader largeIconLoader, int iconSizePx) {
		Drawable icon = null;
		final String component = appEntry.getComponent();
		final String respath = component.substring(component.indexOf(SEPARATOR_RES)+1, component.indexOf(SEPARATOR_LABEL));
		if (respath.length()>0) {
			// icon resource, format "android.resource://[package]/[res type]/[res name]"
			if (largeIconLoader!=null) {
				icon = largeIconLoader.getLargeIcon(respath);
			}
			if (icon==null) {
				// this is also the fallback if "large icon loader" fails
				Uri uri = Uri.parse(RESOURCE_URI_PREFIX+respath);
				log.trace("shortcut/get remote icon", uri);
				try {
					Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
					icon = new BitmapDrawable(GlobalContext.resources, bitmap);
				}
				catch (Throwable t) {
					SystemUtil.dumpError(t);
				}
			}
		}
		else {
			// png file on disk
			initIconDir(context);
			String shortcutId = getShortcutId(component);
			File file = new File(iconDir, shortcutId+PNG);
			icon = Drawable.createFromPath(file.getAbsolutePath());
		}

		if (icon==null) {
			// default icon if something fails
			icon = IconProvider.getDefaultIcon();
		}
		return IconProvider.scale(icon, iconSizePx);
	}

	public static void removeShortcuts(ArrayList<String> shortcutIds) {
		Editor edit = GlobalContext.prefSettings.sharedPrefs.edit();
		for (String shortcutId:shortcutIds) {
			edit.remove(shortcutId);
		}
		edit.commit();

		if (iconDir==null) {
			// if we get here the icondir should already have been initialised
			// (since initial display of the according item has already occurred)
			return;
		}
		for (String shortcutId:shortcutIds) {
			File file = new File(iconDir, shortcutId+PNG);
			boolean deleted = file.delete();
			log.debug("delete icon file", file, deleted);
		}
	}

	public static boolean isShortcutWithLocalIcon(String entryGroup, String entryKey) {
		return entryGroup.startsWith(HBLConstants.PREFS_APPS)
				&& entryKey.startsWith(SHORTCUT_PREFIX)
				&& entryKey.contains(SEPARATOR_RES+SEPARATOR_LABEL)
				;
	}

	public static String encodeIcon(String shortcutId) throws Exception {
		File file = new File(iconDir, shortcutId+PNG);
		if (!file.exists()) {
			return "";
		}
		ByteArrayOutputStream os=FileUtil.getContent(file);
		return Hex.encodeHex(os.toByteArray(), false);
	}

	public static void restoreIcon(String shortcutId, String encodedIconData) throws Exception {
		if (encodedIconData==null || encodedIconData.length()==0) {
			return;
		}
		File file = new File(iconDir, shortcutId+PNG);
		FileUtil.writeToFile(file, Hex.decodeHex(encodedIconData));
		log.debug("icon restore done", file, file.length());
	}

}
