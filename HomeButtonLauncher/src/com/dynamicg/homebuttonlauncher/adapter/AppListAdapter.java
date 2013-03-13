package com.dynamicg.homebuttonlauncher.adapter;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dynamicg.homebuttonlauncher.AppEntry;
import com.dynamicg.homebuttonlauncher.AppListContainer;
import com.dynamicg.homebuttonlauncher.R;
import com.dynamicg.homebuttonlauncher.dialog.SizePrefsHelper;
import com.dynamicg.homebuttonlauncher.preferences.PrefSettings;
import com.dynamicg.homebuttonlauncher.tools.DialogHelper;
import com.dynamicg.homebuttonlauncher.tools.IconProvider;
import com.dynamicg.homebuttonlauncher.tools.LargeIconLoader;

public abstract class AppListAdapter extends BaseAdapter {

	protected final AppListContainer applist;
	protected final LayoutInflater inflater;
	private final int labelSize;
	protected final int iconSizePx;
	protected final int appEntryLayoutId;
	private final LargeIconLoader iconLoader;

	private Integer noLabelGridPadding;

	/*
	 * for main screen
	 */
	public AppListAdapter(Activity activity, AppListContainer apps, PrefSettings settings) {
		this.applist = apps;
		this.inflater = activity.getLayoutInflater();
		this.labelSize = settings.getLabelSize();
		this.iconSizePx = IconProvider.getSizePX(settings.getIconSize());
		this.appEntryLayoutId = settings.getAppEntryLayoutId();
		this.iconLoader = LargeIconLoader.createInstance(activity, settings);
	}

	/*
	 * for add/remove
	 */
	public AppListAdapter(Activity activity, AppListContainer apps, int viewId) {
		this.applist = apps;
		this.inflater = activity.getLayoutInflater();
		this.labelSize = SizePrefsHelper.DEFAULT_LABEL_SIZE;
		this.iconSizePx = IconProvider.getDefaultSizePX();
		this.appEntryLayoutId = viewId;
		this.iconLoader = null;
	}

	@Override
	public abstract View getView(int position, View convertView, ViewGroup parent);

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

	private void noLabelPadding(TextView row) {
		// add some padding
		if (noLabelGridPadding==null) {
			noLabelGridPadding = DialogHelper.getDimension(inflater.getContext(), R.dimen.gridViewNoLabelIconPadding);
		}
		row.setCompoundDrawablePadding(noLabelGridPadding);
	}

	public TextView getOrCreateTextView(View convertView) {
		final TextView row;
		if (convertView==null) {
			row = (TextView)inflater.inflate(appEntryLayoutId, null);
			row.setTextSize(this.labelSize);
			if (this.labelSize==0 && appEntryLayoutId==R.layout.app_entry_compact) {
				noLabelPadding(row);
			}
		}
		else {
			row = (TextView)convertView;
		}
		return row;
	}

	public void bindView(AppEntry appEntry, TextView row) {
		if (this.labelSize==0) {
			row.setText("");
		}
		else {
			row.setText(appEntry.getLabel());
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
	}

}
