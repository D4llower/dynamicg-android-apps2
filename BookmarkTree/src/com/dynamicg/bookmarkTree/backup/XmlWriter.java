package com.dynamicg.bookmarkTree.backup;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import org.xmlpull.v1.XmlSerializer;

import android.text.format.Time;
import android.util.Xml;

import com.dynamicg.bookmarkTree.model.RawDataBean;
import com.dynamicg.common.XmlBackupException;

public class XmlWriter {

	public static final String ENCODING = "UTF-8";
	
	private final FileOutputStream fileos;
	private final XmlSerializer serializer;
	
	public XmlWriter(File xmlfile, ArrayList<RawDataBean> bookmarks) 
	throws Exception {
		fileos = new FileOutputStream(xmlfile);        
		serializer = Xml.newSerializer();
		
		serializer.setOutput(fileos, ENCODING);
		serializer.startDocument(null, Boolean.valueOf(true)); // standalone=true
		serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
		
		writeXmlFile(bookmarks);
		close();
		
	}
	
	private void addTextNode(String tag, String value) 
	throws Exception {
		serializer.startTag(null, tag);
		serializer.text(value);
		serializer.endTag(null, tag);
	}
	
	private void addTextNode(String tag, long value) 
	throws Exception {
		addTextNode(tag, Long.toString(value));
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
				addTextNode(Tags.TITLE, b.fullTitle);
				addTextNode(Tags.URL, b.url);
				addTextNode(Tags.FAVICON, getIconData(b));
				
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
	
}
