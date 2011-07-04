/*
 * Copyright (C) 2010 Oliver Egger, http://www.egger-loser.ch/
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ch.mieng.meteo.mythenquai.mod;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class WeatherData {
	
	/*
	 * { "Globalstrahlung": "5 W/m²", "Luftdruck QFE": "967.2 hPa",
	 * "Luftfeuchte": "73 %", "Lufttemperatur": "14.4 °C", "Niederschlag":
	 * "0 mm", "Pegel": "406.06 m", "Wassertemperatur": "17.6 °C",
	 * "Windböen (max) 10 min.": "2.4 m/s", "Windchill": "14.4 °C",
	 * "Windgeschw. Ø 10min.": "1.2 m/s", "Windrichtung": "NW (316°) ",
	 * "Windstärke Ø 10 min.": "1 bft", "Zeit": "16.09.2010 22:10 Uhr"}
	 */

	final static String GLOBALSTRAHLUNG = "Globalstrahlung";
	final static String LUFTDRUCK = "Luftdruck QFE";
	final static String LUFTFEUCHTE = "Luftfeuchte";
	final static String LUFTTEMPERATUR = "Lufttemperatur";
	final static String NIEDERSCHLAG = "Niederschlag";
	final static String PEGEL = "Pegel";
	final static String TAUPUNKT = "Taupunkt";
	final static String WASSERTEMPERATUR = "Wassertemperatur";
	final static String WINDBOEWEN = "Windb�en (max) 10 min.";
	final static String WINDCHILL = "Windchill";
	final static String WINDGESCHW = "Windgeschw. Ø 10min.";
	final static String WINDRICHTUNG = "Windrichtung";
	final static String WINDSTAERKE = "Windst�rke Ø 10 min.";
	final static String ZEIT = "Zeit";
	
	final static String sortOrder[] = {LUFTTEMPERATUR, LUFTFEUCHTE, WASSERTEMPERATUR, TAUPUNKT, WINDBOEWEN, WINDGESCHW, WINDSTAERKE, WINDRICHTUNG, NIEDERSCHLAG, LUFTDRUCK, WINDCHILL, PEGEL, ZEIT };
	
	private JSONObject json;
	

	private String convertStreamToString(InputStream is) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
		} catch (UnsupportedEncodingException e1) {
			Log.e("convertStreamToString", "", e1);
		}
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			Log.e("convertStreamToString", "", e);
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				Log.e("convertStreamToString", "", e);
			}
		}
		return sb.toString();
	}

	public JSONObject getJson(String url) {

		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url);
		HttpResponse httpResponse;
		try {
			httpResponse = httpClient.execute(httpGet);
			Log.i("response status", httpResponse.getStatusLine().toString());
			HttpEntity entity = httpResponse.getEntity();
			if (entity != null) {
				InputStream instream = entity.getContent();
				String result = convertStreamToString(instream);
				Log.i("resultstring", result);

				JSONObject json = new JSONObject(result);
				Log.i("resultasjson", json.toString());
				instream.close();
				return json;
			}
		} catch (ClientProtocolException e) {
			Log.e("exception in getJson", "", e);
		} catch (IOException e) {
			Log.e("exception in getJson", "", e);
		} catch (JSONException e) {
			Log.e("exception in getJson", "", e);
		}
		return null;
	}

	public String[] getJsonAsStringArray(JSONObject json) {
		ArrayList<String> strings = new ArrayList<String>();

		JSONArray names = json.names();
		JSONArray values = null;
		try {
			values = json.toJSONArray(names);
		} catch (JSONException e) {
			Log.e("parseJsonToStringArray", "", e);
		}
		for (int i = 0; i < values.length(); i++) {
			try {
				strings.add(names.getString(i) + " " + values.getString(i));
			} catch (JSONException e) {
				Log.e("parseJsonToStringArray", "", e);
			}
		}
		return strings.toArray(new String[] {});
	}

	public ArrayList<HashMap<String, String>> getJsonAsArrayList(
			JSONObject json, String mapName, String mapValue) {
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		JSONArray names = json.names();
		JSONArray values = null;
		try {
			values = json.toJSONArray(names);
		} catch (JSONException e) {
			Log.e("parseJsonToStringArray", "", e);
		}
		
		for (int i = 0; i < names.length(); i++) {
			try {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put(mapName, names.getString(i));
				map.put(mapValue, values.getString(i));
				
				list.add(map);
			} catch (JSONException e) {
				Log.e("getJsonAsStringArray", "", e);
			}
		}
		return list;
	}
	
	public ArrayList<HashMap<String, String>> getJsonAsArraySortedList(
			JSONObject json, String mapName, String mapValue, String[] nameSortOrder ) {
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>(nameSortOrder.length);
		JSONArray names = new JSONArray(Arrays.asList(nameSortOrder));
		JSONArray values = null;
		try {
			values = json.toJSONArray(names);
		} catch (JSONException e) {
			Log.e("parseJsonToStringArray", "", e);
		}
		for (int i = 0; i < nameSortOrder.length; i++) {
			try {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put(mapName, names.getString(i));
				map.put(mapValue, values.getString(i));
				list.add(map);
			} catch (JSONException e) {
				Log.e("getJsonAsStringArray", "", e);
			}
		}
		return list;
	}

	public ArrayList<HashMap<String, String>> getWeatherValues(String mapName,
			String mapValue) {

		return getJsonAsArraySortedList(json, mapName, mapValue, sortOrder);
	}
	
	public WeatherData(boolean isTiefenbrunnen) {
		if (isTiefenbrunnen) {
			json = getJson("http://mi-eng.appspot.com/meteo/tiefenbrunnen");
		} else {
			json = getJson("http://mi-eng.appspot.com/meteo/mythenquai");
		}
	}
	
	public boolean hasJson() {
		return json!=null;	
	}
	
	/**
	 * FIXME should round when shrinking
	 * 14.4 °C shrinnk to no digit
	 * @return
	 */
	private String shortenTemparture(String temp) {
		if (temp==null) {
			return null;
		}
		return temp.replaceAll("[\\sC]", "");
	}
	
	public String getWeatherAirTemperature() {
		if (json!=null) {
			try {
				return shortenTemparture(json.getString(LUFTTEMPERATUR));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return "";
	}
	
	public String getWeatherAirHumidity() {
		if (json!=null) {
			try {
				return shortenTemparture(json.getString(LUFTFEUCHTE));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return "";
			
	}

	
	public String getWeatherLakeTemperature() {
		if (json!=null) {
			try {
				return shortenTemparture(json.getString(WASSERTEMPERATUR));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return "";
	}

	
	
}