package com.dynamicg.homebuttonlauncher;

import java.util.List;

import android.app.Activity;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dynamicg.homebuttonlauncher.dialog.SizePrefsHelper;
import com.dynamicg.homebuttonlauncher.preferences.PrefSettings;
import com.dynamicg.homebuttonlauncher.tools.IconProvider;

public class AppListAdapter extends BaseAdapter {

	private final List<AppEntry> applist;
	private final LayoutInflater inflater;
	private final int labelSize;
	private final boolean forEditor;
	private final int iconSizePx;
	private final int appEntryLayoutId;

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
	}

	/*
	 * for add/remove
	 */
	public AppListAdapter(Activity activity, List<AppEntry> apps) {
		this.applist = apps;
		this.inflater = activity.getLayoutInflater();
		this.forEditor = true;
		this.labelSize = SizePrefsHelper.DEFAULT_LABEL_SIZE;
		this.iconSizePx = IconProvider.getDefaultSizePX();
		this.appEntryLayoutId = R.layout.app_entry_default;
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

		row.setText(appEntry.getLabel());
		row.setTextSize(this.labelSize);

		Drawable icon = appEntry.getIcon(iconSizePx);
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
			row.setTypeface(Typeface.DEFAULT, Typeface.ITALIC);
		}
		return row;
	}

}
