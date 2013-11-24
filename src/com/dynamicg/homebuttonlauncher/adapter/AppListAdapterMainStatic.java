package com.dynamicg.homebuttonlauncher.adapter;

import java.util.Arrays;
import java.util.List;

import android.view.View;
import android.view.ViewGroup;

import com.dynamicg.homebuttonlauncher.AppEntry;
import com.dynamicg.homebuttonlauncher.AppListContainer;
import com.dynamicg.homebuttonlauncher.MainActivityHome;
import com.dynamicg.homebuttonlauncher.tools.WidgetHelper;

public class AppListAdapterMainStatic extends AppListAdapter {

	private final List<View> list;

	public AppListAdapterMainStatic(MainActivityHome activity, AppListContainer apps) {
		super(activity, apps);
		list = Arrays.asList(new View[apps.size()]);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		AppEntry entry = applist.get(position);
		if (entry.isWidget()) {
			return WidgetHelper.getWidgetView(activity, inflater, entry);
		}

		if (list.get(position)==null) {
			final View row = getOrCreateView(null); // make sure we don't recycle views
			bindView(position, entry, row);
			list.set(position, row);
		}
		return list.get(position);
	}

}
