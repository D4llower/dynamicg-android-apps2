package com.dynamicg.homebuttonlauncher;

import java.util.List;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dynamicg.homebuttonlauncher.dialog.SizePrefsHelper;
import com.dynamicg.homebuttonlauncher.preferences.PrefSettings;
import com.dynamicg.homebuttonlauncher.tools.IconProvider;
import com.dynamicg.homebuttonlauncher.tools.LargeIconLoader;

public class AppListAdapter extends BaseAdapter {

	protected final List<AppEntry> applist;
	protected final LayoutInflater inflater;
	private final int labelSize;
	private final boolean forEditor;
	protected final int iconSizePx;
	protected final int appEntryLayoutId;
	private final LargeIconLoader iconLoader;

	/*
	 * for main screen
	 */
	public AppListAdapter(Activity activity, List<AppEntry> apps, PrefSettings settings) {
		this.applist = apps;
		this.inflater = activity.getLayoutInflater();
		this.forEditor = false;
		this.labelSize = settings.getLabelSize();
		this.iconSizePx = IconProvider.getSizePX(settings.getIconSize());
		this.appEntryLayoutId = settings.getAppEntryLayoutId();
		this.iconLoader = LargeIconLoader.createInstance(activity, settings);
	}

	/*
	 * for add/remove
	 */
	public AppListAdapter(Activity activity, List<AppEntry> apps, int viewId) {
		this.applist = apps;
		this.inflater = activity.getLayoutInflater();
		this.forEditor = true;
		this.labelSize = SizePrefsHelper.DEFAULT_LABEL_SIZE;
		this.iconSizePx = IconProvider.getDefaultSizePX();
		this.appEntryLayoutId = viewId;
		this.iconLoader = null;
	}

	@Override
	public int getCount() {
		return applist.size();
	}

	@Override
	public Object getItem(int position) {
		return applist.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		final AppEntry appEntry = applist.get(position);
		final TextView row;
		if (convertView==null) {
			row = (TextView)inflater.inflate(appEntryLayoutId, null);
		}
		else {
			row = (TextView)convertView;
		}

		if (this.labelSize==0) {
			row.setText("");
			row.setTextSize(0);
		}
		else {
			row.setText(appEntry.getLabel());
			row.setTextSize(this.labelSize);
		}

		Drawable icon = appEntry.getIcon(iconSizePx, iconLoader);
		if (appEntryLayoutId==R.layout.app_entry_compact) {
			// icon on top
			row.setCompoundDrawablesWithIntrinsicBounds(null, icon, null, null);
		}
		else {
			// icon left
			row.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
		}

		if (forEditor) {
			appEntry.decorateSelection(row);
		}
		return row;
	}

}
