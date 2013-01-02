package com.dynamicg.homebuttonlauncher;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;

import com.dynamicg.common.MarketLinkHelper;
import com.dynamicg.homebuttonlauncher.tools.AppHelper;
import com.dynamicg.homebuttonlauncher.tools.ErrorHandler;
import com.dynamicg.homebuttonlauncher.tools.PopupMenuWrapper;
import com.dynamicg.homebuttonlauncher.tools.PopupMenuWrapper.PopupMenuItemListener;

public class AppListContextMenu {

	private final Context context;

	public AppListContextMenu(final Context context) {
		this.context = context;
	}

	public void attach(final AbsListView listview, final List<AppEntry> appList) {
		listview.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				openContextMenu(view, appList.get(position));
				return true;
			}
		});
	}

	private void openContextMenu(final View anchor, final AppEntry appEntry) {
		final boolean installedFromPlayStore = AppHelper.showPlayStoreLink(context, appEntry);
		final PopupMenuItemListener listener = new PopupMenuItemListener() {
			@Override
			public void popupMenuItemSelected(int id) {
				try {
					switch (id) {
					case MenuGlobals.SHOW_APP_DETAILS: showAppDetails(appEntry); break;
					case MenuGlobals.SHOW_PLAY_STORE: showInPlayStore(appEntry); break;
					}
				}
				catch (Throwable t) {
					ErrorHandler.showCrashReport(context, t);
				}
			}
		};

		PopupMenuWrapper menuWrapper = new PopupMenuWrapper(context, anchor, listener);
		menuWrapper.addItem(MenuGlobals.SHOW_APP_DETAILS, R.string.openAppDetails);
		menuWrapper.addItem(MenuGlobals.SHOW_PLAY_STORE, R.string.openPlayStore, installedFromPlayStore);
		menuWrapper.showMenu();
	}

	private void showAppDetails(AppEntry appEntry) {
		Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
		i.addCategory(Intent.CATEGORY_DEFAULT);
		Uri data = Uri.parse("package:"+appEntry.getPackage());
		i.setData(data);
		context.startActivity(i);
	}

	private void showInPlayStore(AppEntry appEntry) {
		MarketLinkHelper.openMarketIntent(context, appEntry.getPackage());
	}

}
