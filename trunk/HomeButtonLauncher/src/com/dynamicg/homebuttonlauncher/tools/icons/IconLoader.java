package com.dynamicg.homebuttonlauncher.tools.icons;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.dynamicg.homebuttonlauncher.AppEntry;
import com.dynamicg.homebuttonlauncher.GlobalContext;

public class IconLoader {

	private final Context context;
	private final int iconSizePx;
	private final boolean forMainScreen;
	private final LargeIconLoader largeIconLoader;

	public IconLoader(Context context, int iconSizePx, boolean forMainScreen) {
		this.context = context;
		this.iconSizePx = iconSizePx;
		this.forMainScreen = forMainScreen;
		if (forMainScreen) {
			this.largeIconLoader = LargeIconLoader.createInstance(context, GlobalContext.prefSettings);
		}
		else {
			this.largeIconLoader = null;
		}
	}

	public Drawable getIcon(AppEntry appEntry) {
		Drawable icon = null;

		if (appEntry.shortcut) {
			icon = ShortcutHelper.loadIcon(context, appEntry, iconSizePx); // note this returns "scaled"
		}
		else {
			if (largeIconLoader!=null) {
				Drawable appicon = largeIconLoader.getLargeIcon(appEntry);
				icon = IconProvider.scale(appicon, iconSizePx);
			}

			if (icon==null) {
				Drawable appicon = appEntry.resolveInfo.loadIcon(GlobalContext.packageManager);
				icon = IconProvider.scale(appicon, iconSizePx);
			}
		}

		if (forMainScreen) {
			GlobalContext.icons.put(appEntry.getComponent(), icon);
		}

		return icon;
	}

}
