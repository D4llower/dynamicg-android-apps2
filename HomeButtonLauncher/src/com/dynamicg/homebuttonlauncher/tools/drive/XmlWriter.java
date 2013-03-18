package com.dynamicg.homebuttonlauncher.tools.drive;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

import org.xmlpull.v1.XmlSerializer;

import android.util.Xml;

public class XmlWriter {

	private final OutputStream fileos;
	private final XmlSerializer serializer;

	public XmlWriter(File file) throws Exception {
		fileos = new GZIPOutputStream(new FileOutputStream(file));
		serializer = Xml.newSerializer();
		serializer.setOutput(fileos, XmlGlobals.ENCODING);
		serializer.startDocument(null, Boolean.valueOf(true));
		//serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
	}

	private void addText(String tag, String value) throws Exception {
		boolean iterateBuffer = XmlGlobals.ENTRY_VALUE.equals(tag);
		serializer.startTag(null, tag);
		if (iterateBuffer) {
			char[] buffer = value.toCharArray();
			for (int i=0;i<buffer.length;i++) {
				try {
					serializer.text(buffer, i, 1);
				}
				catch (IllegalArgumentException e) {
					serializer.text(" ");
				}
			}
		}
		else {
			serializer.text(value);
		}
		serializer.endTag(null, tag);
	}

	public void write(List<Map<String, String>> content) throws Exception {
		serializer.startTag(null, XmlGlobals.TAG_BODY);
		for (Map<String, String> map:content) {
			serializer.startTag(null, XmlGlobals.TAG_ENTRY);
			for (String key:map.keySet()) {
				addText(key, map.get(key));
			}
			serializer.endTag(null, XmlGlobals.TAG_ENTRY);
		}
		serializer.endTag(null, XmlGlobals.TAG_BODY);

		// DONE - close
		serializer.endDocument();
		serializer.flush();
		fileos.flush();
		fileos.close();
	}

}
