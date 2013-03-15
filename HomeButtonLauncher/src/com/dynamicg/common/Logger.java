package com.dynamicg.common;

import android.util.Log;

/*
 * this class will be replaced with LoggerPROD on production builds
 */
public class Logger {

	private static boolean TRACE_ENABLED = true;
	private static boolean DEBUG_ENABLED = true;

	public final boolean isTraceEnabled = TRACE_ENABLED;
	public final boolean isDebugEnabled = DEBUG_ENABLED;

	private final String textPrefix;

	public Logger(Class<?> cls) {
		if (DEBUG_ENABLED) {
			String clsname = cls.getName();
			textPrefix = "DG/"+clsname.substring(clsname.lastIndexOf(".")+1);
		}
		else {
			textPrefix = null;
		}
	}

	private static StringBuffer append(String text, Object... args) {
		StringBuffer sb = new StringBuffer(text);
		for ( int i=0;i<args.length;i++ ) {
			sb.append(" [");
			sb.append(args[i]);
			sb.append("]");
		}
		return sb;
	}

	public void warn(String text, Object... args) {
		StringBuffer sb = append(text, args);
		Log.w(textPrefix, sb.toString());
	}

	public void debug(String text, Object... args) {
		if (!DEBUG_ENABLED) {
			return;
		}
		StringBuffer sb = append(text, args);
		Log.d(textPrefix, sb.toString());
	}

	public void trace(String text, Object... args) {
		if (!TRACE_ENABLED) {
			return;
		}
		StringBuffer sb = append(text, args);
		Log.v(textPrefix, sb.toString());
	}

	public static void dumpIfDevelopment(Throwable e) {
		if (DEBUG_ENABLED) {
			e.printStackTrace(System.err);
		}
	}

}
