package com.dynamicg.bookmarkTree;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.dynamicg.bookmarkTree.ui.DisclaimerPopup;
import com.dynamicg.bookmarkTree.ui.EditBookmarkDialog;
import com.dynamicg.bookmarkTree.ui.PreferencesDialog;
import com.dynamicg.common.main.SystemUtil;

public class Main extends Activity {

	/*
	 * icons sources:
	 * http://www.veryicon.com/search/bookmark/ 
	 * http://www.iconeasy.com/
	 */
	
	private static int actionCounter=-1;
    public static final int ACTION_COLLAPSE_ALL = ++actionCounter;
    public static final int ACTION_EXPAND_ALL = ++actionCounter;
    public static final int ACTION_RELOAD = ++actionCounter;
    public static final int ACTION_SETTINGS = ++actionCounter;
    public static final int ACTION_DELETE_BOOKMARK = ++actionCounter;
    public static final int ACTION_NEW_BM = ++actionCounter;

    private static boolean initialised = false;
    
    private BookmarkTreeContext ctx;
    
    public void onCreate(Bundle savedInstanceState) {
        
    	super.onCreate(savedInstanceState);
    	if (!initialised) {
    		SystemUtil.init(this);
    		initialised = true;
    	}
        setContentView(R.layout.main);
    	this.ctx = new BookmarkTreeContext(this);
    	
    	DisclaimerPopup.showOnce(ctx);
    	
    }
    
    
    private void createMenu(Menu menu, int id, int title, int icon) {
    	menu.add(0, id, 0, title).setIcon(icon);
    }
    
	public boolean onCreateOptionsMenu(Menu menu) {
		createMenu(menu, ACTION_EXPAND_ALL, R.string.menuExpandAll, R.drawable.menu_expand);
		createMenu(menu, ACTION_COLLAPSE_ALL, R.string.menuCollapseAll, R.drawable.menu_collapse);
		createMenu(menu, ACTION_RELOAD, R.string.menuReload, R.drawable.menu_reload);
		createMenu(menu, ACTION_NEW_BM, R.string.menuCreate, R.drawable.menu_create);
		createMenu(menu, ACTION_SETTINGS, R.string.menuPrefs, R.drawable.menu_prefs);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if ( id==ACTION_COLLAPSE_ALL || id==ACTION_EXPAND_ALL ) {
			ctx.bookmarkManager.toggleFolders(id);
			ctx.bookmarkListAdapter.redraw();
		}
		else if ( id==ACTION_RELOAD ) {
			ctx.reloadAndRefresh();
		}
		else if ( id==ACTION_SETTINGS ) {
			new PreferencesDialog(ctx);
		}
		else if ( id==ACTION_NEW_BM ) {
			new EditBookmarkDialog(ctx);
		}
		return true;
	}
	
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
	
}