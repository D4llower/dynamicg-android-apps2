package com.dynamicg.homebuttonlauncher;

import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.dynamicg.homebuttonlauncher.tools.AppHelper;
import com.dynamicg.homebuttonlauncher.tools.ShortcutHelper;
import com.dynamicg.homebuttonlauncher.tools.icons.IconLoader;

public class AppEntry {

	public static final int ID_SHORTCUT=1;
	public static final int ID_WIDGET=2;

	private final String component;
	public final ResolveInfo resolveInfo;
	public final String label;
	public final int sortnr;
	public final boolean shortcut;
	public final boolean widget;

	private Drawable icon;
	private boolean checked;

	public AppEntry(ResolveInfo resolveInfo, int sortnr, boolean forMainScreen) {
		this.resolveInfo = resolveInfo;
		this.component = AppHelper.getComponentName(resolveInfo);
		this.sortnr = sortnr;
		this.shortcut = false;
		this.widget = false;

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

	public AppEntry(String component, int sortnr, boolean forMainScreen, int which) {
		this.resolveInfo = null;
		this.component = component;
		this.shortcut = which==ID_SHORTCUT;
		this.widget = which==ID_WIDGET;
		this.label = ShortcutHelper.getLabel(component);
		this.sortnr = sortnr;
		if (forMainScreen) {
			icon = GlobalContext.icons.get(this.component);
		}
	}

	// for extra items like "Widget"
	public AppEntry(String component, int sortnr, String label) {
		this.resolveInfo = null;
		this.component = component;
		this.label = label;
		this.sortnr = sortnr;
		this.shortcut = true;
		this.widget = false;
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

	public Drawable getIcon(IconLoader iconLoader) {
		if (icon==null) {
			icon = iconLoader.getIcon(this);
		}
		return icon;
	}

	public boolean isIconLoaded() {
		return icon!=null;
	}

	public Drawable getIconDrawable() {
		return icon;
	}

	public int getAppWidgetId() {
		if (!widget) {
			return 0;
		}
		String id = ShortcutHelper.getShortcutDataPart(component);
		try {
			return Integer.parseInt(id);
		}
		catch (NumberFormatException e) {
			return 0;
		}
	}
}
