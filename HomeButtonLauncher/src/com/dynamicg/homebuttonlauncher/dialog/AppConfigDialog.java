package com.dynamicg.homebuttonlauncher.dialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;

import com.dynamicg.common.Logger;
import com.dynamicg.homebuttonlauncher.AppEntry;
import com.dynamicg.homebuttonlauncher.AppListContainer;
import com.dynamicg.homebuttonlauncher.AppListContextMenu;
import com.dynamicg.homebuttonlauncher.GlobalContext;
import com.dynamicg.homebuttonlauncher.MainActivityHome;
import com.dynamicg.homebuttonlauncher.MenuGlobals;
import com.dynamicg.homebuttonlauncher.OnClickListenerWrapper;
import com.dynamicg.homebuttonlauncher.R;
import com.dynamicg.homebuttonlauncher.adapter.AppListAdapter;
import com.dynamicg.homebuttonlauncher.adapter.AppListAdapterAddRemove;
import com.dynamicg.homebuttonlauncher.adapter.AppListAdapterShortcuts;
import com.dynamicg.homebuttonlauncher.adapter.AppListAdapterSort;
import com.dynamicg.homebuttonlauncher.dialog.header.HeaderAbstract;
import com.dynamicg.homebuttonlauncher.dialog.header.HeaderAppSearch;
import com.dynamicg.homebuttonlauncher.dialog.header.HeaderAppSortReset;
import com.dynamicg.homebuttonlauncher.preferences.HomeLauncherBackupAgent;
import com.dynamicg.homebuttonlauncher.preferences.PrefShortlist;
import com.dynamicg.homebuttonlauncher.preferences.PreferencesManager;
import com.dynamicg.homebuttonlauncher.tab.TabHelperAppAdd;
import com.dynamicg.homebuttonlauncher.tools.AppHelper;

@SuppressLint("UseSparseArrays")
public class AppConfigDialog extends Dialog {

	private static final Logger log = new Logger(AppConfigDialog.class);

	private static final HashMap<Integer, Integer> recentTab = new HashMap<Integer, Integer>();

	private final MainActivityHome activity;
	private final Context context;
	private final PrefShortlist prefShortlist;
	private final boolean[] sortChanged = new boolean[]{false};

	private final int action;
	private final boolean actionAdd;
	private final boolean actionRemove;
	private final boolean actionSort;

	private AppListContainer appList;
	private HeaderAbstract header;
	
	protected AppListAdapter adapter;

	public AppConfigDialog(MainActivityHome activity, PreferencesManager preferences, int action) {
		super(activity);
		this.activity = activity;
		this.context = activity;
		this.prefShortlist = preferences.prefShortlist;
		this.action = action;
		this.actionAdd = action==MenuGlobals.APPS_ADD;
		this.actionSort = action==MenuGlobals.APPS_SORT;
		this.actionRemove = action==MenuGlobals.APPS_REMOVE;

		requestWindowFeature(Window.FEATURE_NO_TITLE);
	}

	private void afterSave() {
		GlobalContext.resetCache();
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

		this.header = actionSort ? new HeaderAppSortReset(this) : new HeaderAppSearch(this);
		final int titleResId = actionRemove ? R.string.menuRemove : actionSort ? R.string.menuSort : R.string.menuAdd;
		header.attach(titleResId);

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

		if (actionAdd && log.isDebugEnabled) {
			new TabHelperAppAdd(activity, this, getSelectedTab()).bindTabs();
		}

		putBody();
	}

	private int getSelectedTab() {
		return recentTab.containsKey(action)?recentTab.get(action):0;
	}

	private void putBody() {
		if (actionAdd && getSelectedTab()>0) {
			this.appList = AppHelper.getShortcutApps();
			putBodyShortcutTab();
		}
		else {
			if (actionAdd) {
				this.appList = AppHelper.getAllAppsList(prefShortlist);
			}
			else {
				this.appList = AppHelper.getSelectedAppsList(prefShortlist, false);
			}
			putBodyMainTab();
		}
		header.setBaseAppList(appList);
	}

	private void putBodyMainTab() {
		if (actionSort) {
			this.adapter = new AppListAdapterSort(activity, appList, sortChanged);
		}
		else {
			this.adapter = new AppListAdapterAddRemove(activity, appList);
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

		new AppListContextMenu(activity).attach(listview, appList);
	}

	private void putBodyShortcutTab() {
		this.adapter = new AppListAdapterShortcuts(activity, appList);
		final ListView listview = (ListView)findViewById(R.id.applist);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				startShortcutApp((AppEntry)appList.get(position));
			}
		});
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

	public void tabChanged(int target) {
		log.debug("tabChanged", target);
		recentTab.put(action, target);
		putBody();
	}

	private void startShortcutApp(AppEntry appEntry) {
		// TODO ## (1) implement
		// TODO ## (2) search does not work
	}

}
