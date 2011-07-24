package com.dynamicg.taWebOpener;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;

public class TAWebOpenerActivity extends Activity {

	private static final String URL = "http://www.tagesanzeiger.ch/mobile/";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);

		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse(URL));
		this.startActivity(i);

		finish();
	}

}