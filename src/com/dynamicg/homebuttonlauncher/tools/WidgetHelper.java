package com.dynamicg.homebuttonlauncher.tools;

import java.util.ArrayList;

import android.app.Activity;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dynamicg.homebuttonlauncher.AppEntry;
import com.dynamicg.homebuttonlauncher.MainActivityHome;
import com.dynamicg.homebuttonlauncher.R;
import com.dynamicg.homebuttonlauncher.dialog.AppConfigDialog;

public class WidgetHelper {

	private static final String PREFIX_WIDGET = "wg-";
	private static final String SEPARATOR_RES = "|";

	private MainActivityHome activity;
	private AppWidgetManager mAppWidgetManager;
	private AppWidgetHost mAppWidgetHost;
	private AppConfigDialog configDialog;

	public WidgetHelper(MainActivityHome activity, AppConfigDialog configDialog) {
		this.activity = activity;
		this.configDialog = configDialog;
		mAppWidgetManager = activity.getAppWidgetManager();
		mAppWidgetHost = activity.getAppWidgetHost();
	}

	/**
	 * Launches the menu to select the widget. The selected widget will be on
	 * the result of the activity.
	 */
	public void selectWidget() {
		int appWidgetId = this.mAppWidgetHost.allocateAppWidgetId();
		Intent pickIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_PICK);
		pickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		addEmptyData(pickIntent);
		activity.startActivityForResult(pickIntent, R.id.REQUEST_PICK_APPWIDGET);
	}

	/**
	 * This avoids a bug in the com.android.settings.AppWidgetPickActivity,
	 * which is used to select widgets. This just adds empty extras to the
	 * intent, avoiding the bug.
	 * 
	 * See more: http://code.google.com/p/android/issues/detail?id=4272
	 */
	void addEmptyData(Intent pickIntent) {
		ArrayList<AppWidgetProviderInfo> customInfo = new ArrayList<AppWidgetProviderInfo>();
		pickIntent.putParcelableArrayListExtra(AppWidgetManager.EXTRA_CUSTOM_INFO, customInfo);
		ArrayList<Bundle> customExtras = new ArrayList<Bundle>();
		pickIntent.putParcelableArrayListExtra(AppWidgetManager.EXTRA_CUSTOM_EXTRAS, customExtras);
	}


	public void processResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == R.id.REQUEST_PICK_APPWIDGET) {
				configureWidget(data);
			} else if (requestCode == R.id.REQUEST_CREATE_APPWIDGET) {
				createWidget(data);
			}
		} else if (resultCode == Activity.RESULT_CANCELED && data != null) {
			int appWidgetId = data.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
			if (appWidgetId != -1) {
				mAppWidgetHost.deleteAppWidgetId(appWidgetId);
			}
		}
	}

	private void configureWidget(Intent data) {
		Bundle extras = data.getExtras();
		int appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
		AppWidgetProviderInfo appWidgetInfo = mAppWidgetManager.getAppWidgetInfo(appWidgetId);
		if (appWidgetInfo.configure != null) {
			Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
			intent.setComponent(appWidgetInfo.configure);
			intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
			activity.startActivityForResult(intent, R.id.REQUEST_CREATE_APPWIDGET);
		} else {
			createWidget(data);
		}
	}

	private void createWidget(Intent data) {
		Bundle extras = data.getExtras();
		int appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
		if (appWidgetId>=0) {
			saveWidget(activity, configDialog, appWidgetId);
		}
	}

	public static View getWidgetView(MainActivityHome activity, LayoutInflater inflater, AppEntry entry) {
		int appWidgetId = entry.getAppWidgetId();
		if (appWidgetId==0) {
			return null;
		}
		AppWidgetProviderInfo appWidgetInfo = activity.getAppWidgetManager().getAppWidgetInfo(appWidgetId);
		AppWidgetHost appWidgetHost = activity.getAppWidgetHost();
		AppWidgetHostView hostView = appWidgetHost.createView(activity, appWidgetId, appWidgetInfo);
		hostView.setAppWidget(appWidgetId, appWidgetInfo);

		ViewGroup container = (ViewGroup)inflater.inflate(R.layout.widget_container, null);
		container.addView(hostView);
		return container;
	}

	public static void remove(MainActivityHome activity, String component) {
		AppWidgetHost appWidgetHost = activity.getAppWidgetHost();
		appWidgetHost.deleteAppWidgetId(getAppWidgetId(component));
	}


	public static boolean isWidgetComponent(String component) {
		return component.startsWith(PREFIX_WIDGET) && component.contains(SEPARATOR_RES);
	}

	public static void saveWidget(MainActivityHome activity, AppConfigDialog optionalDialog, int appWidgetId) {
		String componentToSave = PREFIX_WIDGET + ShortcutHelper.getAndIncrementNextId() + SEPARATOR_RES + appWidgetId;
		activity.saveShortcutComponent(componentToSave);
		AppConfigDialog.afterSave(activity, optionalDialog);
	}

	public static int getAppWidgetId(String component) {
		String id = component.substring(component.indexOf(SEPARATOR_RES)+1);
		try {
			return Integer.parseInt(id);
		}
		catch (NumberFormatException e) {
			return 0;
		}
	}

}
