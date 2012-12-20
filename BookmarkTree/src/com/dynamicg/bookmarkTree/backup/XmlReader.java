package com.dynamicg.bookmarkTree.backup;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;

import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

import com.dynamicg.bookmarkTree.backup.XmlSettingsHelper.PreferenceEntry;
import com.dynamicg.bookmarkTree.model.RawDataBean;
import com.dynamicg.common.Logger;

public class XmlReader {

	private final XmlPullParser parser;

	private int evtype;

	public final ArrayList<PreferenceEntry> settings = new ArrayList<PreferenceEntry>();

	public XmlReader(File xmlfile)
			throws Exception {
		InputStream fis = new FileInputStream(xmlfile);
		if (xmlfile.getName().endsWith(".gz")) {
			fis = new GZIPInputStream(fis);
		}
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
		try {
			return Long.parseLong(parser.getText());
		} catch (NumberFormatException e) {
			Logger.dumpIfDevelopment(e);
			return 0;
		}
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
				else if (equals(tag, Tags.CREATED)) {
					bean.created = getNextLong();
				}
				else if (equals(tag, Tags.TITLE)) {
					bean.fullTitle = getNextText();
				}
				else if (equals(tag, Tags.URL)) {
					bean.url = getNextText();
				}
				else if (equals(tag, Tags.FAVICON)) {
					bean.favicon = encodeIconData(getNextText());
				}
				else if (equals(tag, Tags.PREF_ENTRY)) {
					XmlSettingsHelper.readSettings(parser, settings);
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
			Logger.dumpIfDevelopment(e);
			return null;
		}
	}

}
