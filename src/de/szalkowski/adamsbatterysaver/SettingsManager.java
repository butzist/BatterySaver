package de.szalkowski.adamsbatterysaver;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;


public class SettingsManager implements SettingsStorage {
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
	
	private SharedPreferences preferences;
	private SharedPreferences.Editor editor;
	private SettingsProvider defaults;
	
	class TransactionFailed extends RuntimeException {
		private static final long serialVersionUID = -4552986045852705974L;
		
		public TransactionFailed(String message) {
			super(message);
		}
	}
		
	protected SettingsManager(Context context) {
		preferences = PreferenceManager.getDefaultSharedPreferences(context);
		defaults = new DefaultSettings(context);
		editor = null;
	}
	
	private boolean hasPendingTransaction() {
		return editor != null;
	}
	
	@Override
	public void startTransaction() {
		if(!hasPendingTransaction()) {
			createEditor();
		}
	}
	
	private void createEditor() {
		editor = preferences.edit();
	}
	
	@Override
	public void commitTransaction() throws TransactionFailed {
		if(hasPendingTransaction()) {
			commitEditor();
		} else {
			throw new TransactionFailed("no transaction started");
		}
	}
	
	private void commitEditor() throws TransactionFailed {
		boolean successful;
		successful = editor.commit();
		
		if(!successful) {
			throw new TransactionFailed("editor commit failed");
		}
	}
	
	private void setValue(String key, int value) {
		setGenericValue(key, Integer.valueOf(value));		
	}
	
	private void setValue(String key, boolean value) {
		setGenericValue(key, Boolean.valueOf(value));		
	}
	
	private void setValue(String key, float value) {
		setGenericValue(key, Float.valueOf(value));		
	}
	
	private void setValue(String key, String value) {
		setGenericValue(key, value);		
	}
		
	private void setValue(String key, Set<String> value) {
		setGenericValue(key, value);		
	}
	
	private void setGenericValue(String key, Object value) {
		if(hasPendingTransaction()) {
			setValueInTransaction(key, value);
		} else {
			setSingleValue(key, value);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void setValueInTransaction(String key, Object value) {
		if(value instanceof Integer) {
			editor.putInt(key, (Integer)value);
		} else if(value instanceof Boolean) {
			editor.putBoolean(key, (Boolean)value);
		} else if(value instanceof Float) {
			editor.putFloat(key, (Float)value);
		} else if(value instanceof Set<?>) {
			setStringSet(key, (Set<String>) value);
		} else {
			editor.putString(key, (String)value);
		}
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setStringSet(String key, Set<String> value) {
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			editor.putStringSet(key, value);
		} else {
			setValue(key, joinStringSet(value));
		}
	}
	
	private String joinStringSet(Set<String> value) {
		StringBuilder builder = new StringBuilder();
		Iterator<String> iter = value.iterator();

		if(iter.hasNext()) {
			builder.append(iter.next());
		}
		
		while(iter.hasNext()) {
			builder.append(":");
			builder.append(iter.next());
		}
		
		return builder.toString();
	}

	private void setSingleValue(String key, Object value) {
		startTransaction();
		setValueInTransaction(key, value);		
		commitTransaction();
	}
	
	private int getInteger(String key, int defaultValue) {
		return preferences.getInt(key, defaultValue);
	}
	
	private boolean getBoolean(String key, boolean defaultValue) {
		return preferences.getBoolean(key, defaultValue);
	}
	
	private float getFloat(String key, float defaultValue) {
		return preferences.getFloat(key, defaultValue);
	}
	
	private String getString(String key, String defaultValue) {
		return preferences.getString(key, defaultValue);
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private Set<String> getStringSet(String key, Set<String> defaultValue) {
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			return preferences.getStringSet(key, defaultValue);
		} else {
			return splitString(getString(key, joinStringSet(defaultValue)));
		}
	}
	
	private Set<String> splitString(String string) {
		String[] strings = string.split(":");
		return new HashSet<String>(Arrays.asList(strings));
	}

	@Override
	public int getInterval() {
		return getInteger(SETTINGS_INTERVAL, defaults.getInterval());
	}

	@Override
	public void setInterval(int value) {
		setValue(SETTINGS_INTERVAL, value);
	}

	@Override
	public int getShortInterval() {
		return getInteger(SETTINGS_INTERVAL_SHORT, defaults.getShortInterval());
	}

	@Override
	public void setShortInterval(int value) {
		setValue(SETTINGS_INTERVAL_SHORT, value);
	}

	@Override
	public int getTimeout() {
		return getInteger(SETTINGS_TIMEOUT, defaults.getTimeout());
	}

	@Override
	public void setTimeout(int value) {
		setValue(SETTINGS_TIMEOUT, value);
	}

	@Override
	public boolean getStartService() {
		return getBoolean(SETTINGS_START_SERVICE, defaults.getStartService());
	}

	@Override
	public void setStartService(boolean value) {
		setValue(SETTINGS_START_SERVICE, value);
	}

	@Override
	public int getNightModeFromHour() {
		return getInteger(SETTINGS_NIGHTMODE_FROM_HOUR, defaults.getNightModeFromHour());
	}

	@Override
	public void setNightModeFromHour(int value) {
		setValue(SETTINGS_NIGHTMODE_FROM_HOUR, value);
	}

	@Override
	public int getNightModeFromMinute() {
		return getInteger(SETTINGS_NIGHTMODE_FROM_MINUTE, defaults.getNightModeFromMinute());
	}

	@Override
	public void setNightModeFromMinute(int value) {
		setValue(SETTINGS_NIGHTMODE_FROM_MINUTE, value);
	}

	@Override
	public int getNightModeToHour() {
		return getInteger(SETTINGS_NIGHTMODE_TO_HOUR, defaults.getNightModeToHour());
	}

	@Override
	public void setNightModeToHour(int value) {
		setValue(SETTINGS_NIGHTMODE_TO_HOUR, value);
	}

	@Override
	public int getNightModeToMinute() {
		return getInteger(SETTINGS_NIGHTMODE_TO_MINUTE, defaults.getNightModeToMinute());
	}

	@Override
	public void setNightModeToMinute(int value) {
		setValue(SETTINGS_NIGHTMODE_TO_MINUTE, value);
	}

	@Override
	public int getWifiFlags() {
		return getInteger(SETTINGS_WIFI_FLAGS, defaults.getWifiFlags());
	}
	
	@Override
	public void setWifiFlags(int flags) {
		setValue(SETTINGS_WIFI_FLAGS, flags);
	}

	@Override
	public int getMobileDataFlags() {
		return getInteger(SETTINGS_MOBILEDATA_FLAGS, defaults.getMobileDataFlags());
	}
	
	@Override
	public void setMobileDataFlags(int flags) {
		setValue(SETTINGS_MOBILEDATA_FLAGS, flags);
	}

	@Override
	public int getSyncFlags() {
		return getInteger(SETTINGS_SYNC_FLAGS, defaults.getSyncFlags());
	}
	
	@Override
	public void setSyncFlags(int flags) {
		setValue(SETTINGS_SYNC_FLAGS, flags);
	}

	@Override
	public int getBluetoothFlags() {
		return getInteger(SETTINGS_BLUETOOTH_FLAGS, defaults.getBluetoothFlags());
	}
	
	@Override
	public void setBluetoothFlags(int flags) {
		setValue(SETTINGS_BLUETOOTH_FLAGS, flags);
	}

	@Override
	public Set<String> getWifiWhitelist() {
		return getStringSet(SETTINGS_WIFI_WHITELIST, defaults.getWifiWhitelist());
	}
	
	@Override
	public void setWifiWhitelist(Set<String> values) {
		setValue(SETTINGS_WIFI_WHITELIST, values);
	}

	@Override
	public Set<String> getMobileDataWhitelist() {
		return getStringSet(SETTINGS_MOBILEDATA_WHITELIST, defaults.getMobileDataWhitelist());
	}
	
	@Override
	public void setMobileDataWhitelist(Set<String> values) {
		setValue(SETTINGS_MOBILEDATA_WHITELIST, values);
	}

	@Override
	public Set<String> getSyncWhitelist() {
		return getStringSet(SETTINGS_SYNC_WHITELIST, defaults.getSyncWhitelist());
	}
	
	@Override
	public void setSyncWhitelist(Set<String> values) {
		setValue(SETTINGS_SYNC_WHITELIST, values);
	}
	
	@Override
	public Set<String> getBluetoothWhitelist() {
		return getStringSet(SETTINGS_BLUETOOTH_WHITELIST, defaults.getBluetoothWhitelist());
	}
	
	@Override
	public void setBluetoothWhitelist(Set<String> values) {
		setValue(SETTINGS_BLUETOOTH_WHITELIST, values);
	}

	@Override
	public boolean getWhitelistOnlyTopTask() {
		return getBoolean(SETTINGS_ONLY_TOP_TASK, defaults.getWhitelistOnlyTopTask());
	}
	
	@Override
	public void setWhitelistOnlyTopTask(boolean value) {
		setValue(SETTINGS_ONLY_TOP_TASK, value);
	}

	@Override
	public int getWifiTrafficLimit() {
		return getInteger(SETTINGS_WIFI_TRAFFIC_LIMIT, defaults.getWifiTrafficLimit());
	}
	
	@Override
	public void setWifiTrafficLimit(int value) {
		setValue(SETTINGS_WIFI_TRAFFIC_LIMIT, value);
	}

	@Override
	public int getMobileDataTrafficLimit() {
		return getInteger(SETTINGS_MOBILEDATA_TRAFFIC_LIMIT, defaults.getMobileDataTrafficLimit());
	}

	@Override
	public void setMobileDataTrafficLimit(int value) {
		setValue(SETTINGS_MOBILEDATA_TRAFFIC_LIMIT, value);
	}
}
