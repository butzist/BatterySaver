package de.szalkowski.adamsbatterysaver;

import android.content.ContentResolver;
import android.content.Context;

public class SyncPowerSaver extends PowerSaver {
	static final public int DEFAULT_FLAGS = FLAG_ENABLE_WITH_SCREEN + FLAG_ENABLE_WITH_POWER + FLAG_ENABLE_ON_INTERVAL;

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
	}

	@Override
	protected boolean doIsEnabled() {
		return !ContentResolver.getMasterSyncAutomatically();
	}

}
