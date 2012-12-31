package com.dynamicg.homebuttonlauncher;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.dynamicg.common.DialogWithExitPoints;

public class AppConfigDialog extends DialogWithExitPoints {

	private final MainActivityHome activity;
	private final Settings settings;
	private final List<AppEntry> appList;
	private final int action;

	public AppConfigDialog(MainActivityHome activity, Settings settings, int action) {
		super(activity);
		this.activity = activity;
		this.settings = settings;
		this.action = action;

		if (isRemove()) {
			this.appList = AppHelper.getSelectedAppsList(activity, settings);
		}
		else {
			this.appList = AppHelper.getAllAppsList(activity, settings);
		}
	}

	private boolean isRemove() {
		return action==MenuGlobals.APPS_REMOVE;
	}

	private final void onButtonOk() {
		if (isRemove()) {
			settings.remove(getSelectedComponents());
		}
		else {
			settings.add(getSelectedComponents());
		}
		activity.refreshAppList();
		dismiss();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setTitle(isRemove()?R.string.menuRemoveApps:R.string.menuAddApps);
		setContentView(R.layout.config_add_remove);

		findViewById(R.id.buttonOk).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onButtonOk();
			}
		});

		findViewById(R.id.buttonCancel).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});

		final AppListAdapter adapter = new AppListAdapter(appList, getLayoutInflater(), true);
		final ListView listview = (ListView)findViewById(R.id.applist);
		listview.setAdapter(adapter);

		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				AppEntry entry = (AppEntry)appList.get(position);
				entry.flipCheckedState();
				entry.decorateSelection(view);
			}
		});

		new AppListContextMenu(getContext()).attach(listview, appList);

	}

	private List<String> getSelectedComponents() {
		List<String> list = new ArrayList<String>();
		for (AppEntry entry:appList) {
			if (entry.isChecked()) {
				list.add(entry.getComponent());
			}
		}
		return list;
	}

}
