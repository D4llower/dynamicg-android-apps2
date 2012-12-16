package com.dynamicg.bookmarkTree.backup;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.zip.GZIPOutputStream;

import org.xmlpull.v1.XmlSerializer;

import android.text.format.Time;
import android.util.Xml;

import com.dynamicg.bookmarkTree.model.RawDataBean;
import com.dynamicg.common.Logger;
import com.dynamicg.common.XmlBackupException;

public class XmlWriter {

	private static final Logger log = new Logger(XmlWriter.class);

	public static final String ENCODING = "UTF-8";

	private final OutputStream fileos;
	private final XmlSerializer serializer;

	public XmlWriter(File xmlfile, ArrayList<RawDataBean> bookmarks, boolean useGZ)
			throws Exception {
		if (useGZ) {
			fileos = new GZIPOutputStream(new FileOutputStream(xmlfile));
		}
		else {
			fileos = new FileOutputStream(xmlfile);
		}
		serializer = Xml.newSerializer();

		serializer.setOutput(fileos, ENCODING);
		serializer.startDocument(null, Boolean.valueOf(true)); // standalone=true
		serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);

		writeXmlFile(bookmarks);
		close();

	}

	private void addTextNode(String tag, String value, boolean catchIllegalCharacters)
			throws Exception {
		serializer.startTag(null, tag);

		try {
			serializer.text(value);
		}
		catch (IllegalArgumentException e) {
			if (catchIllegalCharacters) {
				// serializer has printed some characters but then stumbled over an illegal one. try extracting and appending the remainder:
				serializer.text(extractRemainder(value));
			}
			else {
				throw e;
			}
		}

		serializer.endTag(null, tag);
	}

	private void addTextNode(String tag, long value)
			throws Exception {
		addTextNode(tag, Long.toString(value), false);
	}

	private static String split(final StringBuffer sb, final int linesize) {
		final int chunks = (sb.length()-1) / linesize;
		for ( int i=chunks;i>0;i-- ) {
			sb.insert(i*linesize, '\n');
		}
		return sb.toString();
	}

	private String getIconData(RawDataBean b) {
		String buffer;
		// buffer = Hex.encodeHex(MockIcon.getIcon(context), false) ;
		if (b.favicon!=null&&b.favicon.length>0) {
			buffer = Hex.encodeHex(b.favicon, false) ;
			return split ( new StringBuffer(buffer), 80);
		}
		else {
			return "";
		}
	}


	private void writeXmlFile(ArrayList<RawDataBean> bookmarks)
			throws Exception {

		serializer.startTag(null, Tags.BODY);

		for (RawDataBean b:bookmarks) {

			try {
				serializer.startTag(null, Tags.ROW);

				//addTextNode(Tags.ID, b.id); // ID is not restored so we skip it
				addTextNode(Tags.CREATED, b.created);

				//				if (log.debugEnabled) {
				//					b.fullTitle = b.fullTitle.toLowerCase() + "\udbba"+" " + b.fullTitle.toUpperCase() + "\udbba";
				//				}
				addTextNode(Tags.TITLE, b.fullTitle, true);
				addTextNode(Tags.URL, b.url, false);
				addTextNode(Tags.FAVICON, getIconData(b), false);

				serializer.endTag(null, Tags.ROW);
			}
			catch (Throwable t) {
				throw new XmlBackupException("["+b.fullTitle+"] failed", t);
			}
		}

		serializer.endTag(null, Tags.BODY);
	}

	private void close() throws Exception {

		Time t = new Time();
		t.setToNow();
		serializer.text("\n");
		serializer.comment(" export done "+t+" ");
		serializer.text("\n");

		serializer.endDocument();
		serializer.flush();
		fileos.close();
	}

	private static String extractRemainder(String text)
			throws IOException {
		// see https://mail.google.com/mail/u/0/?shva=1#search/bookmark+tree/13b66d96187f3813
		//		com.dynamicg.a.n: _____ failed
		//		at com.dynamicg.bookmarkTree.a.t.a(SourceFile:88)
		//		at com.dynamicg.bookmarkTree.a.t.<init>(SourceFile:31)
		//		at com.dynamicg.bookmarkTree.a.c.a(SourceFile:108)
		//		at com.dynamicg.bookmarkTree.f.i.run(SourceFile:52)
		//		at java.lang.Thread.run(Thread.java:856)
		//		Caused by: java.lang.IllegalArgumentException: Illegal character (dbba)
		//		at org.kxml2.io.KXmlSerializer.reportInvalidCharacter(KXmlSerializer.java:144)
		//		at org.kxml2.io.KXmlSerializer.writeEscaped(KXmlSerializer.java:130)
		//		at org.kxml2.io.KXmlSerializer.text(KXmlSerializer.java:536)
		//		at com.dynamicg.bookmarkTree.a.t.a(SourceFile:39)
		//		at com.dynamicg.bookmarkTree.a.t.a(SourceFile:81)
		//		... 4 more

		if (log.debugEnabled) {
			log.debug("##START##", text);
		}

		// clean buffer
		StringBuffer sb = new StringBuffer();

		// dummy xml stream
		XmlSerializer serializer = Xml.newSerializer();
		serializer.setOutput(new ByteArrayOutputStream(), ENCODING);
		serializer.startDocument(null, Boolean.valueOf(true)); // standalone=true
		serializer.startTag(null, "temp");

		boolean illegalFound=false;

		for (int i=0;i<text.length();i++) {
			try {
				serializer.text(text.substring(i,i+1));
				if (illegalFound) {
					// only append the remainder (i.e. skip the leading part of the string which has already been appended)
					sb.append(text.substring(i,i+1));
				}
			}
			catch (IllegalArgumentException e) {
				illegalFound = true;
				if (log.debugEnabled) {
					log.debug("##CATCH##", i);
				}
			}
		}

		if (log.debugEnabled) {
			log.debug("##DONE##", sb.toString());
		}

		return sb.toString();
	}

}
