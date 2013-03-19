package de.szalkowski.adamsbatterysaver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AutoStartReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
			Intent service = new Intent(context, de.szalkowski.adamsbatterysaver.MainService.class);
			context.startService(service);
		}
	}

}
