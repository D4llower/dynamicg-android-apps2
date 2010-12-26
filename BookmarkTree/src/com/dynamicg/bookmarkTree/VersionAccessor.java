package com.dynamicg.bookmarkTree;

import java.lang.reflect.Field;

import com.dynamicg.common.Logger;

public class VersionAccessor {

	public static boolean isEclairOrHigher() {
		// need to do introspection as this is only available since API 4
		try {
			Class<?> c = android.os.Build.VERSION.class;
			Field f = c.getDeclaredField("SDK_INT");
			if (f!=null) {
				Object value = f.get(null);
				System.out.println("VersionAccessor - SDK_INT value is "+value);
				return (Integer)value >= 5 ; // eclair
			}
		}
		catch (Throwable t) {
			Logger.dumpIfDevelopment(t);
		}
		return false;
	}
	
}
