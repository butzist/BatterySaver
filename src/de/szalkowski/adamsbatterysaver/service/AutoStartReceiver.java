package de.szalkowski.adamsbatterysaver.service;

import de.szalkowski.adamsbatterysaver.SettingsManager;
import de.szalkowski.adamsbatterysaver.SettingsProvider;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AutoStartReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED) ||
				intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED) ||
				intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED)) {
			Intent service = new Intent(context, de.szalkowski.adamsbatterysaver.service.MainService.class);
	        SettingsProvider settings = SettingsManager.getSettingsManager(context.getApplicationContext());
			if(settings.getStartService()) {
				context.startService(service);
			}
		}
	}

}
