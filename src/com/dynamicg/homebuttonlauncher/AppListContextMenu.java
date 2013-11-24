package com.dynamicg.homebuttonlauncher;

import android.content.DialogInterface;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;

import com.dynamicg.common.MarketLinkHelper;
import com.dynamicg.homebuttonlauncher.preferences.HomeLauncherBackupAgent;
import com.dynamicg.homebuttonlauncher.preferences.PrefShortlist;
import com.dynamicg.homebuttonlauncher.tools.AppHelper;
import com.dynamicg.homebuttonlauncher.tools.DialogHelper;
import com.dynamicg.homebuttonlauncher.tools.PopupMenuWrapper;
import com.dynamicg.homebuttonlauncher.tools.PopupMenuWrapper.PopupMenuItemListener;

public class AppListContextMenu {

	private final MainActivityHome context;
	private final PrefShortlist prefShortlist;

	public AppListContextMenu(MainActivityHome activity, PrefShortlist prefShortlist) {
		this.context = activity;
		this.prefShortlist = prefShortlist;
	}

	public AppListContextMenu(MainActivityHome activity) {
		this.context = activity;
		this.prefShortlist = null;
	}

	public void attach(final AbsListView listview, final AppListContainer appList) {
		listview.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				return openContextMenu(view, appList.get(position));
			}
		});
	}

	private boolean openContextMenu(final View anchor, final AppEntry appEntry) {
		final PopupMenuItemListener listener = new PopupMenuItemListener() {
			@Override
			public void popupMenuItemSelected(int id) {
				try {
					switch (id) {
					case HBLConstants.MENU_SHOW_APP_DETAILS: showAppDetails(appEntry); break;
					case HBLConstants.SHOW_PLAY_STORE: showInPlayStore(appEntry); break;
					case HBLConstants.MENU_APPS_REMOVE: remove(appEntry); break;
					}
				}
				catch (Throwable t) {
					DialogHelper.showCrashReport(context, t);
				}
			}
		};

		PopupMenuWrapper menuWrapper = new PopupMenuWrapper(context, anchor, listener);
		if (!appEntry.shortcut) {
			menuWrapper.addItem(HBLConstants.MENU_SHOW_APP_DETAILS, R.string.openAppDetails);
			menuWrapper.addItem(HBLConstants.SHOW_PLAY_STORE, R.string.openPlayStore);
		}
		if (prefShortlist!=null) {
			menuWrapper.addItem(HBLConstants.MENU_APPS_REMOVE, R.string.menuRemove);
		}
		if (menuWrapper.size()>0) {
			menuWrapper.showMenu();
			return true;
		}
		else {
			return false;
		}
	}

	private void showAppDetails(AppEntry appEntry) {
		AppHelper.openAppDetails(context, appEntry.getPackage());
	}

	private void showInPlayStore(AppEntry appEntry) {
		MarketLinkHelper.openMarketIntent(context, appEntry.getPackage());
	}

	private void remove(final AppEntry appEntry) {
		OnClickListenerDialogWrapper okListener = new OnClickListenerDialogWrapper(context) {
			@Override
			public void onClickImpl(DialogInterface d, int which) {
				GlobalContext.resetCache();
				prefShortlist.remove(appEntry);
				context.refreshList();
				HomeLauncherBackupAgent.requestBackup(context);
			}
		};
		DialogHelper.confirm(context, R.string.menuRemove, okListener);
	}

}
