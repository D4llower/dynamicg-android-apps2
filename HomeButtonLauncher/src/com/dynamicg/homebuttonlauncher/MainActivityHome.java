package com.dynamicg.homebuttonlauncher;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;

// see https://plus.google.com/104570711580136846518/posts/QpqfXXigAWW

//note we cannot filter the app on "needs soft home button" (i.e. a non-physical home button like galaxy nexus)
//http://developer.android.com/guide/topics/manifest/uses-feature-element.html
public class MainActivityHome extends Activity {

	private static final int MENU_ADD = 1;
	private static final int MENU_REMOVE = 2;
	private static final int MENU_ABOUT = 3;

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
		ListView list = (ListView)findViewById(R.id.applist);
		list.setAdapter(adapter);
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				appSelected(appList.get(position));
			}
		});
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
		final PopupMenu popupMenu = new PopupMenu(context, anchor);
		final Menu menu = popupMenu.getMenu();

		anchor.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				popupMenu.show();
			}
		});

		popupMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				try {
					dispatchMenuItem(item.getItemId());
				}
				catch (Throwable t) {
					ErrorHandler.showCrashReport(context, t);
				}
				return true;
			}
		});

		/*
		 * menu items
		 */
		menu.add(0, MENU_ADD, 0, R.string.menuAddApps);
		menu.add(0, MENU_REMOVE, 0, R.string.menuRemoveApps);
		menu.add(0, MENU_ABOUT, 0, R.string.menuAbout);

	}

	private void dispatchMenuItem(int id) {
		if (id==MENU_ADD) {
			new AppConfigDialog(MainActivityHome.this, settings, AppConfigDialog.ACTION_ADD).show();
		}
		else if (id==MENU_REMOVE) {
			new AppConfigDialog(MainActivityHome.this, settings, AppConfigDialog.ACTION_REMOVE).show();
		}
		else if (id==MENU_ABOUT) {
			new AboutDialog().show(context, getLayoutInflater());
		}
	}

	public void refreshAppList() {
		try {
			setListAdapter();
		}
		catch (Throwable t) {
			ErrorHandler.showCrashReport(context, t);
		}
	}

}
