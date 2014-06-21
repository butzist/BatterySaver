package de.szalkowski.adamsbatterysaver.settings;

public class PersistentIntegerSetting extends PersistentSetting<Integer> {
	public PersistentIntegerSetting(PersistentSettingsStorage storage,
			String key, Integer defaultValue) {
		super(storage, key, defaultValue);
	}

	@Override
	protected void persistValue() {
		storage.setValue(key, get());
	}

	@Override
	protected Integer getPersistedValue(Integer defaultValue) {
		return storage.getInteger(key, get());
	}
}
