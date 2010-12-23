package com.dynamicg.bookmarkTree.backup;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;

import com.dynamicg.common.SystemUtil;



import android.util.Xml;

public class XmlReader {

	private final XmlPullParser parser;
	
	private int evtype;
	
	public XmlReader(File xmlfile) 
	throws Exception {
		FileInputStream fis = new FileInputStream(xmlfile);
		parser = Xml.newPullParser();
		parser.setInput(fis, XmlWriter.ENCODING);
	}
	
	private void nextItem() 
	throws Exception {
		parser.next();
		evtype = parser.getEventType();
	}
	
	private String getNextText()
	throws Exception {
		nextItem();
		return parser.getText();
	}
	
	private long getNextLong()
	throws Exception {
		nextItem();
		return Long.parseLong(parser.getText());
	}
	
	public ArrayList<RawDataBean> read() 
	throws Exception {
		
		ArrayList<RawDataBean> list = new ArrayList<RawDataBean>();
		
		RawDataBean bean=null;
		String tag;
		while(true) {
			
			nextItem();
			
			if (evtype==XmlPullParser.END_DOCUMENT) {
				break;
			}
			
			if (evtype==XmlPullParser.START_TAG) {
				tag = parser.getName();
				if (equals(tag, Tags.ROW)) {
					bean = new RawDataBean();
					list.add(bean);
					nextItem();
				}
				else if (equals(tag, Tags.ID)) {
					bean.id = getNextLong();
				}
				else if (equals(tag, Tags.CREATED)) {
					bean.created = getNextLong();
				}
				else if (equals(tag, Tags.TITLE)) {
					bean.title = getNextText();
				}
				else if (equals(tag, Tags.URL)) {
					bean.url = getNextText();
				}
				else if (equals(tag, Tags.FAVICON)) {
					bean.favicon = encodeIconData(getNextText());
				}
			}
			
		}

		return list;
		
	}

	private static boolean equals(String s, String tag) {
		return s.equals(tag);
	}
	
	private byte[] encodeIconData(String s) {
		if (s==null) {
			return null;
		}
		String hexString = s.replaceAll("\n", "");
		try {
			return Hex.decodeHex(hexString);
		}
		catch (RuntimeException e) {
			SystemUtil.dumpIfDevelopment(e);
			return null;
		}
	}
	
}