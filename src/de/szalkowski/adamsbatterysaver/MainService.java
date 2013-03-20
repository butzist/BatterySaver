package de.szalkowski.adamsbatterysaver;

import java.lang.reflect.Method;
import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.util.Log;

public class MainService extends Service {
	static private final String LOG = "de.szalkowski.adamsbatterysaver.MainService";
	static public final String ACTION_WAKEUP = "de.szalkowski.adamsbatterysaver.WAKEUP_ACTION"; 
	static public final String ACTION_TIMEOUT = "de.szalkowski.adamsbatterysaver.TIMEOUT_ACTION"; 
	static public final String ACTION_UPDATE = "de.szalkowski.adamsbatterysaver.UPDATE_ACTION"; 
	private boolean screen_on;
	private boolean power_on;
	static public boolean is_running = false;
	static public PowerManager.WakeLock wake_lock = null;
	private BroadcastReceiver powerstate_receiver = null;
	private boolean timeout_active = false;
	private boolean wakeup_active = false;

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
		
		//SharedPreferences service_status = this.getSharedPreferences("status", MODE_PRIVATE);
		
		// register power state listener
		IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        this.powerstate_receiver = new PowerStateReceiver();
        this.registerReceiver(this.powerstate_receiver, filter);
        
        // check current power state
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        this.screen_on = pm.isScreenOn();
		Log.d(LOG, "Screen is " + (this.screen_on ? "on" : "off"));
        
        Intent battery_status = this.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int battery_plugged = battery_status.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        this.power_on = (battery_plugged == BatteryManager.BATTERY_PLUGGED_USB) || (battery_plugged == BatteryManager.BATTERY_PLUGGED_AC); 
		Log.d(LOG, "Power is " + (this.power_on ? "on" : "off"));
		
		// set up wake lock
		if(MainService.wake_lock == null) {
			MainService.wake_lock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Alarm received");
		}

	}
	
	protected void cancelTimeout() {
		AlarmManager alarm = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);

		Intent intent2 = new Intent(MainService.ACTION_TIMEOUT);
		PendingIntent pending2 = PendingIntent.getBroadcast(getApplicationContext(), 0, intent2, PendingIntent.FLAG_UPDATE_CURRENT);
		alarm.cancel(pending2);

		this.timeout_active = false;
	}
	
	protected void cancelWakeup() {
		AlarmManager alarm = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);

		Intent intent1 = new Intent(MainService.ACTION_WAKEUP);
		PendingIntent pending1 = PendingIntent.getBroadcast(getApplicationContext(), 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
		alarm.cancel(pending1);
		
		this.wakeup_active = false;
	}
	
	protected void setTimeout() {
		if(this.timeout_active) {
			cancelTimeout();
		}
		
		// set up timeout
		AlarmManager alarm = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
		int interval = 60000;
		Intent intent = new Intent(MainService.ACTION_TIMEOUT);
		PendingIntent pending = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		alarm.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime()+interval, pending);
		
		this.timeout_active = true;
	}
	
	protected void setWakeup() {
		if(this.wakeup_active) return;
		
		SharedPreferences settings = this.getApplicationContext().getSharedPreferences("settings", MODE_PRIVATE);
		
		// set up timeout
		AlarmManager alarm = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
		int interval = settings.getInt("minutes", 15) * 60000;
		Intent intent = new Intent(MainService.ACTION_WAKEUP);
		PendingIntent pending = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		alarm.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime()+interval, interval, pending);
		
		this.wakeup_active = true;
	}
	
	protected void setMorningWakeup() {
		if(this.wakeup_active) return;
		
		SharedPreferences settings = this.getApplicationContext().getSharedPreferences("settings", MODE_PRIVATE);
		
		// set up timeout
		AlarmManager alarm = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
		int interval = settings.getInt("minutes", 15) * 60000;
		
		Calendar from = Calendar.getInstance();
		from.set(Calendar.HOUR_OF_DAY, settings.getInt("from_hour", 22));
		from.set(Calendar.MINUTE, settings.getInt("from_minute", 0));
		from.set(Calendar.SECOND, 0);
		from.set(Calendar.MILLISECOND, 0);

		Calendar to = Calendar.getInstance();
		to.set(Calendar.HOUR_OF_DAY, settings.getInt("to_hour", 8));
		to.set(Calendar.MINUTE, settings.getInt("to_minute", 0));
		to.set(Calendar.SECOND, 0);
		to.set(Calendar.MILLISECOND, 0);

		if(from.after(to)) {
			to.add(Calendar.DATE, 1);				
		}

		long time = to.getTimeInMillis();
		Intent intent = new Intent(MainService.ACTION_WAKEUP);
		PendingIntent pending = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, time, interval, pending);
		
		this.wakeup_active = true;
	}
	
	protected void enableNetwork() {
		Log.d(LOG, "enable network");

		SharedPreferences settings = this.getApplicationContext().getSharedPreferences("settings", MODE_PRIVATE);
		
		WifiManager wifi = (WifiManager)this.getSystemService(Context.WIFI_SERVICE);
		if(settings.getBoolean("wifi", true) && !wifi.isWifiEnabled()) {
			wifi.setWifiEnabled(true);
		}
		
		if(settings.getBoolean("data", true) && !isMobileDataEnabled()) {
			setMobileDataEnabled(true);
		}
		
		if(settings.getBoolean("sync", true)) {
			ContentResolver.setMasterSyncAutomatically(true);
		}
	}
	
	protected void disableNetwork() {
		Log.d(LOG, "disable network");

		SharedPreferences settings = this.getApplicationContext().getSharedPreferences("settings", MODE_PRIVATE);
		
		if(settings.getBoolean("sync", true)) {
			ContentResolver.setMasterSyncAutomatically(false);
		}
		
		WifiManager wifi = (WifiManager)this.getSystemService(Context.WIFI_SERVICE);
		if(settings.getBoolean("wifi", true) && wifi.isWifiEnabled()) {
			wifi.setWifiEnabled(false);
		}
		
		if(settings.getBoolean("data", true) && isMobileDataEnabled()) {
			setMobileDataEnabled(false);
		}
	}

	protected void saveNetworkStatus() {
		Log.d(LOG, "save network status");

		SharedPreferences settings = this.getApplicationContext().getSharedPreferences("settings", MODE_PRIVATE);
		SharedPreferences.Editor edit = settings.edit();
		
		edit.putBoolean("sync", ContentResolver.getMasterSyncAutomatically());

		WifiManager wifi = (WifiManager)this.getSystemService(Context.WIFI_SERVICE);
		edit.putBoolean("wifi", wifi.isWifiEnabled());
		
		edit.putBoolean("data", isMobileDataEnabled());
		
		edit.commit();		
	}

	protected boolean isMobileDataEnabled() {
		TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
		return telephonyManager.getDataState() != TelephonyManager.DATA_DISCONNECTED;
	}
	
	protected void setMobileDataEnabled(boolean enabled) {
		Log.d(LOG, "setMobileDataEnabled " + enabled);
		// BASED ON http://stackoverflow.com/questions/3644144/how-to-disable-mobile-data-on-android
		
		try {
		    ConnectivityManager conman = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
		    Method setmeth = conman.getClass().getMethod("setMobileDataEnabled", boolean.class);
		    setmeth.invoke(conman, enabled);
		}
		catch(Exception e) {
			Log.w(LOG, "setMobileDataEnabled failed -- trying alternative method");
			
			try {    
				Method dataConnSwitchmethod;
				Class<?> telephonyManagerClass;
				Object ITelephonyStub;
				Class<?> ITelephonyClass;
				
				  TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);

				    telephonyManagerClass = Class.forName(telephonyManager.getClass().getName());
				    Method getITelephonyMethod = telephonyManagerClass.getDeclaredMethod("getITelephony");
				    getITelephonyMethod.setAccessible(true);
				    ITelephonyStub = getITelephonyMethod.invoke(telephonyManager);
				    ITelephonyClass = Class.forName(ITelephonyStub.getClass().getName());

				    if (enabled) {
				        dataConnSwitchmethod = ITelephonyClass
				                .getDeclaredMethod("enableDataConnectivity");
				    } else {
				        dataConnSwitchmethod = ITelephonyClass
				                .getDeclaredMethod("disableDataConnectivity");   
				    }
				    dataConnSwitchmethod.setAccessible(true);
				    dataConnSwitchmethod.invoke(ITelephonyStub);
			}
			catch(Exception ee) {
				Log.e(LOG, "setMobileDataEnabled failed");
			}
		}
	}

	protected boolean isSleepingTime() {
		Log.d(LOG, "sleeping time?");

		SharedPreferences settings = this.getApplicationContext().getSharedPreferences("settings", MODE_PRIVATE);
		
		Calendar from = Calendar.getInstance();
		from.set(Calendar.HOUR_OF_DAY, settings.getInt("from_hour", 22));
		from.set(Calendar.MINUTE, settings.getInt("from_minute", 0));
		from.set(Calendar.SECOND, 0);
		from.set(Calendar.MILLISECOND, 0);

		Calendar to = Calendar.getInstance();
		to.set(Calendar.HOUR_OF_DAY, settings.getInt("to_hour", 8));
		to.set(Calendar.MINUTE, settings.getInt("to_minute", 0));
		to.set(Calendar.SECOND, 0);
		to.set(Calendar.MILLISECOND, 0);

		if(from.after(to)) {
			to.add(Calendar.DATE, 1);				
		}
		
		Calendar now = Calendar.getInstance();

		return (now.after(from) && now.before(to));
	}
	
	@Override
	public void onDestroy() {
		Log.d(LOG, "Destroyed");

		enableNetwork();
		
		this.unregisterReceiver(this.powerstate_receiver);
		MainService.is_running = false;
		
		if(MainService.wake_lock.isHeld()) {
			MainService.wake_lock.release();
		}
		MainService.wake_lock = null;

		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(LOG, "Started");
		
		if(intent == null) {
			return super.onStartCommand(intent, flags, startId);
		}
		
		if(intent.hasExtra("power")) {
			if(intent.getBooleanExtra("power", false)) {
				Log.d(LOG, "Power on");
				this.power_on = true;
			} else {
				Log.d(LOG, "Power off");
				this.power_on = false;
			}
		} else if(intent.hasExtra("screen")) {
			if(intent.getBooleanExtra("screen", false)) {
				Log.d(LOG, "Screen on");
				this.screen_on = true;
			} else {
				Log.d(LOG, "Screen off");
				this.screen_on = false;
			}
			
		} else if(intent.hasExtra("timeout")) {
			Log.d(LOG, "Timeout");
			this.timeout_active = false;
			
			disableNetwork();
			if(isSleepingTime()) {
				Log.d(LOG, "Sleeping time!");
				setMorningWakeup();
			} else {
				setWakeup();
			}

			if(MainService.wake_lock.isHeld()) {
				MainService.wake_lock.release();
			}
			
			return super.onStartCommand(intent, flags, startId);
		} else if(intent.hasExtra("wakeup")) {
			Log.d(LOG, "Wakeup");
			
			if(isSleepingTime()) {
				Log.d(LOG, "Sleeping time!");

				cancelWakeup();
				setMorningWakeup();
				
				if(MainService.wake_lock.isHeld()) {
					MainService.wake_lock.release();
				}
			} else {
				enableNetwork();
				setTimeout();
			}
			
			return super.onStartCommand(intent, flags, startId);
		}
		
		if(this.screen_on || this.power_on) {
			cancelTimeout();
			cancelWakeup();
			enableNetwork();

			if(MainService.wake_lock.isHeld()) {
				MainService.wake_lock.release();
			}
		} else if(!this.screen_on && !this.power_on) {
			saveNetworkStatus();
			setTimeout();				
		}
		
		//stopSelf();
		return super.onStartCommand(intent, flags, startId);
	}

}
