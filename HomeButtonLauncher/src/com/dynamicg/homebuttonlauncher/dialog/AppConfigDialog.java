package com.dynamicg.homebuttonlauncher.dialog;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.dynamicg.common.DialogWithExitPoints;
import com.dynamicg.homebuttonlauncher.AppEntry;
import com.dynamicg.homebuttonlauncher.AppListAdapter;
import com.dynamicg.homebuttonlauncher.AppListContextMenu;
import com.dynamicg.homebuttonlauncher.MainActivityHome;
import com.dynamicg.homebuttonlauncher.MenuGlobals;
import com.dynamicg.homebuttonlauncher.OnClickListenerWrapper;
import com.dynamicg.homebuttonlauncher.R;
import com.dynamicg.homebuttonlauncher.preferences.HomeLauncherBackupAgent;
import com.dynamicg.homebuttonlauncher.preferences.PrefShortlist;
import com.dynamicg.homebuttonlauncher.preferences.PreferencesManager;
import com.dynamicg.homebuttonlauncher.tools.AppHelper;

public class AppConfigDialog extends DialogWithExitPoints {

	private final MainActivityHome activity;
	private final PrefShortlist prefShortlist;
	private final boolean isActionRemove;
	private final List<AppEntry> appList;

	public AppConfigDialog(MainActivityHome activity, PreferencesManager preferences, int action) {
		super(activity);
		this.activity = activity;
		this.prefShortlist = preferences.prefShortlist;
		this.isActionRemove = action==MenuGlobals.APPS_REMOVE;

		if (isActionRemove) {
			this.appList = AppHelper.getSelectedAppsList(activity, prefShortlist);
		}
		else {
			this.appList = AppHelper.getAllAppsList(activity, prefShortlist);
		}
	}

	private final void onButtonOk() {
		if (isActionRemove) {
			prefShortlist.remove(getSelectedComponents());
		}
		else {
			prefShortlist.add(getSelectedComponents());
		}
		activity.refreshList();
		HomeLauncherBackupAgent.requestBackup(getContext());
		dismiss();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setTitle(isActionRemove?R.string.menuRemoveApps:R.string.menuAddApps);
		setContentView(R.layout.config_add_remove);

		findViewById(R.id.buttonOk).setOnClickListener(new OnClickListenerWrapper() {
			@Override
			public void onClickImpl(View v) {
				onButtonOk();
			}
		});

		findViewById(R.id.buttonCancel).setOnClickListener(new OnClickListenerWrapper() {
			@Override
			public void onClickImpl(View v) {
				dismiss();
			}
		});

		final AppListAdapter adapter = new AppListAdapter(activity, appList);
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

		if (!isActionRemove) {
			listview.setFastScrollEnabled(true);
		}

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
