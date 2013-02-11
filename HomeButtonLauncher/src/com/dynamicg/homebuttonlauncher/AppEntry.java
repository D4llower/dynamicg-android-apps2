package com.dynamicg.homebuttonlauncher;

import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.dynamicg.homebuttonlauncher.tools.AppHelper;
import com.dynamicg.homebuttonlauncher.tools.IconProvider;
import com.dynamicg.homebuttonlauncher.tools.LargeIconLoader;

public class AppEntry {

	private final PackageManager packageManager;
	private final String component;
	public final ResolveInfo resolveInfo;
	public final String label;
	public final int sortnr;

	private Drawable icon;
	private boolean checked;

	public AppEntry(PackageManager packageManager, ResolveInfo resolveInfo, int sortnr) {
		this.packageManager = packageManager;
		this.resolveInfo = resolveInfo;
		this.component = AppHelper.getComponentName(resolveInfo);
		this.label = toString(resolveInfo.loadLabel(packageManager), this.component);
		this.sortnr = sortnr;
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

	//TODO async loading of icons (?)
	public Drawable getIcon(int sizePX, LargeIconLoader largeIconLoader) {

		if (icon!=null) {
			return icon;
		}

		if (largeIconLoader!=null && icon==null) {
			Drawable appicon = largeIconLoader.getLargeIcon(this);
			icon = IconProvider.scale(appicon, sizePX);
		}

		if (icon==null) {
			Drawable appicon = resolveInfo.loadIcon(packageManager);
			icon = IconProvider.scale(appicon, sizePX);
		}

		return icon;
	}

}
