/*
 * Copyright (C) 2010 Oliver Egger, http://www.egger-loser.ch/
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ch.mieng.meteo.mythenquai.mod;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class WeatherView extends ListActivity {

	static WeatherData currentWeatherData;
	public ArrayList<HashMap<String, String>> values;

	static final String ACTION_UPDATE = "ch.mieng.meteo.Mythenquai.WeatherData.UPDATE";

	public BroadcastReceiver updateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			upateWeatherValues();
		}

	};

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(updateReceiver);
	}

	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(updateReceiver, new IntentFilter(ACTION_UPDATE));
	}

	public void refreshWeatherData() {
		// ProgressDialog dialog = ProgressDialog.show(this, "",
		// "Aktualisieren ...", true, true);

		startService(new Intent(this, UpdateService.class));

		// dialog.dismiss();
	}

	public void upateWeatherValues() {
		values = currentWeatherData
				.getWeatherValues("name", "value");
		setListAdapter(new SimpleAdapter(this, values, R.layout.list_item,
				new String[] { "name", "value" }, new int[] { R.id.NAME_CELL,
						R.id.VALUE_CELL }));
	}
	
	public void startChartFlotView(int pos) {
		Intent chartIntent = new Intent(this,ChartFlotView.class);
		HashMap<String, String> hashMap = values.get(pos);
		String name = hashMap.get("name");
		if (!WeatherData.ZEIT.equals(name)) {
			chartIntent.putExtra(ChartFlotView.MEASUREMENTVALUE,name);
			startActivity(chartIntent);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.refreshWeatherData();

		ListView lv = getListView();
		lv.setTextFilterEnabled(true);

		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				startChartFlotView(position);
			}
		});

	}

	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
	}

	/* Creates the menu items */
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	/* Handles item selections */
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.listview_menu_refesh:
			Log.i(this.getClass().getName(), "refresh");
			refreshWeatherData();
			return true;
		case R.id.listview_menu_help:
			Log.i(this.getClass().getName(), "help");
			startActivity(new Intent(this,HelpWebView.class));

//			View webViewLayout = getLayoutInflater().inflate(R.layout.help,
//					null);
//
//			WebView webView = (WebView) webViewLayout
//					.findViewById(R.id.help_web_view);
//			webView.loadUrl(getString(R.string.help_url));
//			
//			setContentView(webView);
//
//
//			new AlertDialog.Builder(this).setView(webViewLayout)
//					.setTitle(R.string.help_title)
//					.setPositiveButton(android.R.string.ok, null).create()
//					.show();

			return true;
		case R.id.listview_menu_preferences:
			Log.i(this.getClass().getName(), "preferences");
			startActivity(new Intent(this,SettingsView.class));
		}

		return false;
	}

	public boolean onContextItemSelected(MenuItem item) {
		if (onOptionsItemSelected(item)) {
			return true;
		}
		return super.onContextItemSelected(item);
	}

	public static String getStationIndicator(Context context) {
		return SettingsView.isTiefenbrunnen(context) ? "T" : "M";
	}
	
}