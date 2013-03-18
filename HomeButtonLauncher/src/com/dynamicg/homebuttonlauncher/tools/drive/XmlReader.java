package com.dynamicg.homebuttonlauncher.tools.drive;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.GZIPInputStream;

import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

public class XmlReader {

	private final XmlPullParser parser;
	private int evtype;

	public XmlReader(File file) throws Exception {
		InputStream fis = new GZIPInputStream(new FileInputStream(file));
		parser = Xml.newPullParser();
		parser.setInput(fis, XmlGlobals.ENCODING);
	}

	private void nextItem() throws Exception {
		parser.next();
		evtype = parser.getEventType();
	}

	public List<Map<String, String>> getContent()throws Exception {
		final List<Map<String, String>> content = new ArrayList<Map<String,String>>();
		Map<String, String> map = null;
		while(true) {
			nextItem();
			if (evtype==XmlPullParser.END_DOCUMENT) {
				break;
			}
			if (evtype==XmlPullParser.START_TAG) {
				String tag = parser.getName();
				if (XmlGlobals.TAG_BODY.equals(tag)) {
					// skip
				}
				else if (XmlGlobals.TAG_ENTRY.equals(tag)) {
					map = new TreeMap<String, String>();
					content.add(map);
				}
				else {
					nextItem();
					map.put(tag, parser.getText());
				}
			}
		}
		return content;
	}

}
