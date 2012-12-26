package com.dynamicg.homebuttonlauncher;

import java.util.List;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AppListAdapter extends BaseAdapter {

	private final List<AppEntry> applist;
	private final LayoutInflater inflater;
	private boolean forEditor;

	public AppListAdapter(List<AppEntry> apps, LayoutInflater inflater, boolean forEditor) {
		this.applist = apps;
		this.inflater = inflater;
		this.forEditor = forEditor;
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
		TextView row;
		if (convertView==null) {
			row = (TextView)inflater.inflate(R.layout.app_entry, null);
		}
		else {
			row = (TextView)convertView;
		}

		AppEntry appEntry = applist.get(position);
		row.setCompoundDrawablesWithIntrinsicBounds(appEntry.getIcon(), null, null, null);
		row.setText(appEntry.getLabel());
		if (forEditor) {
			appEntry.decorateSelection(row);
			row.setTypeface(Typeface.DEFAULT, Typeface.ITALIC);
		}
		return row;
	}

}
