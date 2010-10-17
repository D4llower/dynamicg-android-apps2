package com.dynamicg.common.main;

import java.util.ArrayList;
import java.util.regex.Pattern;

import android.content.Context;

public class StringUtil {

	public static String toString(Object... objects) {
		if (objects==null||objects.length==0) {
			return "<null>" ;
		}
		StringBuffer sb = new StringBuffer();
		for ( Object o:objects) {
			sb.append("[");
			sb.append ( o!=null?o.toString():"<null>");
			sb.append("]");
		}
		return sb.toString();
	}
	
	public static String toString(String... string) {
		return toString((Object[])string);
	}

	public static String commaSeparate(ArrayList<String> values) {
		return commaSeparate(values.toArray(new String[values.size()]));
	}
	
	public static String commaSeparate(String... values) {
		StringBuffer sb = new StringBuffer();
		for ( String s:values) {
			if (sb.length()>0) {
				sb.append(",");
			}
			sb.append(s);
		}
		return sb.toString();
	}
	
	/*
	 * convert first letter to uppercase
	 */
	public static String initcap(String value) {
		return value.substring(0,1).toUpperCase() + value.substring(1);
	}

	public static String leftpad(String text, String padSymbol, int length) {
		if ( text==null || text.length()==0 || text.length()>length ) {
			return text;
		}
		StringBuffer sb = new StringBuffer();
		for ( int i=0;i<length-text.length();i++) {
			sb.append(padSymbol);
		}
		sb.append(text);
		return sb.toString();
	}
	public static String leftpad(int value, String padSymbol, int length) {
		return leftpad(Integer.toString(value),padSymbol,length);
	}

	public static String rightpad(String text, String padSymbol, int length) {
		if ( text==null || text.length()==0 || text.length()>length ) {
			return text;
		}
		StringBuffer sb = new StringBuffer();
		sb.append(text);
		for ( int i=0;i<length-text.length();i++) {
			sb.append(padSymbol);
		}
		return sb.toString();
	}
	
	/*
	 * RE-safe split
	 */
	public static String[] split(String value, String expr) {
		return value.split(Pattern.quote(expr));
	}
	
	/*
	 * RE-safe replace
	 */
	public static String replaceAll(String value, String search, String replace) {
		return value.replaceAll(Pattern.quote(search),replace);
	}
	public static String replaceFirst(String value, String search, String replace) {
		return value.replaceFirst(Pattern.quote(search),replace);
	}

	/*
	 * for data export and "break to single line"
	 */
	public static String removeNewlines(String text, String escapeChar) {
		if (text.indexOf("\n")==-1) {
			return text;
		}
		return text.replaceAll("\n", escapeChar);
	}

	public static String getTextForTableCell(String text, int stopAt) {
		if (text==null||text.length()==0) {
			return text;
		}
		text = text.length()<=stopAt ? text : text.substring(0,stopAt) + "..."; 
		return StringUtil.removeNewlines(text," ");
	}
	
	public static String textWithParam(Context context, int res, String param1) {
		return replaceFirst ( context.getString(res), "{1}", param1 );
	}
	
}
