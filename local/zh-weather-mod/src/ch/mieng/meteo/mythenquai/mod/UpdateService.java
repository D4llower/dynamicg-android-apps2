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

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

public class UpdateService extends Service implements Runnable{

	private static final String TAG = "UpdateService";

	/**
	 * Lock used when maintaining queue of requested updates.
	 */
	private static Object sLock = new Object();

	/**
	 * Flag if there is an update thread already running. We only launch a new
	 * thread if one isn't already running.
	 */
	private static boolean sThreadRunning = false;

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);

		// Only start processing thread if not already running
		synchronized (sLock) {
			if (!sThreadRunning) {
				sThreadRunning = true;
				new Thread(this).start();
			}
		}
	}


	@Override
	public IBinder onBind(Intent intent) {
		// We don't need to bind to this service
		return null;
	}


	@Override
	public void run() {
		Log.d(TAG, "Processing thread started");
		AppWidgetManager manager = AppWidgetManager.getInstance(this);

		try {
			WeatherData weatherData = new WeatherData(SettingsView.isTiefenbrunnen(getBaseContext()));
			if (!weatherData.hasJson()) {
				// certain devices have not the internet connectin ready during startup, lets wait ten seconds for it ...
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				weatherData = new WeatherData(SettingsView.isTiefenbrunnen(getBaseContext()));
			}

			if (weatherData.hasJson()) {
				// Build the widget update for today
				RemoteViews updateViews = TemperatureWidget.buildUpdate(this, weatherData);

				WeatherView.currentWeatherData = weatherData;

				// Push update for this widget to the home screen
				ComponentName thisWidget = new ComponentName(this, TemperatureWidget.class);
				manager.updateAppWidget(thisWidget, updateViews);

				// Send Broadcast to active View
				sendBroadcast(new Intent(WeatherView.ACTION_UPDATE));
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
		}

		synchronized (sLock) {
			sThreadRunning = false;
		}

		// No updates remaining, so stop service
		stopSelf();
		Log.d(TAG, "Processing thread stopped");
	}
}