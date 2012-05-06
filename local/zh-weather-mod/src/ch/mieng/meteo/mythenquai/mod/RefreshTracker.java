package ch.mieng.meteo.mythenquai.mod;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.SystemClock;

public class RefreshTracker {

	private static final String KEY_UPTIME = "uptime";
	
	private static SharedPreferences getPrefs(Context context) {
		return context.getSharedPreferences("prefs", Context.MODE_PRIVATE);
	}
	
	private static long getUptime() {
		return SystemClock.uptimeMillis() / 1000l;
	}
	
	public static void registerDataLoaded(Context context) {
		Editor ed = getPrefs(context).edit();
		String uptime = Long.toString(getUptime());
		ed.putString(KEY_UPTIME, uptime);
		ed.commit();
		//System.err.println("##### registerDataLoaded() => "+uptime);
	}
	
	public static boolean needsInit(Context context) {
		String last = getPrefs(context).getString(KEY_UPTIME, "0");
		//System.err.println("##### needsInit() "+Long.parseLong(last)+"/"+getUptime()+"/"+(Long.parseLong(last) < getUptime()));
		return Long.parseLong(last) < getUptime();
	}
	
}
