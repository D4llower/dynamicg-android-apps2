package com.dynamicg.homebuttonlauncher;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;

import com.dynamicg.common.MarketLinkHelper;
import com.dynamicg.homebuttonlauncher.dialog.PreferencesDialog;
import com.dynamicg.homebuttonlauncher.preferences.PreferencesManager;
import com.dynamicg.homebuttonlauncher.tools.AppHelper;
import com.dynamicg.homebuttonlauncher.tools.DialogHelper;
import com.dynamicg.homebuttonlauncher.tools.PopupMenuWrapper;
import com.dynamicg.homebuttonlauncher.tools.PopupMenuWrapper.PopupMenuItemListener;

public class AppListContextMenu {

	private final MainActivityHome activity;
	private final Context context;
	private final PreferencesManager preferences;
	private final boolean fromMainScreen;

	public AppListContextMenu(MainActivityHome activity, boolean fromMainScreen) {
		this.activity = activity;
		this.context = activity;
		this.preferences = activity.getPreferences();
		this.fromMainScreen = fromMainScreen;
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
					case HBLConstants.MENU_PREFERENCES: new PreferencesDialog(activity, preferences).show(); break;
					}
				}
				catch (Throwable t) {
					DialogHelper.showCrashReport(context, t);
				}
			}
		};

		PopupMenuWrapper menuWrapper = new PopupMenuWrapper(context, anchor, listener);
		if (!appEntry.isShortcut()) {
			menuWrapper.addItem(HBLConstants.MENU_SHOW_APP_DETAILS, R.string.openAppDetails);
			menuWrapper.addItem(HBLConstants.SHOW_PLAY_STORE, R.string.openPlayStore);
		}
		if (fromMainScreen) {
			menuWrapper.addItem(HBLConstants.MENU_APPS_REMOVE, R.string.menuRemove);
		}
		if (fromMainScreen && preferences.prefSettings.isNoHeader()) {
			menuWrapper.addItem(HBLConstants.MENU_PREFERENCES, R.string.preferences);
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
				preferences.prefShortlist.remove(appEntry);
				activity.refreshList();
			}
		};
		DialogHelper.confirm(context, R.string.menuRemove, okListener);
	}

}
