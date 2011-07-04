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

import java.util.Date;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import ch.mieng.meteo.mythenquai.mod.SimpleGestureFilter.SimpleGestureListener;

public class ChartFlotView extends Activity implements SimpleGestureListener {

	private WebView webView;

	public static final String MEASUREMENTVALUE = "MEASUREMENTVALUE";

	private SimpleGestureFilter simpleGesturFilter;

	private class WebViewClientUrlLoading extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.chart_flot);
		webView = (WebView) findViewById(R.id.chart_flot_view);
		webView.setWebViewClient(new WebViewClientUrlLoading());

		String value = getIntent().getStringExtra(MEASUREMENTVALUE);

		webView.getSettings().setJavaScriptEnabled(true);
		webView.addJavascriptInterface(
				new ChartInterface(this.webView, (SettingsView
						.isTiefenbrunnen(getBaseContext()) ? "Tiefenbrunnen"
						: "Mythenquai"), value, SettingsView
						.isTiefenbrunnen(getBaseContext()), new Date()),
				"chartInterface");
		webView.getSettings().setSupportZoom(true);
		webView.setWebChromeClient(new WebChromeClient() {
			@SuppressWarnings("unused")
			public void onConsoleMessage(String message, int lineNumber,
					String sourceID) {
				Log.d("MyApplication", message + " -- From line " + lineNumber
						+ " of " + sourceID);
			}
		});

		simpleGesturFilter = new SimpleGestureFilter(this, this);
		simpleGesturFilter.setMode(SimpleGestureFilter.MODE_TRANSPARENT);

		webView.loadUrl("file:///android_asset/chart.html");
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
			webView.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onSwipe(int direction) {

		switch (direction) {

		case SimpleGestureFilter.SWIPE_RIGHT:
			webView.loadUrl("javascript:swipeRight()");
			break;
		case SimpleGestureFilter.SWIPE_LEFT:
			webView.loadUrl("javascript:swipeLeft()");
			break;
		case SimpleGestureFilter.SWIPE_DOWN:
		case SimpleGestureFilter.SWIPE_UP:
		}
	}

	@Override
	public void onDoubleTap() {

	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent me) {
		simpleGesturFilter.onTouchEvent(me);
		return super.dispatchTouchEvent(me);
	}

}
