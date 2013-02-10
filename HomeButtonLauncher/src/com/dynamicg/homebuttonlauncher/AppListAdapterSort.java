package com.dynamicg.homebuttonlauncher;

import java.util.List;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dynamicg.common.Logger;

public class AppListAdapterSort extends AppListAdapter {

	private static final Logger log = new Logger(AppListAdapterSort.class);

	private final View.OnClickListener listener;

	public AppListAdapterSort(Activity activity, List<AppEntry> apps) {
		super(activity, apps, R.layout.app_entry_sort);
		listener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int id = v.getId();
				int position = (Integer)v.getTag();
				applyMove(id, position);
			}
		};
	}

	private synchronized void applyMove(int id, int position) {
		AppEntry entry = applist.get(position);
		applist.remove(entry);
		int newPosition = id==R.id.sortDown ? position+1 : position-1;
		applist.add(newPosition, entry);
		log.debug("applyMove done", entry, position, newPosition);
		notifyDataSetChanged();
	}

	private void prepareButton(View view, int position) {
		TextView button = (TextView)view;
		button.setTag(position);
		button.setOnClickListener(listener);
		boolean active = (view.getId()==R.id.sortUp && position>0)
				|| (view.getId()==R.id.sortDown && position<applist.size()-1);
		if (active) {
			button.setText(view.getId()==R.id.sortUp ? "\u2191" : "\u2193");
		}
		else {
			button.setText("");
		}
		button.setEnabled(active);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final AppEntry appEntry = applist.get(position);
		final LinearLayout layout;
		if (convertView==null) {
			layout = (LinearLayout)inflater.inflate(appEntryLayoutId, null);
		}
		else {
			layout = (LinearLayout)convertView;
		}

		prepareButton((TextView)layout.findViewById(R.id.sortUp), position);
		prepareButton((TextView)layout.findViewById(R.id.sortDown), position);

		ImageView icon = (ImageView)layout.findViewById(R.id.sortIcon);
		icon.setImageDrawable(appEntry.getIcon(iconSizePx));

		return layout;
	}



}
