package com.dynamicg.homebuttonlauncher;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import com.dynamicg.common.Logger;
import com.dynamicg.homebuttonlauncher.dialog.AboutDialog;
import com.dynamicg.homebuttonlauncher.dialog.AppConfigDialog;
import com.dynamicg.homebuttonlauncher.dialog.PreferencesDialog;
import com.dynamicg.homebuttonlauncher.preferences.PreferencesManager;
import com.dynamicg.homebuttonlauncher.tools.AppHelper;
import com.dynamicg.homebuttonlauncher.tools.DialogHelper;
import com.dynamicg.homebuttonlauncher.tools.IconProvider;
import com.dynamicg.homebuttonlauncher.tools.PopupMenuWrapper;
import com.dynamicg.homebuttonlauncher.tools.PopupMenuWrapper.PopupMenuItemListener;

// see https://plus.google.com/104570711580136846518/posts/QpqfXXigAWW

//note we cannot filter the app on "needs soft home button" (i.e. a non-physical home button like galaxy nexus)
//http://developer.android.com/guide/topics/manifest/uses-feature-element.html
public class MainActivityHome extends Activity {

	private static final Logger log = new Logger(MainActivityHome.class);

	private Context context;
	private PreferencesManager preferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		context = this;

		try {
			main();
		}
		catch (Throwable t) {
			DialogHelper.showCrashReport(context, t);
		}
	}

	private void main() {
		setContentView(R.layout.activity_main);
		preferences = new PreferencesManager(context);
		if (isAutoStartSingleSuccessful()) {
			return;
		}
		IconProvider.init(context);
		attachContextMenu();
		setListAdapter();
		setMinWidth();

		if (preferences.prefSettings.getNumTabs()>0) {
			new MainTabHelper(this, preferences).bindTabs();
		}
	}

	private boolean isAutoStartSingleSuccessful() {
		if (!preferences.prefSettings.isAutoStartSingle()) {
			log.debug("autoStart", "disabled");
			return false;
		}

		if (getIntent().getBooleanExtra(MainActivityOpen.KEY, false) == true) {
			// called through "OpenActivity" (i.e. app drawer or homescreen icon), not through swipe
			// -> skip, otherwise we will lock ourselves out
			log.debug("autoStart", "from OpenActivity");
			return false;
		}

		if (preferences.prefShortlist.getComponentsMap().size()!=1) {
			// shortcut - this is faster than "getSelectedAppsList" just below
			log.debug("autoStart", "getComponentsMap", "size!=1");
			return false;
		}

		final AppListContainer appList = AppHelper.getSelectedAppsList(context, preferences.prefShortlist);
		if (appList.size()!=1) {
			log.debug("autoStart", "getSelectedAppsList", "size!=1");
			return false;
		}

		AppEntry entry = appList.get(0);
		log.debug("autoStart", entry.getComponent());
		boolean started = startAppAndClose(entry);
		return started;
	}

	public void refreshList() {
		try {
			setListAdapter();
			setMinWidth();
		}
		catch (Throwable t) {
			DialogHelper.showCrashReport(context, t);
		}
	}

	private void setMinWidth() {
		int minWidth = DialogHelper.getDimension(context, preferences.prefSettings.getMinWidthDimension());
		findViewById(R.id.headerContainer).setMinimumWidth(minWidth);
	}

	private AbsListView getListView() {
		final int layoutResId = preferences.prefSettings.getListLayoutId();
		final View listview = findViewById(R.id.mainListView);

		if (listview instanceof ViewStub) {
			// first call
			ViewStub stub = (ViewStub)listview;
			stub.setLayoutResource(layoutResId);
			return (AbsListView)stub.inflate();
		}

		// replace existing list on refresh
		ViewGroup parent = (ViewGroup)listview.getParent();
		AbsListView replacementListView = (AbsListView)getLayoutInflater().inflate(layoutResId, null);
		parent.addView(replacementListView, parent.indexOfChild(listview));
		parent.removeView(listview);
		return replacementListView;
	}

	private void setListAdapter() {
		final AbsListView listview = getListView();
		listview.setId(R.id.mainListView);

		final AppListContainer appList = AppHelper.getSelectedAppsList(context, preferences.prefShortlist);
		final BaseAdapter adapter = new AppListAdapter(this, appList, preferences.prefSettings);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				startAppAndClose(appList.get(position));
			}
		});
		new AppListContextMenu(this, preferences.prefShortlist).attach(listview, appList);
	}

	private boolean startAppAndClose(AppEntry entry) {
		String component=null;
		try {
			component = entry.getComponent();
			Intent intent = AppHelper.getStartIntent(component);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(intent);
			finish();
			return true;
		}
		catch (Throwable t) {
			String title = "ERROR - cannot open";
			String details = "Component: "+component+"\nException: "+t.getClass().getSimpleName();
			DialogHelper.showError(context, title, details);
			return false;
		}
	}

	private void attachContextMenu() {
		final View anchor = findViewById(R.id.headerIcon);
		anchor.setOnClickListener(new OnClickListenerWrapper() {
			private PopupMenuWrapper wrapper;
			@Override
			public void onClickImpl(View v) {
				// create the popup only if actually required
				if (wrapper==null) {
					wrapper = bindMenu(anchor);
				}
				wrapper.showMenu();
			}
		});
	}

	private PopupMenuWrapper bindMenu(final View anchor) {
		final PopupMenuItemListener listener = new PopupMenuItemListener() {
			@Override
			public void popupMenuItemSelected(int id) {
				final MainActivityHome activity = MainActivityHome.this;
				switch (id) {
				case MenuGlobals.APPS_ADD:
					new AppConfigDialog(activity, preferences, MenuGlobals.APPS_ADD).show();
					break;
				case MenuGlobals.APPS_REMOVE:
					new AppConfigDialog(activity, preferences, MenuGlobals.APPS_REMOVE).show();
					break;
				case MenuGlobals.APPS_SORT:
					new AppConfigDialog(activity, preferences, MenuGlobals.APPS_SORT).show();
					break;
				case MenuGlobals.ABOUT:
					new AboutDialog(activity).show();
					break;
				case MenuGlobals.PREFERENCES:
					new PreferencesDialog(activity, preferences).show();
					break;
				}
			}
		};

		final PopupMenuWrapper menuWrapper = new PopupMenuWrapper(context, anchor, listener);
		menuWrapper.addItem(MenuGlobals.APPS_ADD, R.string.menuAdd, android.R.drawable.ic_menu_add);
		menuWrapper.addItem(MenuGlobals.APPS_REMOVE, R.string.menuRemove, android.R.drawable.ic_menu_close_clear_cancel);
		menuWrapper.addItem(MenuGlobals.APPS_SORT, R.string.menuSort, android.R.drawable.ic_menu_sort_by_size);
		menuWrapper.addItem(MenuGlobals.ABOUT, R.string.menuAbout, android.R.drawable.ic_menu_info_details);
		menuWrapper.addItem(MenuGlobals.PREFERENCES, R.string.preferences, android.R.drawable.ic_menu_preferences);
		return menuWrapper;
	}

	public void updateOnTabSwitch(int tabindex) {
		if (preferences.getTabIndex()!=tabindex) {
			log.debug("updateOnTabSwitch", tabindex);
			preferences.updateCurrentTabIndex(tabindex);
			refreshList();
		}
	}

	public void redrawTabContainer() {
		new MainTabHelper(this, preferences).redraw();
	}

}
