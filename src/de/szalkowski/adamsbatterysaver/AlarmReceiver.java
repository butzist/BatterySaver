package de.szalkowski.adamsbatterysaver;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {
	static private final String LOG = "de.szalkowski.adamsbatterysaver.AlarmReceiver";
	
	@SuppressLint("Wakelock")
	@Override
	public void onReceive(Context context, Intent intent) {
		Intent service = new Intent(context,MainService.class);

		if(!MainService.is_running) {
			Log.e(LOG, "unexpected timer event - service terminated?");
	        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
			if(settings.getBoolean(MainActivity.SETTINGS_START_SERVICE, true)) {
				context.startService(service);
			}
		}
		
		if(MainService.wake_lock != null && !MainService.wake_lock.isHeld()) {
			MainService.wake_lock.acquire();
		}

		if(intent.getAction().equals(MainService.ACTION_WAKEUP_TIMEOUT)) {
			service.setAction(MainService.ACTION_WAKEUP_TIMEOUT);
		} else if(intent.getAction().equals(MainService.ACTION_POWER_TIMEOUT)) {
			service.setAction(MainService.ACTION_POWER_TIMEOUT);
		} else if(intent.getAction().equals(MainService.ACTION_SCREEN_TIMEOUT)) {
			service.setAction(MainService.ACTION_SCREEN_TIMEOUT);
		} else if(intent.getAction().equals(MainService.ACTION_WAKEUP)) {
			service.setAction(MainService.ACTION_WAKEUP);
		}
		
		context.startService(service);
	}

}
