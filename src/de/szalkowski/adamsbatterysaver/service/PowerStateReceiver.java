package de.szalkowski.adamsbatterysaver.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class PowerStateReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, MainService.class);
        i.setAction(MainService.ACTION_UPDATE);
        
		if(intent.getAction().equals(Intent.ACTION_POWER_CONNECTED)) {
	        i.putExtra("power", true);
		} else if(intent.getAction().equals(Intent.ACTION_POWER_DISCONNECTED)) {
	        i.putExtra("power", false);
		} else if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
	        i.putExtra("screen", true);
		} else if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
	        i.putExtra("screen", false);
		} else {
			return;
		}
		
		if(MainService.is_running) {
			context.startService(i);
		}
	}

}
