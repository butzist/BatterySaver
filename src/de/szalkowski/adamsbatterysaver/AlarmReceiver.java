package de.szalkowski.adamsbatterysaver;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

public class AlarmReceiver extends BroadcastReceiver {

	@SuppressLint("Wakelock")
	@Override
	public void onReceive(Context context, Intent intent) {
		if(MainService.wake_lock == null) {
	        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
			MainService.wake_lock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Alarm received");
		}
		
		if(!MainService.wake_lock.isHeld()) {
			MainService.wake_lock.acquire();
		}

		Intent service = new Intent(context,MainService.class);
		if(intent.getAction().equals(MainService.ACTION_TIMEOUT)) {
			service.setAction(MainService.ACTION_TIMEOUT);
			
		} else if(intent.getAction().equals(MainService.ACTION_WAKEUP)) {
			service.setAction(MainService.ACTION_WAKEUP);
		}
		
		context.startService(service);
	}

}
