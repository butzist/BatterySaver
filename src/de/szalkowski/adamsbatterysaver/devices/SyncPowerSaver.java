package de.szalkowski.adamsbatterysaver.devices;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;

public class SyncPowerSaver extends PowerSaver {
	static final private String LOG = "de.szalkowski.adamsbatterysaver.SyncPowerSaver";
	static final public int DEFAULT_FLAGS = FLAG_DISABLE_WITH_SCREEN + FLAG_DISABLE_WITH_POWER + FLAG_DISABLE_ON_INTERVAL;
	static final private String ACTION_CHECK_CONNECTION = "de.szalkowski.adamsbatterysaver.SyncPowerSaver.CHECK_CONNECTION_ACTION";
	static final private int NETWORK_CHECK_INTERVAL = 10000; 
	static final private int NETWORK_CHECK_RETRIES = 4;
	
	private BroadcastReceiver alarm_receiver;
	private int retries = 1;
	
	public SyncPowerSaver(Context context, int flags) {
		super(context, "sync", flags);
		
		this.alarm_receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				context.unregisterReceiver(alarm_receiver);
				
				if(isOnline() || retries >= NETWORK_CHECK_RETRIES) {
					doSync();
				} else {
					retries += 1;
					setCheckNetWakeup();					
				}
			}
		};
	}

	@Override
	protected void doStartPowersave() {
		ContentResolver.setMasterSyncAutomatically(false);
	}

	@Override
	protected void doStopPowersave() {
		this.retries = 1;
		
		if(isOnline()) {
			doSync();
		} else {
			setCheckNetWakeup();			
		}
	}
	
	protected void doSync() {
		ContentResolver.setMasterSyncAutomatically(true);
		AccountManager manager = AccountManager.get(context);
		for(Account a : manager.getAccountsByType("com.android.email")) {
			Bundle extras = new Bundle(1);
			extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
			ContentResolver.requestSync(a, "com.android.email", extras);
		}
	}
	
	protected void setCheckNetWakeup() {
		Log.d(LOG,"delaying sync (retry "+ this.retries + ")");
		
		IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_CHECK_CONNECTION);
        context.registerReceiver(this.alarm_receiver, filter);
        
		AlarmManager alarm = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(ACTION_CHECK_CONNECTION);
		PendingIntent pending = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		alarm.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + NETWORK_CHECK_INTERVAL, pending);
	}
	
	@Override
	protected boolean doIsEnabled() {
		return !ContentResolver.getMasterSyncAutomatically();
	}
	
	protected boolean isOnline() {
		ConnectivityManager connManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo net = connManager.getActiveNetworkInfo();
		
		if(net != null && net.isConnected()) {
			Log.v(LOG,net.getTypeName() + " online");
			return true;
		} else {
			Log.v(LOG,"network offline");
			return false;
		}
	}

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	@Override
	protected boolean doHasTraffic() throws Exception {
		if(android.os.Build.VERSION.SDK_INT >= 11) {
			return !ContentResolver.getCurrentSyncs().isEmpty();
		} else {
			return ContentResolver.getCurrentSync() != null;
		}
	}	
}
