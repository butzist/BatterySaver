package de.szalkowski.adamsbatterysaver;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;


public class SettingsManager {
	static final public String SETTINGS_INTERVAL = "interval";
	static final public String SETTINGS_INTERVAL_SHORT = "interval_short";
	static final public String SETTINGS_TIMEOUT = "timeout";
	static final public String SETTINGS_TRAFFIC_LIMIT = "traffic_limit";
	static final public String SETTINGS_START_SERVICE = "start_service";
	static final public String SETTINGS_NIGHTMODE_FROM_HOUR = "from_hour";
	static final public String SETTINGS_NIGHTMODE_FROM_MINUTE = "from_minute";
	static final public String SETTINGS_NIGHTMODE_TO_HOUR = "to_hour";
	static final public String SETTINGS_NIGHTMODE_TO_MINUTE = "to_minute";
	static final public String SETTINGS_WIFI_FLAGS = "wifi_flags";
	static final public String SETTINGS_MOBILEDATA_FLAGS = "data_flags";
	static final public String SETTINGS_SYNC_FLAGS = "sync_flags";
	static final public String SETTINGS_BLUETOOTH_FLAGS = "blue_flags";
	
	private SharedPreferences preferences;
	private SharedPreferences.Editor editor;
	private Resources resources;
	
	class TransactionFailed extends RuntimeException {
		private static final long serialVersionUID = -4552986045852705974L;
		
		public TransactionFailed(String message) {
			super(message);
		}
	}
		
	protected SettingsManager(Context context) {
		preferences = PreferenceManager.getDefaultSharedPreferences(context);
		resources = context.getResources();
		editor = null;
	}
	
	public static SettingsManager getSettingsManager(Context context) {
		return new SettingsManager(context);
	}
	
	private boolean hasPendingTransaction() {
		return editor != null;
	}
	
	public void startTransaction() {
		if(!hasPendingTransaction()) {
			createEditor();
		}
	}
	
	private void createEditor() {
		editor = preferences.edit();
	}
	
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
	
	// TODO make private or protected
	public void setValue(String key, int value) {
		setGenericValue(key, Integer.valueOf(value));		
	}
	
	public void setValue(String key, boolean value) {
		setGenericValue(key, Boolean.valueOf(value));		
	}
	
	public void setValue(String key, String value) {
		setGenericValue(key, value);		
	}
	
	private void setGenericValue(String key, Object value) {
		if(hasPendingTransaction()) {
			setValueInTransaction(key, value);
		} else {
			setSingleValue(key, value);
		}
	}
	
	private void setValueInTransaction(String key, Object value) {
		if(value instanceof Integer) {
			editor.putInt(key, (Integer)value);
		} else if(value instanceof Boolean) {
			editor.putBoolean(key, (Boolean)value);
		} else {
			editor.putString(key, (String)value);
		}
	}
	
	private void setSingleValue(String key, Object value) {
		startTransaction();
		setValueInTransaction(key, value);		
		commitTransaction();
	}
	
	// TODO remove me
	public int getInteger(String key, int defaultValue) {
		return preferences.getInt(key, defaultValue);
	}
	
	public int getInterval() {
		int defaultValue = resources.getInteger(R.integer.pref_interval_default);
		return preferences.getInt(SETTINGS_INTERVAL, defaultValue);
	}

	public void setInterval(int value) {
		setValue(SETTINGS_INTERVAL, value);
	}

	public int getShortInterval() {
		int defaultValue = resources.getInteger(R.integer.pref_interval_short_default);
		return preferences.getInt(SETTINGS_INTERVAL_SHORT, defaultValue);
	}

	public void setShortInterval(int value) {
		setValue(SETTINGS_INTERVAL_SHORT, value);
	}

	public int getTimeout() {
		int defaultValue = resources.getInteger(R.integer.pref_timeout_default);
		return preferences.getInt(SETTINGS_TIMEOUT, defaultValue);
	}

	public void setTimeout(int value) {
		setValue(SETTINGS_TIMEOUT, value);
	}

	public int getTrafficLimit() {
		int defaultValue = resources.getInteger(R.integer.pref_traffic_limit_default);
		return preferences.getInt(SETTINGS_TRAFFIC_LIMIT, defaultValue);
	}

	public void setTrafficLimit(int value) {
		setValue(SETTINGS_TRAFFIC_LIMIT, value);
	}

	public boolean getStartService() {
		boolean defaultValue = true;
		return preferences.getBoolean(SETTINGS_START_SERVICE, defaultValue);
	}

	public void setStartService(boolean value) {
		setValue(SETTINGS_START_SERVICE, value);
	}

	public int getNightModeFromHour() {
		int defaultValue = resources.getInteger(R.integer.pref_from_hour_default);
		return preferences.getInt(SETTINGS_NIGHTMODE_FROM_HOUR, defaultValue);
	}

	public void setNightModeFromHour(int value) {
		setValue(SETTINGS_NIGHTMODE_FROM_HOUR, value);
	}

	public int getNightModeFromMinute() {
		int defaultValue = 0;
		return preferences.getInt(SETTINGS_NIGHTMODE_FROM_MINUTE, defaultValue);
	}

	public void setNightModeFromMinute(int value) {
		setValue(SETTINGS_NIGHTMODE_FROM_MINUTE, value);
	}

	public int getNightModeToHour() {
		int defaultValue = resources.getInteger(R.integer.pref_to_hour_default);
		return preferences.getInt(SETTINGS_NIGHTMODE_TO_HOUR, defaultValue);
	}

	public void setNightModeToHour(int value) {
		setValue(SETTINGS_NIGHTMODE_TO_HOUR, value);
	}

	public int getNightModeToMinute() {
		int defaultValue = 0;
		return preferences.getInt(SETTINGS_NIGHTMODE_TO_MINUTE, defaultValue);
	}

	public void setNightModeToMinute(int value) {
		setValue(SETTINGS_NIGHTMODE_TO_MINUTE, value);
	}
	
	public int getWifiFlags(int defaultValue) {
		return preferences.getInt(SETTINGS_WIFI_FLAGS, defaultValue);
	}
	
	public void setWifiFlags(int flags) {
		setValue(SETTINGS_WIFI_FLAGS, flags);
	}
	
	public int getMobileDataFlags(int defaultValue) {
		return preferences.getInt(SETTINGS_MOBILEDATA_FLAGS, defaultValue);
	}
	
	public void setMobileDataFlags(int flags) {
		setValue(SETTINGS_MOBILEDATA_FLAGS, flags);
	}
	
	public int getSyncFlags(int defaultValue) {
		return preferences.getInt(SETTINGS_SYNC_FLAGS, defaultValue);
	}
	
	public void setSyncFlags(int flags) {
		setValue(SETTINGS_SYNC_FLAGS, flags);
	}
	
	public int getBluetoothFlags(int defaultValue) {
		return preferences.getInt(SETTINGS_BLUETOOTH_FLAGS, defaultValue);
	}
	
	public void setBluetoothFlags(int flags) {
		setValue(SETTINGS_BLUETOOTH_FLAGS, flags);
	}
}
