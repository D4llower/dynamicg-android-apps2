package com.dynamicg.homebuttonlauncher.dialog;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;

import com.dynamicg.common.Logger;
import com.dynamicg.homebuttonlauncher.AppEntry;
import com.dynamicg.homebuttonlauncher.AppListAdapter;
import com.dynamicg.homebuttonlauncher.AppListAdapterSort;
import com.dynamicg.homebuttonlauncher.AppListContainer;
import com.dynamicg.homebuttonlauncher.AppListContextMenu;
import com.dynamicg.homebuttonlauncher.MainActivityHome;
import com.dynamicg.homebuttonlauncher.MenuGlobals;
import com.dynamicg.homebuttonlauncher.OnClickListenerWrapper;
import com.dynamicg.homebuttonlauncher.R;
import com.dynamicg.homebuttonlauncher.preferences.HomeLauncherBackupAgent;
import com.dynamicg.homebuttonlauncher.preferences.PrefShortlist;
import com.dynamicg.homebuttonlauncher.preferences.PreferencesManager;
import com.dynamicg.homebuttonlauncher.tools.AppHelper;

public class AppConfigDialog extends Dialog {

	private static final Logger log = new Logger(AppConfigDialog.class);

	private final MainActivityHome activity;
	private final Context context;
	private final PrefShortlist prefShortlist;
	protected final AppListContainer appList;
	private final boolean[] sortChanged = new boolean[]{false};

	private final boolean actionAdd;
	private final boolean actionRemove;
	private final boolean actionSort;

	protected AppListAdapter adapter;

	public AppConfigDialog(MainActivityHome activity, PreferencesManager preferences, int action) {
		super(activity);
		this.activity = activity;
		this.context = activity;
		this.prefShortlist = preferences.prefShortlist;
		this.actionAdd = action==MenuGlobals.APPS_ADD;
		this.actionSort = action==MenuGlobals.APPS_SORT;
		this.actionRemove = action==MenuGlobals.APPS_REMOVE;

		if (actionAdd) {
			this.appList = AppHelper.getAllAppsList(activity, prefShortlist);
		}
		else {
			this.appList = AppHelper.getSelectedAppsList(activity, prefShortlist);
		}

		requestWindowFeature(Window.FEATURE_NO_TITLE);
	}

	private void afterSave() {
		activity.refreshList();
		HomeLauncherBackupAgent.requestBackup(context);
		dismiss();
	}

	private final void onButtonOk() {
		if (actionAdd) {
			log.debug("actionAdd");
			prefShortlist.add(getSelectedComponents());
		}
		else if (actionRemove) {
			log.debug("actionRemove");
			prefShortlist.remove(getSelectedComponents());
		}
		else if (actionSort && sortChanged[0]) {
			// only store the sorted list if we actually had changes
			log.debug("actionSort");
			prefShortlist.saveSortedList(appList.getApps());
		}
		afterSave();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.configure_apps);

		ConfigHeaderAbstract header = actionSort ? new ConfigHeaderSortReset(this) : new ConfigHeaderSearch(this);
		header.attach();
		final int titleResId = actionRemove ? R.string.menuRemoveApps : actionSort ? R.string.menuSort : R.string.menuAddApps;
		header.setTitleAndWidth(titleResId);

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

		if (actionSort) {
			this.adapter = new AppListAdapterSort(activity, appList, sortChanged);
		}
		else {
			this.adapter = new AppListAdapter(activity, appList, R.layout.app_entry_default);
		}

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

		if (actionAdd) {
			listview.setFastScrollEnabled(true);
		}

		new AppListContextMenu(context).attach(listview, appList);
	}

	private List<String> getSelectedComponents() {
		List<String> list = new ArrayList<String>();
		for (AppEntry entry:appList.getApps()) {
			if (entry.isChecked()) {
				list.add(entry.getComponent());
			}
		}
		return list;
	}

	public void doSortReset() {
		prefShortlist.resetSortList();
		afterSave();
	}

	// when "search" is applied
	public void updateAppList(List<AppEntry> newList) {
		appList.updateList(newList);
		adapter.notifyDataSetChanged();
	}

}
