package de.szalkowski.adamsbatterysaver;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {

	@SuppressLint("Wakelock")
	@Override
	public void onReceive(Context context, Intent intent) {
		if(!MainService.wake_lock.isHeld()) {
			MainService.wake_lock.acquire();
		}

		Intent service = new Intent(context,MainService.class);
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
