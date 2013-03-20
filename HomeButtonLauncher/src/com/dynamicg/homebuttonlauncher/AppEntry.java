package com.dynamicg.homebuttonlauncher;

import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.dynamicg.homebuttonlauncher.tools.AppHelper;
import com.dynamicg.homebuttonlauncher.tools.icons.IconProvider;
import com.dynamicg.homebuttonlauncher.tools.icons.LargeIconLoader;

public class AppEntry {

	private final String component;
	public final ResolveInfo resolveInfo;
	public final String label;
	public final int sortnr;

	private Drawable icon;
	private boolean checked;

	public AppEntry(ResolveInfo resolveInfo, int sortnr, boolean forMainScreen) {
		this.resolveInfo = resolveInfo;
		this.component = AppHelper.getComponentName(resolveInfo);
		this.sortnr = sortnr;

		if (forMainScreen) {
			String label = GlobalContext.labels.get(component);
			if (label==null) {
				label = toString(resolveInfo.loadLabel(GlobalContext.packageManager), this.component);
				GlobalContext.labels.put(component, label);
			}
			this.label = label;
			this.icon = GlobalContext.icons.get(component);
		}
		else {
			this.label = toString(resolveInfo.loadLabel(GlobalContext.packageManager), this.component);
		}
	}

	private static String toString(CharSequence c, String nullvalue) {
		return c!=null ? c.toString() : nullvalue;
	}

	public String getLabel() {
		return label;
	}

	public String getComponent() {
		return component;
	}

	public String getPackage() {
		return component.split("/")[0];
	}

	public boolean isChecked() {
		return checked;
	}

	public void flipCheckedState() {
		checked = !checked;
	}

	public void decorateSelection(View view) {
		view.setBackgroundResource(checked?R.drawable.app_selector_shape:0);
	}

	public Drawable getIcon(int iconSizePx, LargeIconLoader largeIconLoader, boolean forMainScreen) {

		if (icon!=null) {
			return icon;
		}

		if (largeIconLoader!=null) {
			Drawable appicon = largeIconLoader.getLargeIcon(this);
			icon = IconProvider.scale(appicon, iconSizePx);
		}

		if (icon==null) {
			Drawable appicon = resolveInfo.loadIcon(GlobalContext.packageManager);
			icon = IconProvider.scale(appicon, iconSizePx);
		}

		if (forMainScreen) {
			GlobalContext.icons.put(component, icon);
		}

		return icon;
	}

	public boolean isIconLoaded() {
		return icon!=null;
	}

	public Drawable getIconDrawable() {
		return icon;
	}

}
