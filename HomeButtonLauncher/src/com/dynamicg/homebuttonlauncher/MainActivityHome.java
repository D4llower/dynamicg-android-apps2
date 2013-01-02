package com.dynamicg.homebuttonlauncher;

import java.util.List;

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

import com.dynamicg.common.DialogWithExitPoints;
import com.dynamicg.homebuttonlauncher.dialog.AboutDialog;
import com.dynamicg.homebuttonlauncher.dialog.AppConfigDialog;
import com.dynamicg.homebuttonlauncher.dialog.PreferencesDialog;
import com.dynamicg.homebuttonlauncher.preferences.PreferencesManager;
import com.dynamicg.homebuttonlauncher.tools.AppHelper;
import com.dynamicg.homebuttonlauncher.tools.ErrorHandler;
import com.dynamicg.homebuttonlauncher.tools.IconProvider;
import com.dynamicg.homebuttonlauncher.tools.PopupMenuWrapper;
import com.dynamicg.homebuttonlauncher.tools.PopupMenuWrapper.PopupMenuItemListener;

// see https://plus.google.com/104570711580136846518/posts/QpqfXXigAWW

//note we cannot filter the app on "needs soft home button" (i.e. a non-physical home button like galaxy nexus)
//http://developer.android.com/guide/topics/manifest/uses-feature-element.html
public class MainActivityHome extends Activity {

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
			ErrorHandler.showCrashReport(context, t);
		}
	}

	private void main() {
		setContentView(R.layout.activity_main);
		preferences = new PreferencesManager(context);
		IconProvider.init(context);
		attachContextMenu();
		setListAdapter();
		setMinWidth();
	}

	public void refreshList() {
		try {
			setListAdapter();
			setMinWidth();
		}
		catch (Throwable t) {
			ErrorHandler.showCrashReport(context, t);
		}
	}

	private void setMinWidth() {
		int widthDim = preferences.prefSettings.getMinWidthDimen();
		float minWidth = context.getResources().getDimension(widthDim);
		findViewById(R.id.headerContainer).setMinimumWidth((int)minWidth);
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

		final List<AppEntry> appList = AppHelper.getSelectedAppsList(context, preferences.prefShortlist);
		final BaseAdapter adapter = new AppListAdapter(this, appList, preferences.prefSettings);
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
					new AppConfigDialog(MainActivityHome.this, preferences, MenuGlobals.APPS_ADD).show();
					break;
				case MenuGlobals.APPS_REMOVE:
					new AppConfigDialog(MainActivityHome.this, preferences, MenuGlobals.APPS_REMOVE).show();
					break;
				case MenuGlobals.ABOUT:
					new AboutDialog().show(context, getLayoutInflater());
					break;
				case MenuGlobals.PREFERENCES:
					new PreferencesDialog(MainActivityHome.this, preferences).show();
					break;
				}
			}
		};

		final PopupMenuWrapper menuWrapper = new PopupMenuWrapper(context, anchor, listener);
		menuWrapper.attachToAnchorClick();
		menuWrapper.addItem(MenuGlobals.APPS_ADD, R.string.menuAddApps);
		menuWrapper.addItem(MenuGlobals.APPS_REMOVE, R.string.menuRemoveApps);
		menuWrapper.addItem(MenuGlobals.ABOUT, R.string.menuAbout);
		menuWrapper.addItem(MenuGlobals.PREFERENCES, R.string.preferences);

	}

	@Override
	public void onResume() {
		super.onResume();
		DialogWithExitPoints.handleActivityResume(this);
	}

}
