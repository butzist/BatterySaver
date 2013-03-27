package de.szalkowski.adamsbatterysaver;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.TrafficStats;
import android.os.BatteryManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class MainService extends Service {
	static private final String LOG = "de.szalkowski.adamsbatterysaver.MainService";
	static public final String ACTION_WAKEUP = "de.szalkowski.adamsbatterysaver.WAKEUP_ACTION"; 
	static public final String ACTION_WAKEUP_TIMEOUT = "de.szalkowski.adamsbatterysaver.WAKEUP_TIMEOUT_ACTION"; 
	static public final String ACTION_SCREEN_TIMEOUT = "de.szalkowski.adamsbatterysaver.SCREEN_TIMEOUT_ACTION"; 
	static public final String ACTION_POWER_TIMEOUT = "de.szalkowski.adamsbatterysaver.POWER_TIMEOUT_ACTION"; 
	static public final String ACTION_UPDATE = "de.szalkowski.adamsbatterysaver.UPDATE_ACTION"; 
	
	static public boolean is_running = false;
	static public PowerManager.WakeLock wake_lock = null;
	
	private boolean screen_on;
	private boolean power_on;
	private BroadcastReceiver powerstate_receiver = null;
	private boolean wakeup_timeout_active = false;
	private boolean screen_timeout_active = false;
	private boolean power_timeout_active = false;
	private boolean wakeup_active = false;
	private long traffic = 0;
	private List<PowerSaver> power_savers;

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
		
		// register power state listener
		IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        this.powerstate_receiver = new PowerStateReceiver();
        this.registerReceiver(this.powerstate_receiver, filter);
        
        Intent activity = new Intent(this.getApplicationContext(),MainActivity.class);
        PendingIntent pending_activity = PendingIntent.getActivity(this, 0, activity, PendingIntent.FLAG_UPDATE_CURRENT);
        
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("Adam's Battery Saver")
                .setContentText("click to configure")
                .setContentIntent(pending_activity);
        
        startForeground(42, builder.build());
        
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
		
		this.power_savers = new LinkedList<PowerSaver>();

		int flags = PowerSaver.FLAG_ENABLE_ON_INTERVAL + PowerSaver.FLAG_ENABLE_WITH_POWER + PowerSaver.FLAG_ENABLE_WITH_SCREEN + PowerSaver.FLAG_SAVE_STATE;
		
		WifiPowerSaver wifi = new WifiPowerSaver(this, flags);
		this.power_savers.add(wifi);
		
		MobileDataPowerSaver data = new MobileDataPowerSaver(this, flags);
		this.power_savers.add(data);
		
		SyncPowerSaver sync = new SyncPowerSaver(this, PowerSaver.FLAG_ENABLE_ON_INTERVAL + PowerSaver.FLAG_ENABLE_WITH_POWER + PowerSaver.FLAG_ENABLE_WITH_SCREEN);
		this.power_savers.add(sync);
		
		BluetoothPowerSaver blue = new BluetoothPowerSaver(this, PowerSaver.FLAG_ENABLE_WITH_POWER);
		this.power_savers.add(blue);
		
		setWakeupTimeout();
	}
	
	protected void cancelWakeupTimeout() {
		if(!this.wakeup_timeout_active) return;
		
		AlarmManager alarm = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);

		Intent intent = new Intent(MainService.ACTION_WAKEUP_TIMEOUT);
		PendingIntent pending = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		alarm.cancel(pending);

		this.wakeup_timeout_active = false;
	}
	
	protected void cancelScreenTimeout() {
		if(!this.screen_timeout_active) return;
		
		AlarmManager alarm = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);

		Intent intent = new Intent(MainService.ACTION_SCREEN_TIMEOUT);
		PendingIntent pending = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		alarm.cancel(pending);

		this.screen_timeout_active = false;
	}
	
	protected void cancelPowerTimeout() {
		if(!this.power_timeout_active) return;
		
		AlarmManager alarm = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);

		Intent intent = new Intent(MainService.ACTION_POWER_TIMEOUT);
		PendingIntent pending = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		alarm.cancel(pending);

		this.power_timeout_active = false;
	}
	
	protected void cancelWakeup() {
		if(!this.wakeup_active) return;
		
		AlarmManager alarm = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);

		Intent intent = new Intent(MainService.ACTION_WAKEUP);
		PendingIntent pending = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		alarm.cancel(pending);
		
		this.wakeup_active = false;
	}
	
	protected void setWakeupTimeout() {
		if(this.wakeup_timeout_active) {
			cancelWakeupTimeout();
		}
		
		// set up timeout
		AlarmManager alarm = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
		int interval = MainActivity.DEFAULT_TIMEOUT*1000;
		Intent intent = new Intent(MainService.ACTION_WAKEUP_TIMEOUT);
		PendingIntent pending = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		alarm.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime()+interval, pending);
		
		this.wakeup_timeout_active = true;
	}
	
	protected void setScreenTimeout() {
		if(this.screen_timeout_active) {
			cancelScreenTimeout();
		}
		
		// set up timeout
		AlarmManager alarm = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
		int interval = MainActivity.DEFAULT_TIMEOUT*1000;
		Intent intent = new Intent(MainService.ACTION_SCREEN_TIMEOUT);
		PendingIntent pending = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		alarm.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime()+interval, pending);
		
		this.screen_timeout_active = true;
	}
	
	protected void setPowerTimeout() {
		if(this.power_timeout_active) {
			cancelPowerTimeout();
		}
		
		// set up timeout
		AlarmManager alarm = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
		int interval = MainActivity.DEFAULT_TIMEOUT*1000;
		Intent intent = new Intent(MainService.ACTION_POWER_TIMEOUT);
		PendingIntent pending = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		alarm.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime()+interval, pending);
		
		this.power_timeout_active = true;
	}

	protected long getTrafficSinceWakeup() {
		long traffic = TrafficStats.getTotalRxBytes() + TrafficStats.getTotalTxBytes();
		
		return traffic - this.traffic;
	}
	
	protected void setWakeup(boolean short_interval) {
		if(this.wakeup_active) {
			cancelWakeup();
		}
		
		SharedPreferences settings = this.getApplicationContext().getSharedPreferences("settings", MODE_PRIVATE);
		
		// set up timeout
		AlarmManager alarm = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
		int interval = settings.getInt("minutes", MainActivity.DEFAULT_MINUTES) * 60000;
		int first_interval = settings.getInt("minutes", MainActivity.DEFAULT_MINUTES_SHORT) * 60000;
		if(!short_interval) {
			first_interval = interval;
		}
		
		Intent intent = new Intent(MainService.ACTION_WAKEUP);
		PendingIntent pending = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		alarm.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime()+first_interval, interval, pending);
		
		this.wakeup_active = true;
	}
	
	protected void setMorningWakeup() {
		if(this.wakeup_active) {
			cancelWakeup();
		}
		
		SharedPreferences settings = this.getApplicationContext().getSharedPreferences("settings", MODE_PRIVATE);
		
		// set up timeout
		AlarmManager alarm = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
		int interval = settings.getInt("minutes", MainActivity.DEFAULT_MINUTES) * 60000;
		long wakeup_time = getWakeupTime();
		
		Intent intent = new Intent(MainService.ACTION_WAKEUP);
		PendingIntent pending = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, wakeup_time, interval, pending);
		
		this.wakeup_active = true;
	}
	
	protected void stopPowersave(boolean isInterval) {
		for(PowerSaver power_saver : this.power_savers) {
			if(!isInterval || power_saver.flagEnableOnIntervalSet()) {
				power_saver.stopPowersave();
			}
		}
	}
	
	protected void applyPowersave() {
		for(PowerSaver power_saver : this.power_savers) {
			if(power_saver.flagEnableWithPowerSet() && this.power_on) {
				power_saver.stopPowersave();
			} else if(power_saver.flagEnableWithScreenSet() && this.screen_on) {
				power_saver.stopPowersave();
			} else {
				power_saver.startPowersave();
			}
		}
	}

	protected boolean isSleepingTime() {
		SharedPreferences settings = this.getApplicationContext().getSharedPreferences("settings", MODE_PRIVATE);
		
		Calendar from = Calendar.getInstance();
		from.set(Calendar.HOUR_OF_DAY, settings.getInt("from_hour", MainActivity.DEFAULT_FROM));
		from.set(Calendar.MINUTE, settings.getInt("from_minute", 0));
		from.set(Calendar.SECOND, 0);
		from.set(Calendar.MILLISECOND, 0);
		
		Calendar to = Calendar.getInstance();
		to.set(Calendar.HOUR_OF_DAY, settings.getInt("to_hour", MainActivity.DEFAULT_TO));
		to.set(Calendar.MINUTE, settings.getInt("to_minute", 0));
		to.set(Calendar.SECOND, 0);
		to.set(Calendar.MILLISECOND, 0);

		
		Calendar now = Calendar.getInstance();
		if(from.after(to)) {
			if(now.after(to)) {
				to.add(Calendar.DATE, 1);
			} else {
				from.add(Calendar.DATE, -1);				
			}
		}

		// add interval time so that first wakeup can not fall into night time
		now.add(Calendar.MINUTE, settings.getInt("minutes", MainActivity.DEFAULT_MINUTES));

		return (now.after(from) && now.before(to));
	}
	
	protected long getWakeupTime() {
		SharedPreferences settings = this.getApplicationContext().getSharedPreferences("settings", MODE_PRIVATE);
		
		Calendar from = Calendar.getInstance();
		from.set(Calendar.HOUR_OF_DAY, settings.getInt("from_hour", MainActivity.DEFAULT_FROM));
		from.set(Calendar.MINUTE, settings.getInt("from_minute", 0));
		from.set(Calendar.SECOND, 0);
		from.set(Calendar.MILLISECOND, 0);

		Calendar to = Calendar.getInstance();
		to.set(Calendar.HOUR_OF_DAY, settings.getInt("to_hour", MainActivity.DEFAULT_TO));
		to.set(Calendar.MINUTE, settings.getInt("to_minute", 0));
		to.set(Calendar.SECOND, 0);
		to.set(Calendar.MILLISECOND, 0);

		
		Calendar now = Calendar.getInstance();
		if(from.after(to)) {
			if(now.after(to)) {
				to.add(Calendar.DATE, 1);
			} else {
				from.add(Calendar.DATE, -1);				
			}
		}

		return to.getTimeInMillis();
	}
	
	@Override
	public void onDestroy() {
		Log.d(LOG, "Destroyed");

		stopPowersave(false);
		
		cancelWakeup();
		cancelWakeupTimeout();
		cancelPowerTimeout();
		cancelScreenTimeout();
		
		this.unregisterReceiver(this.powerstate_receiver);
		MainService.is_running = false;
		
		if(MainService.wake_lock.isHeld()) {
			MainService.wake_lock.release();
		}
		MainService.wake_lock = null;

		stopForeground(true);

		super.onDestroy();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if(intent == null || intent.getAction() == null) {
			return super.onStartCommand(intent, flags, startId);
		}
		
		if(intent.getAction().equals(MainService.ACTION_UPDATE)) {
			if(intent.hasExtra("power")) {
				this.cancelPowerTimeout();

				if(intent.getBooleanExtra("power", false)) {
					Log.d(LOG, "Power on");
					this.power_on = true;
				} else {
					Log.d(LOG, "Power off");
					this.setPowerTimeout();
				}
			} else if(intent.hasExtra("screen")) {
				this.cancelScreenTimeout();

				if(intent.getBooleanExtra("screen", false)) {
					Log.d(LOG, "Screen on");
					this.screen_on = true;
				} else {
					Log.d(LOG, "Screen off");
					this.setScreenTimeout();
					this.setWakeup(true);
				}
			}
			
			this.applyPowersave();
			
		} else if(intent.getAction().equals(MainService.ACTION_WAKEUP_TIMEOUT)) {
			Log.d(LOG, "Timeout");
			Log.v(LOG, "Traffic: " + getTrafficSinceWakeup() + " bytes");
			
			this.wakeup_timeout_active = false;
			this.applyPowersave();
			
			if(isSleepingTime()) {
				Log.d(LOG, "Sleeping time!");
				setMorningWakeup();
			} else if(!this.wakeup_active) {
				setWakeup(false);
			}
		} else if(intent.getAction().equals(MainService.ACTION_SCREEN_TIMEOUT)) {
			Log.d(LOG, "Screen timeout");
			
			this.screen_timeout_active = false;
			this.screen_on = false;
			
			this.applyPowersave();
		} else if(intent.getAction().equals(MainService.ACTION_POWER_TIMEOUT)) {
			Log.d(LOG, "Power timeout");
			
			this.power_timeout_active = false;
			this.power_on = false;
			
			this.applyPowersave();
		} else if(intent.getAction().equals(MainService.ACTION_WAKEUP)) {
			Log.d(LOG, "Wakeup");
			
			this.traffic = TrafficStats.getTotalRxBytes() + TrafficStats.getTotalTxBytes(); 
			
			this.cancelWakeupTimeout();
			this.stopPowersave(true);
			this.setWakeupTimeout();
		}
		
		if(MainService.wake_lock.isHeld()) {
			MainService.wake_lock.release();
		}

		return super.onStartCommand(intent, flags, startId);
	}
}
