package com.dynamicg.homebuttonlauncher.adapter;

import android.view.View;
import android.view.ViewGroup;

import com.dynamicg.homebuttonlauncher.AppEntry;
import com.dynamicg.homebuttonlauncher.AppListContainer;
import com.dynamicg.homebuttonlauncher.MainActivityHome;
import com.dynamicg.homebuttonlauncher.tools.widgets.WidgetHelper;

public class AppListAdapterMain extends AppListAdapter {

	public AppListAdapterMain(MainActivityHome activity, AppListContainer apps) {
		super(activity, apps);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		AppEntry entry = applist.get(position);
		if (entry.isWidget()) {
			return WidgetHelper.getWidgetView(activity, inflater, entry);
		}

		final View row = getOrCreateView(convertView);
		bindView(position, applist.get(position), row);
		return row;
	}

}
