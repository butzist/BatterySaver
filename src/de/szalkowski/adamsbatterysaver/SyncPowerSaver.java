package de.szalkowski.adamsbatterysaver;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

public class SyncPowerSaver extends PowerSaver {
	static final public int DEFAULT_FLAGS = FLAG_ENABLE_WITH_SCREEN + FLAG_ENABLE_WITH_POWER + FLAG_ENABLE_ON_INTERVAL;
	static final private String LOG = "de.szalkowski.adamsbatterysaver.Sync";
	
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

}
