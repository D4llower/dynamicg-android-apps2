package ch.mieng.meteo.mythenquai.mod;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.SystemClock;

public class RefreshTracker {

	private static final String KEY_LAST_REFRESH = "lastRefresh";
	private static boolean log = true;
	
	private static SharedPreferences getPrefs(Context context) {
		return context.getSharedPreferences("prefs", Context.MODE_PRIVATE);
	}
	
	private static long getUptime() {
		return SystemClock.uptimeMillis() / 1000l;
	}
	
	public static void registerDataLoaded(Context context) {
		String uptime = Long.toString(getUptime());
		Editor editor = getPrefs(context).edit();
		editor.putString(KEY_LAST_REFRESH, uptime);
		editor.commit();
		if (log) {
			System.err.println("##### registerDataLoaded() => "+uptime);
		}
	}
	
	public static boolean needsInit(Context context) {
		String lastDataRefresh = getPrefs(context).getString(KEY_LAST_REFRESH, "0");
		boolean wasRestarted = Long.parseLong(lastDataRefresh) == 0 || getUptime() < Long.parseLong(lastDataRefresh);
		if (log) {
			System.err.println("##### needsInit() "+Long.parseLong(lastDataRefresh)+"/"+getUptime()+"/"+wasRestarted);
		}
		return wasRestarted;
	}
	
}
