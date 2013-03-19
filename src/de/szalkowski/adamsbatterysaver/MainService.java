package de.szalkowski.adamsbatterysaver;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class MainService extends Service {
	private final String LOG = "de.szalkowski.adamsbatterysaver.MainService";

	@Override
	public IBinder onBind(Intent intent) {
		Log.w(LOG, "Bind");
		
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.w(LOG, "Created");
		
		/*
		IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        BroadcastReceiver receiver = new PowerStateReceiver();
        registerReceiver(receiver, filter);
        */
	}

	@Override
	public void onDestroy() {
		Log.w(LOG, "Destroyed");
		
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.w(LOG, "Started");
		
		if(intent.hasExtra("power")) {
			if(intent.getBooleanExtra("power", false)) {
				Log.w(LOG, "Power on");
				
			} else {
				Log.w(LOG, "Power off");
				
			}
		} else if(intent.hasExtra("screen")) {
			if(intent.getBooleanExtra("screen", false)) {
				Log.w(LOG, "Screen on");
				
			} else {
				Log.w(LOG, "Screen off");
				
			}
		}
		
		
		stopSelf();
		return super.onStartCommand(intent, flags, startId);
	}

}
