package de.szalkowski.adamsbatterysaver;

import java.util.Set;

public interface SettingsProvider {
	public abstract int getInterval();

	public abstract int getShortInterval();

	public abstract int getTimeout();

	public abstract int getTrafficLimit();

	public abstract boolean getStartService();

	public abstract int getNightModeFromHour();

	public abstract int getNightModeFromMinute();

	public abstract int getNightModeToHour();

	public abstract int getNightModeToMinute();
	
	public abstract boolean getWhitelistOnlyTopTask();

	public abstract int getWifiFlags();

	public abstract int getMobileDataFlags();

	public abstract int getSyncFlags();

	public abstract int getBluetoothFlags();
	
	public abstract Set<String> getWifiWhitelist();

	public abstract Set<String> getMobileDataWhitelist();

	public abstract Set<String> getSyncWhitelist();

	public abstract Set<String> getBluetoothWhitelist();
}