package de.szalkowski.adamsbatterysaver;

import java.util.Set;

import de.szalkowski.adamsbatterysaver.SettingsManager.TransactionFailed;

public interface SettingsStorage extends SettingsProvider {

	void setMobileDataTrafficLimit(int value);

	void setWifiTrafficLimit(int value);

	void setWhitelistOnlyTopTask(boolean value);

	void setBluetoothWhitelist(Set<String> values);

	void setSyncWhitelist(Set<String> values);

	void setMobileDataWhitelist(Set<String> values);

	void setWifiWhitelist(Set<String> values);

	void setBluetoothFlags(int flags);

	void setSyncFlags(int flags);

	void setMobileDataFlags(int flags);

	void setWifiFlags(int flags);

	void setNightModeToMinute(int value);

	void setNightModeToHour(int value);

	void setNightModeFromMinute(int value);

	void setNightModeFromHour(int value);

	void setStartService(boolean value);

	void setTimeout(int value);

	void setShortInterval(int value);

	void setInterval(int value);

	void commitTransaction() throws TransactionFailed;

	void startTransaction();

}
