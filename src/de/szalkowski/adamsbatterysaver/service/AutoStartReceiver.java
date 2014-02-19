package de.szalkowski.adamsbatterysaver.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import de.szalkowski.adamsbatterysaver.AdamsBatterySaverApplication;
import de.szalkowski.adamsbatterysaver.SettingsProvider;

public class AutoStartReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED) ||
				intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED) ||
				intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED)) {
			Intent service = new Intent(context, de.szalkowski.adamsbatterysaver.service.MainService.class);
	        SettingsProvider settings = AdamsBatterySaverApplication.getSettings();
			if(settings.getStartService()) {
				context.startService(service);
			}
		}
	}

}
