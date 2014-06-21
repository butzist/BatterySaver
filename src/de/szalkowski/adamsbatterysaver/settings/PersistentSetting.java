package de.szalkowski.adamsbatterysaver.settings;

abstract class PersistentSetting<T> extends Setting<T> {
	protected PersistentSetting(PersistentSettingsStorage storage, String key, T defaultValue) {
		super(defaultValue);
		T value = getPersistedValue(defaultValue);
		super.set(value);
		this.storage = storage;
		this.key = key;
	}
	
	protected abstract void persistValue();
	protected abstract T getPersistedValue(T defaultValue);
	
	public void set(T value) {
		super.set(value);
		persistValue();
	}

	protected final PersistentSettingsStorage storage;
	protected final String key;
}
