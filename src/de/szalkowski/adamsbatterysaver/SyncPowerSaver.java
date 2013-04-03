package de.szalkowski.adamsbatterysaver;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;

public class SyncPowerSaver extends PowerSaver {
	//static private final String LOG = "de.szalkowski.adamsbatterysaver.SyncPowerSaver";
	static final public int DEFAULT_FLAGS = FLAG_DISABLE_WITH_SCREEN + FLAG_DISABLE_WITH_POWER + FLAG_DISABLE_ON_INTERVAL + FLAG_REQUIRES_NETWORK;
	static final protected String ACTION_CHECK_CONNECTION = "de.szalkowski.adamsbatterysaver.SyncPowerSaver.CHECK_CONNECTION_ACTION";
	
	public SyncPowerSaver(Context context, int flags) {
		super(context, "sync", flags);
	}

	@Override
	protected void doStartPowersave() {
		ContentResolver.setMasterSyncAutomatically(false);
	}

	@Override
	protected void doStopPowersave() {
		ContentResolver.setMasterSyncAutomatically(true);
		AccountManager manager = AccountManager.get(context);
		for(Account a : manager.getAccountsByType("com.android.email")) {
			Bundle extras = new Bundle(1);
			extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
			ContentResolver.requestSync(a, "com.android.email", extras);
		}				
	}
	
	@Override
	protected boolean doIsEnabled() {
		return !ContentResolver.getMasterSyncAutomatically();
	}

	@Override
	public boolean flagRequiresNetwork() {
		// XXX fixme
		return true;
	}
	
	
}
