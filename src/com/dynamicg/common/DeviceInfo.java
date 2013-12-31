package com.dynamicg.common;

import java.io.File;
import java.lang.reflect.Field;

import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

public class DeviceInfo {

	private static String getValue(Class<?> c, String key) {
		// need to do introspection as this is some fields are >API3
		try {
			Field f = c.getDeclaredField(key);
			if (f!=null) {
				Object value = f.get(null);
				return value.toString();
			}
		}
		catch (Throwable t) {
		}
		return "";
	}

	private static String getNameAndValue(Class<?> c, String key) {
		String name = key.substring(0,1).toUpperCase() + key.substring(1).toLowerCase();
		return name+": "+getValue(c, key);
	}

	/*
	 * see
	 * http://www.glbenchmark.com/phonedetails.jsp?benchmark=glpro11&D=Samsung+SGH-I997+Infuse+4G&testgroup=system
	 * http://www.glbenchmark.com/phonedetails.jsp?benchmark=glpro11&D=Samsung+GT-I9000+Galaxy+S&testgroup=system
	 */
	public static String getDeviceInfo() {
		Class<?> build = Build.class;
		String NL = "\n";
		return getNameAndValue(build,"MODEL")
				+ NL + getNameAndValue(build,"DEVICE")
				+ NL + getNameAndValue(build,"BRAND")
				+ NL + getNameAndValue(build,"MANUFACTURER")
				+ NL + getNameAndValue(build,"DISPLAY")
				+ NL + "SDK: " + Build.VERSION.SDK_INT+", "+Build.VERSION.RELEASE
				+ NL + "Free space MB: " + dumpStorage()
				;
	}

	private static String dumpStorage(File path) {
		long oneMB=1024l * 1024l;
		StatFs stat = new StatFs(path.getPath());
		long freeMB = ( (long)stat.getAvailableBlocks() * (long)stat.getBlockSize() ) / oneMB;
		long totalMB = ( (long)stat.getBlockCount() * (long)stat.getBlockSize() ) / oneMB;
		return freeMB+" of "+totalMB;
	}

	private static String dumpStorage() {
		try {
			return "Phone "+dumpStorage(Environment.getDataDirectory())
					+ ", Ext "+dumpStorage(Environment.getExternalStorageDirectory());
		}
		catch (Throwable t1) {
			try {
				return "Phone "+dumpStorage(Environment.getDataDirectory());
			}
			catch (Throwable t2) {
				return "?";
			}
		}
	}

}
