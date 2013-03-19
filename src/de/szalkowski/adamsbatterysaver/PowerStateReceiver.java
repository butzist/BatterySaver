package de.szalkowski.adamsbatterysaver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class PowerStateReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equals(Intent.ACTION_POWER_CONNECTED)) {
	        Intent i = new Intent(context, MainService.class);
	        i.putExtra("power", true);
	        context.startService(i);
		} else if(intent.getAction().equals(Intent.ACTION_POWER_DISCONNECTED)) {
	        Intent i = new Intent(context, MainService.class);
	        i.putExtra("power", false);
	        context.startService(i);
		} else if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
	        Intent i = new Intent(context, MainService.class);
	        i.putExtra("screen", true);
	        context.startService(i);
		} else if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
	        Intent i = new Intent(context, MainService.class);
	        i.putExtra("screen", false);
	        context.startService(i);
		}
	}

}
