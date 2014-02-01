package de.szalkowski.adamsbatterysaver.service;

import java.util.Calendar;
import java.util.Collection;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import de.szalkowski.adamsbatterysaver.AdamsBatterySaverApplication;
import de.szalkowski.adamsbatterysaver.Logger;
import de.szalkowski.adamsbatterysaver.R;
import de.szalkowski.adamsbatterysaver.SettingsProvider;
import de.szalkowski.adamsbatterysaver.devices.PowerSaver;
import de.szalkowski.adamsbatterysaver.ui.MainActivity;

public class MainService extends Service {
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
	private Collection<PowerSaver> power_savers;
	private SettingsProvider settings;

	@Override
	public IBinder onBind(Intent intent) {
		Logger.warning("Bind");
		
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Logger.debug("Created");
		
		MainService.is_running = true;
		
		// register power state listener
		IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        this.powerstate_receiver = new PowerStateReceiver();
        this.registerReceiver(this.powerstate_receiver, filter);
        
        toForeground(true);

        // check current power state
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        this.screen_on = pm.isScreenOn();
		Logger.debug("Screen is " + (this.screen_on ? "on" : "off"));
        
        Intent battery_status = this.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int battery_plugged = battery_status.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        this.power_on = (battery_plugged == BatteryManager.BATTERY_PLUGGED_USB) || (battery_plugged == BatteryManager.BATTERY_PLUGGED_AC); 
		Logger.debug("Power is " + (this.power_on ? "on" : "off"));
		
		// set up wake lock
		if(MainService.wake_lock == null) {
			MainService.wake_lock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Alarm received");
		}
		
		settings = AdamsBatterySaverApplication.getSettings();

		this.power_savers = AdamsBatterySaverApplication.getPowersavers().getPowerSavers();
		
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
	                .setContentTitle(this.getString(R.string.app_name))
	                .setContentText(this.getString(R.string.click_to_configure))
	                .setContentIntent(pending_activity);
	    	this.startForeground(42, builder.build());
		} else {
			this.stopForeground(true);		
		}
		
		this.in_foreground = foreground;
	}
	
	protected void updateSettings() {		
		for(PowerSaver saver : this.power_savers) {
			//saver.updateSettings(); FIXME
		}
		
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

		// set up timeout
		AlarmManager alarm = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
		int interval = settings.getTimeout()*1000;
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
		int interval = settings.getTimeout()*1000;
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
		int interval = settings.getTimeout()*1000;
		Intent intent = new Intent(MainService.ACTION_POWER_TIMEOUT);
		PendingIntent pending = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		alarm.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime()+interval, pending);
		
		this.power_timeout_active = true;
	}
	
	protected void setWakeup(boolean short_interval) {
		if(this.wakeup_active) {
			cancelWakeup();
		}
		
		// set up timeout
		AlarmManager alarm = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
		int interval = settings.getInterval() * 60000;
		int first_interval = settings.getShortInterval() * 60000;
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
		
		// set up timeout
		AlarmManager alarm = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
		int interval = settings.getInterval() * 60000;
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
		// All timeouts need to update this
		for(PowerSaver power_saver : this.power_savers) {
			if((power_saver.flagDisableWithPowerSet() && this.power_on) || (power_saver.flagDisableWithScreenSet() && this.screen_on)) {
					power_saver.stopPowersave();
			} else if (power_saver.flagDisabledWhileTrafficSet() && power_saver.hasTraffic()) {
				Logger.debug("delaying " + power_saver.getName() + " powersave");
				this.setWakeupTimeout();
			} else {
				power_saver.startPowersave();
			}
		}
	}

	protected boolean isSleepingTime() {
		Calendar from = Calendar.getInstance();
		from.set(Calendar.HOUR_OF_DAY, settings.getNightModeFromHour());
		from.set(Calendar.MINUTE, settings.getNightModeFromMinute());
		from.set(Calendar.SECOND, 0);
		from.set(Calendar.MILLISECOND, 0);
		
		Calendar to = Calendar.getInstance();
		to.set(Calendar.HOUR_OF_DAY, settings.getNightModeToHour());
		to.set(Calendar.MINUTE, settings.getNightModeToMinute());
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
		now.add(Calendar.MINUTE, settings.getInterval());

		return (now.after(from) && now.before(to));
	}
	
	protected long getWakeupTime() {
		Calendar from = Calendar.getInstance();
		from.set(Calendar.HOUR_OF_DAY, settings.getNightModeFromHour());
		from.set(Calendar.MINUTE, settings.getNightModeFromMinute());
		from.set(Calendar.SECOND, 0);
		from.set(Calendar.MILLISECOND, 0);

		Calendar to = Calendar.getInstance();
		to.set(Calendar.HOUR_OF_DAY, settings.getNightModeToHour());
		to.set(Calendar.MINUTE, settings.getNightModeToMinute());
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
		Logger.debug("Destroyed");

		forceStopPowersave();
		
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
			return START_STICKY;
		}
		
		if(intent.getAction().equals(MainService.ACTION_UPDATE)) {
			if(intent.hasExtra("power")) {
				this.cancelPowerTimeout();

				if(intent.getBooleanExtra("power", false)) {
					Logger.debug("Power on");
					this.power_on = true;
				} else {
					Logger.debug("Power off");
					this.setPowerTimeout();
				}
			} else if(intent.hasExtra("screen")) {
				this.cancelScreenTimeout();

				if(intent.getBooleanExtra("screen", false)) {
					Logger.debug("Screen on");
					this.screen_on = true;
				} else {
					Logger.debug("Screen off");
					this.setScreenTimeout();
					this.setWakeup(true);
				}
			} else {
				updateSettings();
			}
			
			this.applyPowersave();
			
		} else if(intent.getAction().equals(MainService.ACTION_WAKEUP_TIMEOUT)) {
			Logger.debug("Timeout");
			this.wakeup_timeout_active = false;
			this.applyPowersave();
			
			if(isSleepingTime()) {
				Logger.debug("Sleeping time!");
				setMorningWakeup();
			} else if(!this.wakeup_active) {
				setWakeup(false);
			}
		} else if(intent.getAction().equals(MainService.ACTION_SCREEN_TIMEOUT)) {
			Logger.debug("Screen timeout");
			this.screen_timeout_active = false;
			this.screen_on = false;

			
			this.applyPowersave();
		} else if(intent.getAction().equals(MainService.ACTION_POWER_TIMEOUT)) {
			Logger.debug("Power timeout");
			this.power_timeout_active = false;
			this.power_on = false;

			
			this.applyPowersave();
		} else if(intent.getAction().equals(MainService.ACTION_WAKEUP)) {
			Logger.debug("Wakeup");
			
			this.cancelWakeupTimeout();
			this.stopPowersave();
			this.setWakeupTimeout();
		} else if(intent.getAction().equals(MainService.ACTION_DISABLE)) {
			stopSelf();
		}
		
		if(MainService.wake_lock.isHeld()) {
			MainService.wake_lock.release();
		}
		
        if(!settings.getStartService()) {
        	Logger.warning("Server should not be running");
        	stopSelf();
        }

		return START_STICKY;
	}
}
