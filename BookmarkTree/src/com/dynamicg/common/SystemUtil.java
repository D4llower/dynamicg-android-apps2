package com.dynamicg.common;

import java.io.PrintWriter;
import java.io.StringWriter;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.Toast;

public class SystemUtil {

	public static String getExceptionText(Throwable exception) {
		final int limit = 1200;

		if (exception==null) {
			return "<no exception>";
		}

		StringWriter sw = new StringWriter() ;
		exception.printStackTrace(new PrintWriter(sw)) ;
		String text = sw.getBuffer().toString();

		return text.length()>limit ? text.substring(0,limit)+"..." : text;

	}

	public static LayoutInflater getLayoutInflater(Context context) {
		return (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public static void toastShort(Context context, String text) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}

	public static boolean isHoneycombOrNewer() {
		return android.os.Build.VERSION.SDK_INT >= 11;
	}

	public static boolean isIcsOrNewer() {
		return android.os.Build.VERSION.SDK_INT >= 14;
	}

	public static void sleep(long time) {
		try {
			Thread.sleep(time);
		}
		catch (Throwable t) {}
	}

	public static boolean isInvalidBrowserContentUrl(Throwable exception) {
		// java.lang.IllegalArgumentException: Unknown URL content://browser/bookmarks
		// java.lang.IllegalArgumentException: Unknown URL content://com.android.browser/bookmarks
		return exception instanceof IllegalArgumentException
				&& exception.toString().indexOf("Unknown URL")>=0
				&& exception.toString().indexOf("/bookmarks")>=0
				;
	}

}
