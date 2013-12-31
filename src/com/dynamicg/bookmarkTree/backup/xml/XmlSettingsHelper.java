package com.dynamicg.bookmarkTree.backup.xml;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeSet;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import android.content.SharedPreferences.Editor;

import com.dynamicg.bookmarkTree.BookmarkTreeContext;
import com.dynamicg.bookmarkTree.prefs.PreferencesWrapper;
import com.dynamicg.common.Logger;

public class XmlSettingsHelper {

	private static final Logger log = new Logger(XmlSettingsHelper.class);

	private static final String TYPE_STRING = "String";
	private static final String TYPE_INTEGER = "Integer";

	public static void writeSettings(XmlSerializer serializer)
			throws IOException {
		Map<String, ?> prefs = BookmarkTreeContext.settings.getAll();
		TreeSet<String> sortedKeys = new TreeSet<String>(prefs.keySet());
		for (String key : sortedKeys) {
			Object o = prefs.get(key);
			serializer.startTag(null, Tags.PREF_ENTRY);
			serializer.attribute(null, Tags.PREF_NAME, key);
			serializer.attribute(null, Tags.PREF_TYPE, o.getClass().getSimpleName());
			serializer.attribute(null, Tags.PREF_VALUE, o.toString());
			serializer.endTag(null, Tags.PREF_ENTRY);
		}
	}

	public static class PreferenceEntry {
		private String name;
		private String value;
		private String datatype;
	}

	public static void readSettings(XmlPullParser parser, ArrayList<PreferenceEntry> settings) {
		PreferenceEntry entry = new PreferenceEntry();
		entry.name = parser.getAttributeValue(null, Tags.PREF_NAME);
		entry.value = parser.getAttributeValue(null, Tags.PREF_VALUE);
		entry.datatype = parser.getAttributeValue(null, Tags.PREF_TYPE);
		settings.add(entry);

		if (log.isDebugEnabled) {
			log.debug("pref entry loaded", entry.name, entry.value, entry.datatype);
		}
	}

	public static void restore(ArrayList<PreferenceEntry> settingsFromXml) {
		Editor edit = BookmarkTreeContext.settings.edit();
		for (PreferenceEntry entry:settingsFromXml) {
			if (TYPE_STRING.equals(entry.datatype)) {
				edit.putString(entry.name, entry.value);
				log.debug("restore string", entry.name, entry.value);
			}
			else if (TYPE_INTEGER.equals(entry.datatype)) {
				try {
					edit.putInt(entry.name, Integer.parseInt(entry.value));
					log.debug("restore int", entry.name, entry.value);
				}
				catch (NumberFormatException e) {}
			}
		}
		edit.apply();

		// DONE - RELOAD CACHE
		PreferencesWrapper.afterRestore();
	}

}