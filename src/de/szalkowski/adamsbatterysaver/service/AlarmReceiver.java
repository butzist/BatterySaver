package de.szalkowski.adamsbatterysaver.service;

import de.szalkowski.adamsbatterysaver.Logger;
import de.szalkowski.adamsbatterysaver.SettingsManager;
import de.szalkowski.adamsbatterysaver.SettingsProvider;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {	
	@SuppressLint("Wakelock")
	@Override
	public void onReceive(Context context, Intent intent) {
		Intent service = new Intent(context,MainService.class);

		if(!MainService.is_running) {
			Logger.error("unexpected timer event - service terminated?");
	        SettingsProvider settings = SettingsManager.getSettingsManager(context.getApplicationContext());
			if(settings.getStartService()) {
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
