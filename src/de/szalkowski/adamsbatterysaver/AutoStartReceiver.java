package de.szalkowski.adamsbatterysaver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class AutoStartReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
			Intent service = new Intent(context, de.szalkowski.adamsbatterysaver.MainService.class);
	        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
			if(settings.getBoolean("start_service", true)) {
				context.startService(service);
			}
		}
	}

}
