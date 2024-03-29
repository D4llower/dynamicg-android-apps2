package com.dynamicg.bookmarkTree;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.dynamicg.bookmarkTree.backup.BackupPrefs;
import com.dynamicg.bookmarkTree.backup.BackupRestoreCloudHelper;
import com.dynamicg.bookmarkTree.backup.BackupRestoreDialog;
import com.dynamicg.bookmarkTree.backup.GoogleDriveGlobals;
import com.dynamicg.bookmarkTree.dialogs.AboutDialog;
import com.dynamicg.bookmarkTree.dialogs.EditBookmarkDialog;
import com.dynamicg.bookmarkTree.prefs.PreferencesDialog;
import com.dynamicg.common.ErrorNotification;
import com.dynamicg.common.StringUtil;
import com.dynamicg.common.SystemUtil;

public class Main extends Activity {

	/*
	 * icons sources:
	 * http://www.veryicon.com/search/bookmark/
	 * http://www.iconeasy.com/
	 * 
	 * http://www.softicons.com
	 * http://www.softicons.com/free-icons/folder-icons/latt-for-os-x-icons-by-rick-patrick
	 * 
	 * app icon ICS:
	 * http://www.iconarchive.com/show/touchdown-3D-icons-by-dario-arnaez/My-world-icon.html
	 */

	public static final int ACTION_COLLAPSE_ALL = 1;
	public static final int ACTION_EXPAND_ALL = 2;
	public static final int ACTION_RELOAD = 3;
	public static final int ACTION_SETTINGS = 4;
	public static final int ACTION_DELETE_BOOKMARK = 5;
	public static final int ACTION_NEW_BM = 6;
	public static final int ACTION_BACKUP_RESTORE = 7;

	private BookmarkTreeContext ctx;

	public Main() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			setContentView(R.layout.main);
			this.ctx = new BookmarkTreeContext(this);
			BackupPrefs.onStartup(ctx);
			AboutDialog.showOnce(ctx, false); // for debugging
		}
		catch (Throwable t) {
			ErrorNotification.notifyError(this, "App error", t);
		}
	}


	private void createMenu(Menu menu, int id, int title, int icon) {
		menu.add(0, id, 0, title).setIcon(icon);
	}

	private Menu menu;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.menu = menu;
		createMenu(menu, ACTION_EXPAND_ALL, R.string.menuExpandAll, R.drawable.menu_expand);
		createMenu(menu, ACTION_COLLAPSE_ALL, R.string.menuCollapseAll, R.drawable.menu_collapse);
		createMenu(menu, ACTION_RELOAD, R.string.menuReload, R.drawable.menu_reload);
		createMenu(menu, ACTION_NEW_BM, R.string.menuCreate, R.drawable.menu_create);
		createMenu(menu, ACTION_BACKUP_RESTORE, SystemUtil.isHoneycombOrNewer()?R.string.brDialogTitle:R.string.menuBackup, R.drawable.menu_save);
		createMenu(menu, ACTION_SETTINGS, R.string.menuPrefs, R.drawable.menu_prefs);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		menu.close();

		int id = item.getItemId();
		if ( id==ACTION_COLLAPSE_ALL || id==ACTION_EXPAND_ALL ) {
			ctx.bookmarkManager.toggleFolders(id);
			ctx.bookmarkListAdapter.redraw();
		}
		else if ( id==ACTION_RELOAD ) {
			ctx.reloadAndRefresh();
			int numberOfBookmarks = ctx.bookmarkManager.getNumberOfBookmarks();
			SystemUtil.toastShort(ctx.activity,
					StringUtil.textWithParam(this, R.string.hintReloaded, numberOfBookmarks));
		}
		else if ( id==ACTION_NEW_BM ) {
			new EditBookmarkDialog(ctx);
		}
		else if ( id==ACTION_SETTINGS ) {
			new PreferencesDialog(ctx);
		}
		else if ( id==ACTION_BACKUP_RESTORE ) {
			new BackupRestoreDialog(ctx);
		}
		return true;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode==GoogleDriveGlobals.ACTION_RESTORE) {
			BackupRestoreCloudHelper.confirmGoogleDriveRestore(data);
		}
	}

}