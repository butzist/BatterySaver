package de.szalkowski.adamsbatterysaver;

import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import android.content.res.Resources;
import de.szalkowski.adamsbatterysaver.devices.PowerSaver;

public class DefaultSettings implements SettingsProvider {
	private Resources resources;
	
	public DefaultSettings(Context context) {
		resources = context.getResources();
	}

	@Override
	public int getInterval() {
		return resources.getInteger(R.integer.pref_interval_default);
	}

	@Override
	public int getShortInterval() {
		return resources.getInteger(R.integer.pref_interval_short_default);
	}

	@Override
	public int getTimeout() {
		return resources.getInteger(R.integer.pref_timeout_default);
	}

	@Override
	public boolean getStartService() {
		return true;
	}

	@Override
	public int getNightModeFromHour() {
		return resources.getInteger(R.integer.pref_from_hour_default);
	}
	
	@Override
	public int getNightModeFromMinute() {
		return 0;
	}

	@Override
	public int getNightModeToHour() {
		return resources.getInteger(R.integer.pref_to_hour_default);
	}

	@Override
	public int getNightModeToMinute() {
		return 0;
	}

	@Override
	public int getWifiFlags() {
		return PowerSaver.FLAG_DISABLE_WITH_SCREEN + PowerSaver.FLAG_DISABLE_WITH_POWER + PowerSaver.FLAG_DISABLE_ON_INTERVAL + PowerSaver.FLAG_SAVE_STATE;
	}
	
	@Override
	public int getMobileDataFlags() {
		return PowerSaver.FLAG_DISABLE_WITH_SCREEN + PowerSaver.FLAG_DISABLE_WITH_POWER + PowerSaver.FLAG_DISABLE_ON_INTERVAL + PowerSaver.FLAG_SAVE_STATE;
	}

	@Override
	public int getSyncFlags() {
		return PowerSaver.FLAG_DISABLE_WITH_SCREEN + PowerSaver.FLAG_DISABLE_WITH_POWER + PowerSaver.FLAG_DISABLE_ON_INTERVAL;
	}
	
	@Override
	public int getBluetoothFlags() {
		return PowerSaver.FLAG_DISABLE_WITH_POWER + PowerSaver.FLAG_DISABLED_WHILE_TRAFFIC + PowerSaver.FLAG_SAVE_STATE;
	}

	@Override
	public boolean getWhitelistOnlyTopTask() {
		return resources.getBoolean(R.bool.pref_only_top_task_default);
	}

	@Override
	public Set<String> getWifiWhitelist() {
		return new HashSet<String>();
	}

	@Override
	public Set<String> getMobileDataWhitelist() {
		return new HashSet<String>();
	}

	@Override
	public Set<String> getSyncWhitelist() {
		return new HashSet<String>();
	}

	@Override
	public Set<String> getBluetoothWhitelist() {
		return new HashSet<String>();
	}

	public int getMobileDataTrafficLimit() {
		return resources.getInteger(R.integer.pref_data_traffic_limit_default);
	}

	public int getWifiTrafficLimit() {
		return resources.getInteger(R.integer.pref_wifi_traffic_limit_default);
	}
}
