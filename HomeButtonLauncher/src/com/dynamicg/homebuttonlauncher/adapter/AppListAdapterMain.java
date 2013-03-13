package com.dynamicg.homebuttonlauncher.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dynamicg.homebuttonlauncher.AppListContainer;
import com.dynamicg.homebuttonlauncher.preferences.PrefSettings;

public class AppListAdapterMain extends AppListAdapter {

	public AppListAdapterMain(Activity activity, AppListContainer apps, PrefSettings settings) {
		super(activity, apps, settings);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final TextView row = getOrCreateTextView(convertView);
		bindView(applist.get(position), row);
		return row;
	}

}
