package com.dynamicg.common;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class FileUtil {

	public static String getContentAsString(File f)
			throws Exception {
		FileReader r = new FileReader(f);
		StringBuilder sb=new StringBuilder();
		char[] buffer = new char[2048];
		int len;
		while ( (len=r.read(buffer))>0 ) {
			sb.append(buffer, 0, len);
		}

		try {
			r.close();
		}
		catch (Throwable t) {
			// ignore
		}

		return sb.toString();
	}

	public static void writePlain(File file, String s)
			throws Exception {
		FileWriter w = new FileWriter(file);
		w.append(s);
		w.flush();
		w.close();
	}

}
