package com.dynamicg.bookmarkTree;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.dynamicg.bookmarkTree.ui.DisclaimerPopup;
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
    
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, ACTION_EXPAND_ALL, 0, "Expand All").setIcon(R.drawable.menu_expand);
		menu.add(0, ACTION_COLLAPSE_ALL, 0, "Collapse All").setIcon(R.drawable.menu_collapse);
		menu.add(0, ACTION_RELOAD, 0, "Reload").setIcon(R.drawable.menu_reload);
		menu.add(0, ACTION_SETTINGS, 0, "Preferences").setIcon(R.drawable.menu_prefs);
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
		return true;
	}
	
}