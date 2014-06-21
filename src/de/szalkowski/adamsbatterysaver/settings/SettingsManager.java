package de.szalkowski.adamsbatterysaver.settings;

import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import android.content.res.Resources;
import de.szalkowski.adamsbatterysaver.R;
import de.szalkowski.adamsbatterysaver.devices.PowerSaver;

public class SettingsManager {
	static final protected String SETTINGS_INTERVAL = "interval";
	static final protected String SETTINGS_INTERVAL_SHORT = "interval_short";
	static final protected String SETTINGS_TIMEOUT = "timeout";
	static final protected String SETTINGS_START_SERVICE = "start_service";
	static final protected String SETTINGS_NIGHTMODE_FROM_HOUR = "from_hour";
	static final protected String SETTINGS_NIGHTMODE_FROM_MINUTE = "from_minute";
	static final protected String SETTINGS_NIGHTMODE_TO_HOUR = "to_hour";
	static final protected String SETTINGS_NIGHTMODE_TO_MINUTE = "to_minute";
	static final protected String SETTINGS_WIFI_FLAGS = "wifi_flags";
	static final protected String SETTINGS_MOBILEDATA_FLAGS = "data_flags";
	static final protected String SETTINGS_SYNC_FLAGS = "sync_flags";
	static final protected String SETTINGS_BLUETOOTH_FLAGS = "blue_flags";
	static final protected String SETTINGS_WIFI_WHITELIST = "wifi_whitelist";
	static final protected String SETTINGS_MOBILEDATA_WHITELIST = "data_whitelist";
	static final protected String SETTINGS_SYNC_WHITELIST = "sync_whitelist";
	static final protected String SETTINGS_BLUETOOTH_WHITELIST = "blue_whitelist";
	static final protected String SETTINGS_ONLY_TOP_TASK = "only_top_task";	
	static final protected String SETTINGS_WIFI_TRAFFIC_LIMIT = "wifi_traffic_limit";
	static final protected String SETTINGS_MOBILEDATA_TRAFFIC_LIMIT = "data_traffic_limit";
	
	private Resources resources;
	private PersistentSettingsStorage storage;
	
	public SettingsManager(Context context) {
		this.resources = context.getResources();
		this.storage = new SharedPreferencesStorage(context);
		
		int defaultInteval = resources.getInteger(R.integer.pref_interval_default);
		interval = new PersistentIntegerSetting(storage, SETTINGS_INTERVAL, defaultInteval);
		
		int defaultIntevalShort = resources.getInteger(R.integer.pref_interval_short_default);
		interval_short = new PersistentIntegerSetting(storage, SETTINGS_INTERVAL_SHORT, defaultIntevalShort);
		
		int defaultTimeout = resources.getInteger(R.integer.pref_timeout_default);
		timeout = new PersistentIntegerSetting(storage, SETTINGS_TIMEOUT, defaultTimeout);
		
		boolean defaultStartService = true;
		start_service = new PersistentBooleanSetting(storage, SETTINGS_START_SERVICE, defaultStartService);
		
		int defaultNightModeFromHour = resources.getInteger(R.integer.pref_from_hour_default);
		nightmode_from_hour = new PersistentIntegerSetting(storage, SETTINGS_NIGHTMODE_FROM_HOUR, defaultNightModeFromHour);

		int defaultNightModeFromMinute = 0;
		nightmode_from_minute = new PersistentIntegerSetting(storage, SETTINGS_NIGHTMODE_FROM_MINUTE, defaultNightModeFromMinute);

		int defaultNightModeToHour = resources.getInteger(R.integer.pref_to_hour_default);
		nightmode_to_hour = new PersistentIntegerSetting(storage, SETTINGS_NIGHTMODE_TO_HOUR, defaultNightModeToHour);

		int defaultNightModeToMinute = 0;
		nightmode_to_minute = new PersistentIntegerSetting(storage, SETTINGS_NIGHTMODE_TO_MINUTE, defaultNightModeToMinute);

		int defaultWifiFlags = PowerSaver.FLAG_DISABLE_WITH_SCREEN + PowerSaver.FLAG_DISABLE_WITH_POWER + PowerSaver.FLAG_DISABLE_ON_INTERVAL + PowerSaver.FLAG_SAVE_STATE;
		wifi_flags = new PersistentIntegerSetting(storage, SETTINGS_WIFI_FLAGS, defaultWifiFlags);

		int defaultMobileDataFlags = PowerSaver.FLAG_DISABLE_WITH_SCREEN + PowerSaver.FLAG_DISABLE_WITH_POWER + PowerSaver.FLAG_DISABLE_ON_INTERVAL + PowerSaver.FLAG_SAVE_STATE;
		mobile_data_flags = new PersistentIntegerSetting(storage, SETTINGS_MOBILEDATA_FLAGS, defaultMobileDataFlags);

		int defaultSyncFlags = PowerSaver.FLAG_DISABLE_WITH_SCREEN + PowerSaver.FLAG_DISABLE_WITH_POWER + PowerSaver.FLAG_DISABLE_ON_INTERVAL;
		sync_flags = new PersistentIntegerSetting(storage, SETTINGS_SYNC_FLAGS, defaultSyncFlags);

		int defaultBluetoothFlags = PowerSaver.FLAG_DISABLE_WITH_POWER + PowerSaver.FLAG_DISABLED_WHILE_TRAFFIC + PowerSaver.FLAG_SAVE_STATE;
		bluetooth_flags = new PersistentIntegerSetting(storage, SETTINGS_BLUETOOTH_FLAGS, defaultBluetoothFlags);

		Set<String> defaultWifiWhitelist = new HashSet<String>();
		wifi_white_list = new PersistentStringSetSetting(storage, SETTINGS_WIFI_WHITELIST, defaultWifiWhitelist);

		Set<String> defaultMobileDataWhitelist = new HashSet<String>();
		mobile_data_white_list = new PersistentStringSetSetting(storage, SETTINGS_MOBILEDATA_WHITELIST, defaultMobileDataWhitelist);

		Set<String> defaultSyncWhitelist = new HashSet<String>();
		sync_white_list = new PersistentStringSetSetting(storage, SETTINGS_SYNC_WHITELIST, defaultSyncWhitelist);

		Set<String> defaultBluetoothWhitelist = new HashSet<String>();
		bluetooth_white_list = new PersistentStringSetSetting(storage, SETTINGS_BLUETOOTH_WHITELIST, defaultBluetoothWhitelist);

		boolean defaultOnlyTopTask = resources.getBoolean(R.bool.pref_only_top_task_default);
		only_top_task = new PersistentBooleanSetting(storage, SETTINGS_ONLY_TOP_TASK, defaultOnlyTopTask);

		int defaultWifiTrafficLimit = resources.getInteger(R.integer.pref_wifi_traffic_limit_default);
		wifi_traffic_limit = new PersistentIntegerSetting(storage, SETTINGS_WIFI_TRAFFIC_LIMIT, defaultWifiTrafficLimit);

		int defaultMobileDataTrafficLimit = resources.getInteger(R.integer.pref_data_traffic_limit_default);
		mobile_data_traffic_limit = new PersistentIntegerSetting(storage, SETTINGS_MOBILEDATA_TRAFFIC_LIMIT, defaultMobileDataTrafficLimit);
	}
	
	public void startTransaction() {
		storage.startTransaction();
	}

	public void commitTransaction() {
		storage.commitTransaction();
	}

	final public Setting<Integer> interval;
	final public Setting<Integer> interval_short;
	final public Setting<Integer> timeout;
	final public Setting<Boolean> start_service;
	final public Setting<Integer> nightmode_from_minute;
	final public Setting<Integer> nightmode_from_hour;
	final public Setting<Integer> nightmode_to_minute;
	final public Setting<Integer> nightmode_to_hour;
	final public Setting<Integer> wifi_flags;
	final public Setting<Integer> mobile_data_flags;
	final public Setting<Integer> sync_flags;
	final public Setting<Integer> bluetooth_flags;
	final public Setting<Set<String>> wifi_white_list;
	final public Setting<Set<String>> mobile_data_white_list;
	final public Setting<Set<String>> sync_white_list;
	final public Setting<Set<String>> bluetooth_white_list;
	final public Setting<Boolean> only_top_task;
	final public Setting<Integer> wifi_traffic_limit;
	final public Setting<Integer> mobile_data_traffic_limit;
}
