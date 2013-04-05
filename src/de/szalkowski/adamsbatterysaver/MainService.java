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
	static public final String ACTION_DISABLE = "de.szalkowski.adamsbatterysaver.DISABLE_ACTION";
	
	static public boolean is_running = false;
	static public PowerManager.WakeLock wake_lock = null;
	
	private boolean screen_on;
	private boolean power_on;
	private BroadcastReceiver powerstate_receiver = null;
	private boolean wakeup_timeout_active = false;
	private boolean screen_timeout_active = false;
	private boolean power_timeout_active = false;
	private boolean wakeup_active = false;
	private boolean in_foreground = false;
	private long traffic_at_wakeup = 0;
	private long traffic_at_poweroff = 0;
	private long traffic_at_screenoff = 0;
	private long traffic_since_timeout = 0;
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
        
		WidgetProvider.updateAllWidgets(this);

		//if(!WidgetProvider.hasWidgets(this)) {
        //	toForeground(true);
        //}
        toForeground(true);
        
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
		
		SharedPreferences settings = this.getApplicationContext().getSharedPreferences("settings", MODE_PRIVATE);
		
		this.power_savers = new LinkedList<PowerSaver>();

		WifiPowerSaver wifi = new WifiPowerSaver(this, settings.getInt("wifi_flags", WifiPowerSaver.DEFAULT_FLAGS));
		this.power_savers.add(wifi);
		
		MobileDataPowerSaver data = new MobileDataPowerSaver(this, settings.getInt("data_flags", MobileDataPowerSaver.DEFAULT_FLAGS));
		this.power_savers.add(data);
		
		SyncPowerSaver sync = new SyncPowerSaver(this, settings.getInt("sync_flags", SyncPowerSaver.DEFAULT_FLAGS));
		this.power_savers.add(sync);
		
		BluetoothPowerSaver blue = new BluetoothPowerSaver(this, settings.getInt("blue_flags", BluetoothPowerSaver.DEFAULT_FLAGS));
		this.power_savers.add(blue);
		
		setWakeupTimeout();
	}
	
	protected void toForeground(boolean foreground) {
		if(foreground == this.in_foreground) return;
		
		if(foreground) {
	        Intent activity = new Intent(this.getApplicationContext(),MainActivity.class);
	        PendingIntent pending_activity = PendingIntent.getActivity(this, 0, activity, PendingIntent.FLAG_UPDATE_CURRENT);

	        NotificationCompat.Builder builder =
	                new NotificationCompat.Builder(this)
	                .setSmallIcon(R.drawable.ic_launcher)
	                .setContentTitle("Adam's Battery Saver")
	                .setContentText("click to configure")
	                .setContentIntent(pending_activity);
	    	this.startForeground(42, builder.build());
		} else {
			this.stopForeground(true);		
		}
		
		this.in_foreground = foreground;
	}
	
	protected void updateSettings() {
		SharedPreferences settings = this.getApplicationContext().getSharedPreferences("settings", MODE_PRIVATE);
		
		for(PowerSaver saver : this.power_savers) {
			if(saver.getClass() == WifiPowerSaver.class) {
				saver.setFlags(settings.getInt("wifi_flags", WifiPowerSaver.DEFAULT_FLAGS));
			} else if(saver.getClass() == MobileDataPowerSaver.class) {
				saver.setFlags(settings.getInt("data_flags", MobileDataPowerSaver.DEFAULT_FLAGS));
			} else if(saver.getClass() == SyncPowerSaver.class) {
				saver.setFlags(settings.getInt("sync_flags", SyncPowerSaver.DEFAULT_FLAGS));
			} else if(saver.getClass() == BluetoothPowerSaver.class) {
				saver.setFlags(settings.getInt("blue_flags", BluetoothPowerSaver.DEFAULT_FLAGS));
			}
		}
		
        //if(!WidgetProvider.hasWidgets(this)) {
        //	toForeground(true);
        //} else {
        //	toForeground(false);
        //}
		
		setWakeup(false);
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
		
		SharedPreferences settings = this.getApplicationContext().getSharedPreferences("settings", MODE_PRIVATE);
		this.traffic_at_wakeup = TrafficStats.getTotalRxBytes() + TrafficStats.getTotalTxBytes();

		// set up timeout
		AlarmManager alarm = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
		int interval = settings.getInt(MainActivity.SETTINGS_TIMEOUT, MainActivity.DEFAULT_TIMEOUT)*1000;
		Intent intent = new Intent(MainService.ACTION_WAKEUP_TIMEOUT);
		PendingIntent pending = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		alarm.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime()+interval, pending);
		
		this.wakeup_timeout_active = true;
	}
	
	protected void setScreenTimeout() {
		if(this.screen_timeout_active) {
			cancelScreenTimeout();
		}
		
		SharedPreferences settings = this.getApplicationContext().getSharedPreferences("settings", MODE_PRIVATE);
		this.traffic_at_screenoff = TrafficStats.getTotalRxBytes() + TrafficStats.getTotalTxBytes();

		// set up timeout
		AlarmManager alarm = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
		int interval = settings.getInt(MainActivity.SETTINGS_TIMEOUT, MainActivity.DEFAULT_TIMEOUT)*1000;
		Intent intent = new Intent(MainService.ACTION_SCREEN_TIMEOUT);
		PendingIntent pending = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		alarm.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime()+interval, pending);
		
		this.screen_timeout_active = true;
	}
	
	protected void setPowerTimeout() {
		if(this.power_timeout_active) {
			cancelPowerTimeout();
		}
		
		SharedPreferences settings = this.getApplicationContext().getSharedPreferences("settings", MODE_PRIVATE);
		this.traffic_at_poweroff = TrafficStats.getTotalRxBytes() + TrafficStats.getTotalTxBytes();

		// set up timeout
		AlarmManager alarm = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
		int interval = settings.getInt(MainActivity.SETTINGS_TIMEOUT, MainActivity.DEFAULT_TIMEOUT)*1000;
		Intent intent = new Intent(MainService.ACTION_POWER_TIMEOUT);
		PendingIntent pending = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		alarm.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime()+interval, pending);
		
		this.power_timeout_active = true;
	}
	
	protected void setWakeup(boolean short_interval) {
		if(this.wakeup_active) {
			cancelWakeup();
		}
		
		SharedPreferences settings = this.getApplicationContext().getSharedPreferences("settings", MODE_PRIVATE);
		
		// set up timeout
		AlarmManager alarm = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
		int interval = settings.getInt(MainActivity.SETTINGS_INTERVAL, MainActivity.DEFAULT_INTERVAL) * 60000;
		int first_interval = settings.getInt(MainActivity.SETTINGS_INTERVAL_SHORT, MainActivity.DEFAULT_INTERVAL_SHORT) * 60000;
		if(!short_interval) {
			first_interval = interval;
		}
		
		Intent intent = new Intent(MainService.ACTION_WAKEUP);
		PendingIntent pending = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		alarm.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime()+first_interval, interval, pending);
		
		this.wakeup_active = true;
	}
	
	protected void setMorningWakeup() {
		if(this.wakeup_active) {
			cancelWakeup();
		}
		
		SharedPreferences settings = this.getApplicationContext().getSharedPreferences("settings", MODE_PRIVATE);
		
		// set up timeout
		AlarmManager alarm = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
		int interval = settings.getInt(MainActivity.SETTINGS_INTERVAL, MainActivity.DEFAULT_INTERVAL) * 60000;
		long wakeup_time = getWakeupTime();
		
		Intent intent = new Intent(MainService.ACTION_WAKEUP);
		PendingIntent pending = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, wakeup_time, interval, pending);
		
		this.wakeup_active = true;
	}
	
	protected void stopPowersave() {
		for(PowerSaver power_saver : this.power_savers) {
			if(!power_saver.flagDisableOnIntervalSet())
				continue;
			
			power_saver.stopPowersave();
		}
	}
	
	protected void forceStopPowersave() {
		for(PowerSaver power_saver : this.power_savers) {
			power_saver.stopPowersave();
		}
	}
	
	protected void applyPowersave() {
		SharedPreferences settings = this.getApplicationContext().getSharedPreferences("settings", MODE_PRIVATE);
		Log.v(LOG,"Traffic since timeout: " + this.traffic_since_timeout + " bytes");

		// All timeouts need to update this
		long traffic_limit = settings.getLong(MainActivity.SETTINGS_TRAFFIC_LIMIT, MainActivity.DEFAULT_TRAFFIC_LIMIT);
		
		for(PowerSaver power_saver : this.power_savers) {
			if((power_saver.flagDisableWithPowerSet() && this.power_on) || (power_saver.flagDisableWithScreenSet() && this.screen_on)) {
					power_saver.stopPowersave();
			} else if (power_saver.flagDisabledWhileTrafficSet() && (this.traffic_since_timeout > traffic_limit)) {
				Log.d(LOG,"delaying " + power_saver.getName() + " powersave");
				this.setWakeupTimeout();
			} else {
				power_saver.startPowersave();
			}
		}
	}

	protected boolean isSleepingTime() {
		SharedPreferences settings = this.getApplicationContext().getSharedPreferences("settings", MODE_PRIVATE);
		
		Calendar from = Calendar.getInstance();
		from.set(Calendar.HOUR_OF_DAY, settings.getInt(MainActivity.SETTINGS_NIGHTMODE_FROM_HOUR, MainActivity.DEFAULT_FROM));
		from.set(Calendar.MINUTE, settings.getInt(MainActivity.SETTINGS_NIGHTMODE_FROM_MINUTE, 0));
		from.set(Calendar.SECOND, 0);
		from.set(Calendar.MILLISECOND, 0);
		
		Calendar to = Calendar.getInstance();
		to.set(Calendar.HOUR_OF_DAY, settings.getInt(MainActivity.SETTINGS_NIGHTMODE_TO_HOUR, MainActivity.DEFAULT_TO));
		to.set(Calendar.MINUTE, settings.getInt(MainActivity.SETTINGS_NIGHTMODE_TO_MINUTE, 0));
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
		now.add(Calendar.MINUTE, settings.getInt(MainActivity.SETTINGS_INTERVAL, MainActivity.DEFAULT_INTERVAL));

		return (now.after(from) && now.before(to));
	}
	
	protected long getWakeupTime() {
		SharedPreferences settings = this.getApplicationContext().getSharedPreferences("settings", MODE_PRIVATE);
		
		Calendar from = Calendar.getInstance();
		from.set(Calendar.HOUR_OF_DAY, settings.getInt(MainActivity.SETTINGS_NIGHTMODE_FROM_HOUR, MainActivity.DEFAULT_FROM));
		from.set(Calendar.MINUTE, settings.getInt(MainActivity.SETTINGS_NIGHTMODE_FROM_MINUTE, 0));
		from.set(Calendar.SECOND, 0);
		from.set(Calendar.MILLISECOND, 0);

		Calendar to = Calendar.getInstance();
		to.set(Calendar.HOUR_OF_DAY, settings.getInt(MainActivity.SETTINGS_NIGHTMODE_TO_HOUR, MainActivity.DEFAULT_TO));
		to.set(Calendar.MINUTE, settings.getInt(MainActivity.SETTINGS_NIGHTMODE_TO_MINUTE, 0));
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

		forceStopPowersave();
		
		cancelWakeup();
		cancelWakeupTimeout();
		cancelPowerTimeout();
		cancelScreenTimeout();
		
		this.unregisterReceiver(this.powerstate_receiver);
		MainService.is_running = false;
		
		WidgetProvider.updateAllWidgets(this);
		
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
			return START_STICKY;
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
			} else {
				updateSettings();
			}
			
			this.applyPowersave();
			
		} else if(intent.getAction().equals(MainService.ACTION_WAKEUP_TIMEOUT)) {
			Log.d(LOG, "Timeout");
			this.traffic_since_timeout = TrafficStats.getTotalRxBytes() + TrafficStats.getTotalTxBytes() - this.traffic_at_wakeup;
			
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
			this.traffic_since_timeout = TrafficStats.getTotalRxBytes() + TrafficStats.getTotalTxBytes() - this.traffic_at_screenoff;
			
			this.screen_timeout_active = false;
			this.screen_on = false;

			
			this.applyPowersave();
		} else if(intent.getAction().equals(MainService.ACTION_POWER_TIMEOUT)) {
			Log.d(LOG, "Power timeout");
			this.traffic_since_timeout = TrafficStats.getTotalRxBytes() + TrafficStats.getTotalTxBytes() - this.traffic_at_poweroff;
			
			this.power_timeout_active = false;
			this.power_on = false;

			
			this.applyPowersave();
		} else if(intent.getAction().equals(MainService.ACTION_WAKEUP)) {
			Log.d(LOG, "Wakeup");
			
			this.cancelWakeupTimeout();
			this.stopPowersave();
			this.setWakeupTimeout();
		} else if(intent.getAction().equals(MainService.ACTION_DISABLE)) {
			SharedPreferences settings = this.getApplicationContext().getSharedPreferences("settings", MODE_PRIVATE);
			SharedPreferences.Editor editor = settings.edit();
			editor.putBoolean(MainActivity.SETTINGS_START_SERVICE, false);
			editor.commit();

			stopSelf();
		}
		
		if(MainService.wake_lock.isHeld()) {
			MainService.wake_lock.release();
		}

		return START_STICKY;
	}
}
