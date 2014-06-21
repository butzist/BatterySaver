package de.szalkowski.adamsbatterysaver.settings;

import java.util.Set;

import de.szalkowski.adamsbatterysaver.settings.SharedPreferencesStorage.TransactionFailed;

public interface PersistentSettingsStorage {

	public abstract void startTransaction();

	public abstract void commitTransaction() throws TransactionFailed;


	public abstract void setValue(String key, int value);

	public abstract void setValue(String key, boolean value);

	public abstract void setValue(String key, float value);

	public abstract void setValue(String key, String value);

	public abstract void setValue(String key, Set<String> value);

	public abstract int getInteger(String key, int defaultValue);

	public abstract boolean getBoolean(String key, boolean defaultValue);

	public abstract float getFloat(String key, float defaultValue);

	public abstract String getString(String key, String defaultValue);

	public abstract Set<String> getStringSet(String key, Set<String> defaultValue);
}