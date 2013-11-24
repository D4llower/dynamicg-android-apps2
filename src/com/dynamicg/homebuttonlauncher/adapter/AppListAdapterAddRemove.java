package com.dynamicg.homebuttonlauncher.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import com.dynamicg.homebuttonlauncher.AppEntry;
import com.dynamicg.homebuttonlauncher.AppListContainer;
import com.dynamicg.homebuttonlauncher.R;

public class AppListAdapterAddRemove extends AppListAdapter {

	public AppListAdapterAddRemove(Activity activity, AppListContainer apps) {
		super(activity, apps, R.layout.app_entry_default);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final View row = getOrCreateView(convertView);
		final AppEntry appEntry = applist.get(position);
		bindView(position, appEntry, row);
		appEntry.decorateSelection(row);
		return row;
	}

}
