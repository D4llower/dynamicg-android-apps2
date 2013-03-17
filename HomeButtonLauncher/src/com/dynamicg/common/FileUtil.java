package com.dynamicg.common;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class FileUtil {

	private static final int BUFFERSIZE = 4096;

	public static String getZipFileContent(File file) throws Exception {
		GZIPInputStream zis = new GZIPInputStream(new FileInputStream(file));
		ByteArrayOutputStream os = new ByteArrayOutputStream();

		byte[] buf = new byte[BUFFERSIZE];
		int len;
		while ((len = zis.read(buf, 0, BUFFERSIZE)) > -1) {
			os.write(buf, 0, len);
		}
		os.close();
		zis.close();
		return os.toString();
	}

	public static void writeZipFile(File file, String s) throws Exception {
		GZIPOutputStream zstream = new GZIPOutputStream(new FileOutputStream(file), BUFFERSIZE);
		zstream.write(s.getBytes());
		zstream.finish();
		zstream.close();
	}

}
