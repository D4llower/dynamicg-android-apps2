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


import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class TemperatureWidget extends AppWidgetProvider {

	public static final long INTERVALL_MM = 10l * 60l * 1000l;
	private static final long MAX_AGE_MILLI = 3l * 60l * 60l * 1000l;

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		// set click intent on start
		RemoteViews updateViews = new RemoteViews(context.getPackageName(), R.layout.widget);
		setClickIntent(context, updateViews);
		appWidgetManager.updateAppWidget(appWidgetIds, updateViews);

		// all updates are triggered through alarm manager
		final AlarmManager am = (AlarmManager)context.getSystemService(Activity.ALARM_SERVICE);
		Intent widgetUpdateIntent = new Intent(context, UpdateService.class);
		PendingIntent pi = PendingIntent.getService(context, 0, widgetUpdateIntent, 0);
		am.setInexactRepeating(AlarmManager.RTC, System.currentTimeMillis(), INTERVALL_MM, pi);
	}

	private static void setClickIntent(Context context, RemoteViews updateViews) {
		Intent intent = new Intent(context, WeatherView.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
		updateViews.setOnClickPendingIntent(R.id.widget, pendingIntent);
	}

	static public RemoteViews buildUpdate(Context context, WeatherData weatherData) {

		RemoteViews updateViews = null;

		updateViews = new RemoteViews(context.getPackageName(), R.layout.widget);

		String air = weatherData.getWeatherAirTemperature();
		long lastRefresh = weatherData.parseTime();
		if (lastRefresh<System.currentTimeMillis()-MAX_AGE_MILLI) {
			air = "--";
		}
		updateViews.setTextViewText(R.id.TAIR, air);

		String time = WeatherView.getStationIndicator(context) + " " + weatherData.getDisplayTime();
		updateViews.setTextViewText(R.id.ZEIT, time);

		setClickIntent(context, updateViews);
		DashClockZhWeatherExtension.saveCurrentCondition(context, air, time);
		return updateViews;
	}

}
