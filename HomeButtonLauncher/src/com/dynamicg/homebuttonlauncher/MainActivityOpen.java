package com.dynamicg.homebuttonlauncher;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

// entry point to start as main app. not really required but some people put in 1 star ratings when there is no "open" after install
public class MainActivityOpen extends Activity {

	public static final String KEY = "dynamicg.MainActivityOpen";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//simply forward to the (single task) main app.
		//note if we extend from "MainActivityHome" instead we could end up having multiple screens
		Intent intent = new Intent(this, MainActivityHome.class);
		intent.putExtra(KEY, true);
		startActivity(intent);
		finish();
	}

}
