package de.szalkowski.adamsbatterysaver;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

public class MainService extends Service {
	private final String LOG = "de.szalkowski.adamsbatterysaver.MainService";
	static private boolean screen_on;
	static private boolean power_on;
	static public boolean is_running = false;
	
	BroadcastReceiver powerstate_receiver = null;

	@Override
	public IBinder onBind(Intent intent) {
		Log.w(LOG, "Bind");
		
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(LOG, "Created");
		
		MainService.is_running = true;
		
		SharedPreferences settings = this.getApplicationContext().getSharedPreferences("settings", MODE_PRIVATE);
		
		//SharedPreferences service_status = this.getSharedPreferences("status", MODE_PRIVATE);
		
		IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        this.powerstate_receiver = new PowerStateReceiver();
        this.registerReceiver(this.powerstate_receiver, filter);
        
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        MainService.screen_on = pm.isScreenOn();
		Log.d(LOG, "Screen is " + (MainService.screen_on ? "on" : "off"));
        
        Intent battery_status = this.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int battery_plugged = battery_status.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        MainService.power_on = (battery_plugged == BatteryManager.BATTERY_PLUGGED_USB) || (battery_plugged == BatteryManager.BATTERY_PLUGGED_AC); 
		Log.d(LOG, "Power is " + (MainService.power_on ? "on" : "off"));
	}

	@Override
	public void onDestroy() {
		Log.d(LOG, "Destroyed");
		
		this.unregisterReceiver(this.powerstate_receiver);
		MainService.is_running = false;
		
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(LOG, "Started");
		
		if(intent.hasExtra("power")) {
			if(intent.getBooleanExtra("power", false)) {
				Log.d(LOG, "Power on");
				MainService.power_on = true;
			} else {
				Log.d(LOG, "Power off");
				MainService.power_on = true;
			}
		} else if(intent.hasExtra("screen")) {
			if(intent.getBooleanExtra("screen", false)) {
				Log.d(LOG, "Screen on");
				MainService.screen_on = true;
			} else {
				Log.d(LOG, "Screen off");
				MainService.screen_on = false;
			}
		}
		
		
		//stopSelf();
		return super.onStartCommand(intent, flags, startId);
	}

}
