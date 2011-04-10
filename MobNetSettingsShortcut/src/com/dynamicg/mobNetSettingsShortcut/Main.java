package com.dynamicg.mobNetSettingsShortcut;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

public class Main extends Activity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
//        TextView body = new TextView(this);
//        body.setText("test");
//        setContentView(body);
    	
    	/*
    	 * there is no "post installation" service where we could set up the notification 
    	 * bar, so we leave the main activity in.
    	 * pointless for regular users, but would otherwise require restarting the phone
    	 */
        MobNetSettingsHandler.setupNotification(this);
        Toast.makeText(this, R.string.startupToast, Toast.LENGTH_SHORT).show();
        this.finish();
    }

}