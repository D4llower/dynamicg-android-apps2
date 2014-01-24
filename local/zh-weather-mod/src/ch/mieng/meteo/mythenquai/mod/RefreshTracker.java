package ch.mieng.meteo.mythenquai.mod;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.SystemClock;

public class RefreshTracker {

	private static final String KEY_INIT_MS = "initMS";

	public static boolean needsInit(Context context) {
		SharedPreferences prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE);
		long initMS = prefs.getLong(KEY_INIT_MS, 0);
		long systemStartMS = System.currentTimeMillis() - SystemClock.elapsedRealtime();
		if (systemStartMS>initMS) {
			Editor editor = prefs.edit();
			editor.putLong(KEY_INIT_MS, System.currentTimeMillis());
			editor.commit();
			return true;
		}
		return false;
	}

}
