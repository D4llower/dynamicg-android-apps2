package com.dynamicg.mobNetSettingsShortcut;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

public class MobNetSettingsHandler {

	final static int notificationId = 1;
	
	public static void setupNotification(Context context) {
		
		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);	
		
		final int icon3G = R.drawable.stat_sys_data_inandout_3g_25x25;
		final int iconEmpty = R.drawable.ic_placeholder;
		
		final CharSequence tickerText = context.getString(R.string.app_name);
		final CharSequence contentTitle = context.getString(R.string.statusPanelTitle);
		final CharSequence contentText = context.getString(R.string.statusPanelHint);
		
		Notification notification = new Notification(icon3G, tickerText, 0);
		notification.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
		notification.when = 0;
		
		Intent targetIntent = getMobileNetworkSettingsIntent();
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, targetIntent, 0);
		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		
		/*
		 * to get rid of the icon, we do it twice (first call will fill the panel, second call will reset the status bar icon)
		 */
		mNotificationManager.notify(notificationId, notification);
		notification.icon = iconEmpty;
		mNotificationManager.notify(notificationId, notification);
		
	}

	public static Intent getMobileNetworkSettingsIntent() {
    	Intent intent=new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS); 
    	ComponentName cName = new ComponentName("com.android.phone","com.android.phone.Settings"); 
    	intent.setComponent(cName);
    	return intent;
	}
	
    public static void openMobileNetworkSettings(Context context) {
    	context.startActivity ( getMobileNetworkSettingsIntent() );
    }
    
}
