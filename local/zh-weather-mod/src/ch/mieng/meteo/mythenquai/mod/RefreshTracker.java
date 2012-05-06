package ch.mieng.meteo.mythenquai.mod;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.SystemClock;

public class RefreshTracker {

	private static final String KEY_LAST_REFRESH = "lastRefreshSec";
	private static boolean log = false;
	
	private static SharedPreferences getPrefs(Context context) {
		return context.getSharedPreferences("prefs", Context.MODE_PRIVATE);
	}
	
	private static int getUptimeSS() {
		return (int)(SystemClock.uptimeMillis() / 1000l);
	}
	
	public static void registerDataLoaded(Context context) {
		int uptime = getUptimeSS();
		Editor editor = getPrefs(context).edit();
		editor.putInt(KEY_LAST_REFRESH, uptime);
		editor.commit();
		if (log) {
			System.err.println("##### registerDataLoaded() => "+uptime);
		}
	}
	
	public static boolean needsInit(Context context) {
		int lastDataRefresh = getPrefs(context).getInt(KEY_LAST_REFRESH, 0);
		boolean wasRestarted = lastDataRefresh == 0 || lastDataRefresh>getUptimeSS();
		if (log) {
			System.err.println("##### needsInit() "+lastDataRefresh+"/"+getUptimeSS()+"/"+wasRestarted);
		}
		return wasRestarted;
	}
	
}
