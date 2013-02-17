package com.dynamicg.homebuttonlauncher;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.dynamicg.common.Logger;

public class AppListAdapterSort extends AppListAdapter {

	private static final Logger log = new Logger(AppListAdapterSort.class);

	private final boolean[] sortChanged;
	private final View.OnClickListener clickListener;
	private final View.OnLongClickListener longClickListener;

	public AppListAdapterSort(Activity activity, AppListContainer apps, boolean[] sortChanged) {
		super(activity, apps, R.layout.app_entry_sort);
		this.sortChanged = sortChanged;

		clickListener = new View.OnClickListener() {
			@Override
			public synchronized void onClick(View v) {
				int oldPosition = (Integer)v.getTag();
				int newPosition = v.getId()==R.id.sortDown ? oldPosition+1 : oldPosition-1;
				applyMove(oldPosition, newPosition);
			}
		};

		longClickListener = new View.OnLongClickListener() {
			@Override
			public synchronized boolean onLongClick(View v) {
				int oldPosition = (Integer)v.getTag();
				int newPosition = v.getId()==R.id.sortDown ? applist.size()-1 : 0;
				applyMove(oldPosition, newPosition);
				return true;
			}
		};
	}

	private void applyMove(int oldPosition, int newPosition) {
		sortChanged[0] = true;
		AppEntry entry = applist.get(oldPosition);
		applist.moveTo(entry, newPosition);
		log.debug("applyMove done", entry, oldPosition, newPosition);
		notifyDataSetChanged();
	}

	private void prepareButton(LinearLayout row, int buttonId, int position) {
		Button button = (Button)row.findViewById(buttonId);
		button.setTag(position);

		button.setOnClickListener(clickListener);
		button.setOnLongClickListener(longClickListener);
		button.setLongClickable(true);

		boolean active = (buttonId==R.id.sortUp && position>0)
				|| (buttonId==R.id.sortDown && position<applist.size()-1);
		if (active) {
			button.setText(buttonId==R.id.sortUp ? "\u2191" : "\u2193");
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

		prepareButton(layout, R.id.sortUp, position);
		prepareButton(layout, R.id.sortDown, position);

		ImageView icon = (ImageView)layout.findViewById(R.id.sortIcon);
		icon.setImageDrawable(appEntry.getIcon(iconSizePx, null));

		return layout;
	}



}
