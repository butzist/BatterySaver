package de.szalkowski.adamsbatterysaver;

import java.util.Set;

import android.content.Context;

public class LoggingSettingsManager extends SettingsManager {

	protected LoggingSettingsManager(Context context) {
		super(context);
	}

	@Override
	public void startTransaction() {
		Logger.debug("startTransaction");
		super.startTransaction();
	}

	@Override
	public void commitTransaction() throws TransactionFailed {
		Logger.debug("commitTransaction");
		super.commitTransaction();
	}

	@Override
	public int getInterval() {
		int value = super.getInterval();
		Logger.debug("getInterval: " + value);
		return value;
	}

	@Override
	public void setInterval(int value) {
		Logger.debug("setInterval: " + value);
		super.setInterval(value);
	}

	@Override
	public int getShortInterval() {
		int value = super.getShortInterval();
		Logger.debug("getShortInterval: " + value);
		return value;
	}

	@Override
	public void setShortInterval(int value) {
		Logger.debug("setShortInterval: " + value);
		super.setShortInterval(value);
	}

	@Override
	public int getTimeout() {
		int value = super.getTimeout();
		Logger.debug("getTimeout: " + value);
		return value;
	}

	@Override
	public void setTimeout(int value) {
		Logger.debug("setTimeout: " + value);
		super.setTimeout(value);
	}

	@Override
	public boolean getStartService() {
		boolean value = super.getStartService();
		Logger.debug("getStartService: " + value);
		return value;
	}

	@Override
	public void setStartService(boolean value) {
		Logger.debug("setStartService: " + value);
		super.setStartService(value);
	}

	@Override
	public int getNightModeFromHour() {
		int value = super.getNightModeFromHour();
		Logger.debug("getNightModeFromHour: " + value);
		return value;
	}

	@Override
	public void setNightModeFromHour(int value) {
		Logger.debug("setNightModeFromHour: " + value);
		super.setNightModeFromHour(value);
	}

	@Override
	public int getNightModeFromMinute() {
		int value = super.getNightModeFromMinute();
		Logger.debug("getNightModeFromMinute: " + value);
		return value;
	}

	@Override
	public void setNightModeFromMinute(int value) {
		Logger.debug("setNightModeFromMinute: " + value);
		super.setNightModeFromMinute(value);
	}

	@Override
	public int getNightModeToHour() {
		int value = super.getNightModeToHour();
		Logger.debug("getNightModeToHour: " + value);
		return value;
	}

	@Override
	public void setNightModeToHour(int value) {
		Logger.debug("setNightModeToHour: " + value);
		super.setNightModeToHour(value);
	}

	@Override
	public int getNightModeToMinute() {
		int value = super.getNightModeToMinute();
		Logger.debug("getNightModeToMinute: " + value);
		return value;
	}

	@Override
	public void setNightModeToMinute(int value) {
		Logger.debug("setNightModeToMinute: " + value);
		super.setNightModeToMinute(value);
	}

	@Override
	public int getWifiFlags() {
		int flags = super.getWifiFlags();
		Logger.debug("getWifiFlags: " + flags);
		return flags;
	}

	@Override
	public void setWifiFlags(int flags) {
		Logger.debug("setWifiFlags: " + flags);
		super.setWifiFlags(flags);
	}

	@Override
	public int getMobileDataFlags() {
		int flags = super.getMobileDataFlags();
		Logger.debug("getMobileDataFlags: " + flags);
		return flags;
	}

	@Override
	public void setMobileDataFlags(int flags) {
		Logger.debug("setMobileDataFlags: " + flags);
		super.setMobileDataFlags(flags);
	}

	@Override
	public int getSyncFlags() {
		int flags = super.getSyncFlags();
		Logger.debug("getSyncFlags: " + flags);
		return flags;
	}

	@Override
	public void setSyncFlags(int flags) {
		Logger.debug("setSyncFlags: " + flags);
		super.setSyncFlags(flags);
	}

	@Override
	public int getBluetoothFlags() {
		int flags = super.getBluetoothFlags();
		Logger.debug("getBluetoothFlags: " + flags);
		return flags;
	}

	@Override
	public void setBluetoothFlags(int flags) {
		Logger.debug("setBluetoothFlags: " + flags);
		super.setBluetoothFlags(flags);
	}

	@Override
	public Set<String> getWifiWhitelist() {
		Set<String> values = super.getWifiWhitelist(); 
		Logger.debug("getWifiWhitelist: " + values);
		return values;
	}

	@Override
	public void setWifiWhitelist(Set<String> values) {
		Logger.debug("setWifiWhitelist: " + values);
		super.setWifiWhitelist(values);
	}

	@Override
	public Set<String> getMobileDataWhitelist() {
		Set<String> values = super.getMobileDataWhitelist(); 
		Logger.debug("getMobileDataWhitelist: " + values);
		return values;
	}

	@Override
	public void setMobileDataWhitelist(Set<String> values) {
		Logger.debug("setMobileDataWhitelist: " + values);
		super.setMobileDataWhitelist(values);
	}

	@Override
	public Set<String> getSyncWhitelist() {
		Set<String> values = super.getSyncWhitelist();; 
		Logger.debug("getSyncWhitelist: " + values);
		return values;
	}

	@Override
	public void setSyncWhitelist(Set<String> values) {
		Logger.debug("setSyncWhitelist: " + values);
		super.setSyncWhitelist(values);
	}

	@Override
	public Set<String> getBluetoothWhitelist() {
		Set<String> values = super.getBluetoothWhitelist();
		Logger.debug("getBluetoothWhitelist: " + values);
		return values;
	}

	@Override
	public void setBluetoothWhitelist(Set<String> values) {
		Logger.debug("setBluetoothWhitelist: " + values);
		super.setBluetoothWhitelist(values);
	}

	@Override
	public boolean getWhitelistOnlyTopTask() {
		boolean value = super.getWhitelistOnlyTopTask(); 
		Logger.debug("getWhitelistOnlyTopTask: " + value);
		return value;
	}

	@Override
	public void setWhitelistOnlyTopTask(boolean value) {
		Logger.debug("setWhitelistOnlyTopTask: " + value);
		super.setWhitelistOnlyTopTask(value);
	}

	@Override
	public int getWifiTrafficLimit() {
		int value = super.getWifiTrafficLimit();
		Logger.debug("getWifiTrafficLimit: " + value);
		return value;
	}

	@Override
	public void setWifiTrafficLimit(int value) {
		Logger.debug("setWifiTrafficLimit: " + value);
		super.setWifiTrafficLimit(value);
	}

	@Override
	public int getMobileDataTrafficLimit() {
		int value = super.getMobileDataTrafficLimit();
		Logger.debug("getMobileDataTrafficLimit: " + value);
		return value;
	}

	@Override
	public void setMobileDataTrafficLimit(int value) {
		Logger.debug("setMobileDataTrafficLimit: " + value);
		super.setMobileDataTrafficLimit(value);
	}
}
