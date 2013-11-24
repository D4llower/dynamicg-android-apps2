package com.dynamicg.homebuttonlauncher.adapter;

import java.util.Arrays;
import java.util.List;

import android.view.View;
import android.view.ViewGroup;

import com.dynamicg.homebuttonlauncher.AppEntry;
import com.dynamicg.homebuttonlauncher.AppListContainer;
import com.dynamicg.homebuttonlauncher.MainActivityHome;
import com.dynamicg.homebuttonlauncher.tools.widgets.WidgetsSelector;

public class AppListAdapterMainStatic extends AppListAdapter {

	private final List<View> list;
	private MainActivityHome activity;

	public AppListAdapterMainStatic(MainActivityHome activity, AppListContainer apps) {
		super(activity, apps);
		this.activity = activity;
		list = Arrays.asList(new View[apps.size()]);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO add to all adapters
		AppEntry entry = applist.get(position);
		int appWidgetId = entry.getAppWidgetId();
		if (appWidgetId>0) {
			return WidgetsSelector.getWidgetView(activity, inflater, appWidgetId);
		}

		if (list.get(position)==null) {
			final View row = getOrCreateView(null); // make sure we don't recycle views
			bindView(position, applist.get(position), row);
			list.set(position, row);
		}
		return list.get(position);
	}

}
