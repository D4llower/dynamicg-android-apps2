package com.dynamicg.mobNetSettingsShortcut;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootCompletedReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(final Context context, final Intent bootintent) {
		//context.startService(IntentHelper.getMobileNetworkSettingsIntent());
		MobNetSettingsHandler.setupNotification(context);
	}
	
}
