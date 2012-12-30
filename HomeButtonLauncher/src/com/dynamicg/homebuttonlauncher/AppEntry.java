package com.dynamicg.homebuttonlauncher;

import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.view.View;

public class AppEntry {

	private final PackageManager packageManager;
	private final String component;
	private final String label;
	private final ResolveInfo resolverInfo;

	private Drawable icon;
	private boolean checked;

	public AppEntry(PackageManager packageManager, ResolveInfo resolveInfo) {
		this.packageManager = packageManager;
		this.resolverInfo = resolveInfo;
		this.component = AppHelper.getComponentName(resolveInfo);
		this.label = toString(resolveInfo.loadLabel(packageManager), this.component);
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

	public Drawable getIcon() {
		if (icon==null) {
			Drawable appicon = resolverInfo.loadIcon(packageManager);
			icon = IconProvider.scale(appicon);
		}
		return icon;
	}

}
