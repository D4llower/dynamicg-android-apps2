package com.dynamicg.homebuttonlauncher.dialog;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;

import com.dynamicg.homebuttonlauncher.AppEntry;
import com.dynamicg.homebuttonlauncher.AppListAdapter;
import com.dynamicg.homebuttonlauncher.AppListAdapterSort;
import com.dynamicg.homebuttonlauncher.AppListContextMenu;
import com.dynamicg.homebuttonlauncher.MainActivityHome;
import com.dynamicg.homebuttonlauncher.MenuGlobals;
import com.dynamicg.homebuttonlauncher.OnClickListenerWrapper;
import com.dynamicg.homebuttonlauncher.R;
import com.dynamicg.homebuttonlauncher.preferences.HomeLauncherBackupAgent;
import com.dynamicg.homebuttonlauncher.preferences.PrefShortlist;
import com.dynamicg.homebuttonlauncher.preferences.PreferencesManager;
import com.dynamicg.homebuttonlauncher.tools.AppHelper;
import com.dynamicg.homebuttonlauncher.tools.DialogHelper;
import com.dynamicg.homebuttonlauncher.tools.PopupMenuWrapper;
import com.dynamicg.homebuttonlauncher.tools.PopupMenuWrapper.PopupMenuItemListener;

public class AppConfigDialog extends Dialog {

	private final MainActivityHome activity;
	private final Context context;
	private final PrefShortlist prefShortlist;
	private final List<AppEntry> appList;
	private final boolean actionAdd;
	private final boolean actionRemove;
	private final boolean actionSort;

	private static final int MENU_RESET = 1;

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

		if (actionSort) {
			requestWindowFeature(Window.FEATURE_NO_TITLE);
		}
	}

	private void afterSave() {
		activity.refreshList();
		HomeLauncherBackupAgent.requestBackup(context);
		dismiss();
	}

	private final void onButtonOk() {
		if (actionRemove) {
			prefShortlist.remove(getSelectedComponents());
		}
		else if (actionSort) {
			prefShortlist.saveSortedList(appList);
		}
		else {
			prefShortlist.add(getSelectedComponents());
		}
		afterSave();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		int titleResId = actionRemove ? R.string.menuRemoveApps : actionSort ? R.string.menuSort : R.string.menuAddApps;
		setTitle(titleResId);

		setContentView(R.layout.configure_apps);

		if (actionSort) {
			attachHeaderForSort();
		}
		else {
			// hide custom header
			findViewById(R.id.headerContainer).setVisibility(View.GONE);
		}

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

		final AppListAdapter adapter;
		if (actionSort) {
			adapter = new AppListAdapterSort(activity, appList);
		}
		else {
			adapter = new AppListAdapter(activity, appList, R.layout.app_entry_default);
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

	private void attachHeaderForSort() {
		final View anchor = DialogHelper.prepareCustomHeader(this, R.string.menuSort);
		final PopupMenuItemListener listener = new PopupMenuItemListener() {
			@Override
			public void popupMenuItemSelected(int id) {
				if (id==MENU_RESET) {
					confirmSortReset();
				}
			}
		};
		final PopupMenuWrapper menuWrapper = new PopupMenuWrapper(context, anchor, listener);
		menuWrapper.attachToAnchorClick();
		menuWrapper.addItem(MENU_RESET, R.string.menuReset);
	}

	private void confirmSortReset() {
		AlertDialog.Builder b = new AlertDialog.Builder(context);
		String label = context.getString(R.string.menuReset)+"?";
		b.setTitle(label);
		b.setPositiveButton(R.string.buttonOk, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				prefShortlist.resetSortList();
				afterSave();
			}
		} );
		b.setNegativeButton(R.string.buttonCancel, null);
		b.show();
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
