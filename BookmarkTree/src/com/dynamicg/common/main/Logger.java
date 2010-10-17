package com.dynamicg.common.main;

import android.util.Log;

public class Logger {

	private static boolean TRACE_ENABLED ;
	private static boolean DEBUG_ENABLED ;
	private static boolean INFO_ENABLED ;

	static {
		resetLogging();
	}

	public static void resetLogging() {
		if ( SystemUtil.isDevelopmentOrDevDevice() ) {
			TRACE_ENABLED = false;
			DEBUG_ENABLED = true;
			INFO_ENABLED = true;
		}
		else {
			TRACE_ENABLED = false;
			DEBUG_ENABLED = false;
			INFO_ENABLED = false;
		}
	}
	
	public static void enable() {
		INFO_ENABLED = true;
		DEBUG_ENABLED = true;
	}
	
	public static void enableTrace() {
		TRACE_ENABLED = true;
	}
	
	public static void disable() {
		INFO_ENABLED = false;
		DEBUG_ENABLED = false;
		TRACE_ENABLED = false;
	}
	
	private String textPrefix;

	public Logger(Class<?> cls) {
		textPrefix = cls.getName();
		textPrefix = "DG/"+textPrefix.substring(textPrefix.lastIndexOf(".")+1);
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
	
	public void info(String text) {
		if (!INFO_ENABLED) {
			return;
		}
		Log.i(textPrefix, text);
	}

	public void info(String text, Object... args) {
		if (!INFO_ENABLED) {
			return;
		}
		StringBuffer sb = append(text, args);
		Log.i(textPrefix, sb.toString());
	}

	public void debug(String text) {
		if (!DEBUG_ENABLED) {
			return;
		}
		Log.d(textPrefix, text);
	}

	public void debug(String text, Object... args) {
		if (!DEBUG_ENABLED) {
			return;
		}
		StringBuffer sb = append(text, args);
		Log.d(textPrefix, sb.toString());
	}

	public void trace(String text) {
		if (!TRACE_ENABLED ) {
			return;
		}
		Log.v(textPrefix, text);
	}
	public void trace(String text, Object... args) {
		if (!TRACE_ENABLED ) {
			return;
		}
		StringBuffer sb = append(text, args);
		Log.v(textPrefix, sb.toString());
	}

	public void warn(String text) {
		Log.w(textPrefix, text);
	}

	public void warn(String text, Object... args) {
		StringBuffer sb = append(text, args);
		Log.w(textPrefix, sb.toString());
	}

	public void error(String text) {
		Log.e(textPrefix, text);
	}

	public void error(String text, Object... args) {
		StringBuffer sb = append(text, args);
		Log.e(textPrefix, sb.toString());
	}

	public boolean isDebugEnabled() {
		return DEBUG_ENABLED;
	}

	public boolean isTraceEnabled() {
		return TRACE_ENABLED;
	}

}
