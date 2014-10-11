package ch.mieng.meteo.mythenquai.mod;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.android.apps.dashclock.api.DashClockExtension;
import com.google.android.apps.dashclock.api.ExtensionData;

public class DashClockZhWeatherExtension extends DashClockExtension {

	private static final String KEY_AIR = "air";
	private static final String KEY_TIME = "time";

	private final Context context = this;

	@Override
	protected void onInitialize(boolean isReconnect) {
		super.onInitialize(isReconnect);
		setUpdateWhenScreenOn(true);
	}

	@Override
	protected void onUpdateData(int reason) {
		String[] recent = getRecent(context);
		ExtensionData data = new ExtensionData()
		.visible(true)
		.icon(R.drawable.ic_dashclock)
		.status(recent[0])
		.expandedTitle(recent[0])
		.expandedBody(recent[1])
		//.clickIntent(new Intent(context, Main.class))
		;
		publishUpdate(data);
	}

	public static void saveCurrentCondition(Context context, String air, String time) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		prefs
		.edit()
		.putString(KEY_AIR, air)
		.putString(KEY_TIME, time)
		.apply();
	}

	private static String[] getRecent(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String[] result = new String[2];
		result[0] = prefs.getString(KEY_AIR, "-");
		result[1] = prefs.getString(KEY_TIME, "-");
		return result;
	}
}
