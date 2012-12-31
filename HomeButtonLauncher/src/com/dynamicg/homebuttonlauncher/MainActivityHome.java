package com.dynamicg.homebuttonlauncher;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;

import com.dynamicg.common.DialogWithExitPoints;
import com.dynamicg.homebuttonlauncher.PopupMenuWrapper.PopupMenuItemListener;

// see https://plus.google.com/104570711580136846518/posts/QpqfXXigAWW

//note we cannot filter the app on "needs soft home button" (i.e. a non-physical home button like galaxy nexus)
//http://developer.android.com/guide/topics/manifest/uses-feature-element.html
public class MainActivityHome extends Activity {

	private Context context;
	private Settings settings;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		context = this;

		try {
			main();
		}
		catch (Throwable t) {
			ErrorHandler.showCrashReport(context, t);
		}
	}

	private void main() {
		setContentView(R.layout.activity_main);
		settings = new Settings(context);
		IconProvider.init(context);
		attachContextMenu();
		setListAdapter();
	}

	private void setListAdapter() {
		final List<AppEntry> appList = AppHelper.getSelectedAppsList(context, settings);
		AppListAdapter adapter = new AppListAdapter(appList, getLayoutInflater(), false);
		ListView listview = (ListView)findViewById(R.id.applist);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				appSelected(appList.get(position));
			}
		});

		new AppListContextMenu(context).attach(listview, appList);
	}

	private void appSelected(AppEntry entry) {
		String component=null;
		try {
			component = entry.getComponent();
			Intent intent = AppHelper.getStartIntent(component);
			startActivity(intent);
			finish();
		}
		catch (Throwable t) {
			String title = "ERROR - cannot open";
			String details = "Component: "+component+"\nException: "+t.getClass().getSimpleName();
			ErrorHandler.showError(context, title, details);
		}
	}

	private void attachContextMenu() {
		final View anchor = findViewById(R.id.headerIcon);
		final PopupMenuItemListener listener = new PopupMenuItemListener() {
			@Override
			public void popupMenuItemSelected(int id) {
				switch (id) {
				case MenuGlobals.APPS_ADD:
					new AppConfigDialog(MainActivityHome.this, settings, MenuGlobals.APPS_ADD).show();
					break;
				case MenuGlobals.APPS_REMOVE:
					new AppConfigDialog(MainActivityHome.this, settings, MenuGlobals.APPS_REMOVE).show();
					break;
				case MenuGlobals.ABOUT:
					new AboutDialog().show(context, getLayoutInflater());
					break;
				}
			}
		};

		final PopupMenuWrapper menuWrapper = new PopupMenuWrapper(context, anchor, listener);
		menuWrapper.attachToAnchorClick();
		menuWrapper.addItem(MenuGlobals.APPS_ADD, R.string.menuAddApps);
		menuWrapper.addItem(MenuGlobals.APPS_REMOVE, R.string.menuRemoveApps);
		menuWrapper.addItem(MenuGlobals.ABOUT, R.string.menuAbout);

	}

	public void refreshAppList() {
		try {
			setListAdapter();
		}
		catch (Throwable t) {
			ErrorHandler.showCrashReport(context, t);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		DialogWithExitPoints.handleActivityResume(this);
	}

}
