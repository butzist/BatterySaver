package de.szalkowski.adamsbatterysaver.settings;

public class PersistentStringSetting extends PersistentSetting<String> {

	public PersistentStringSetting(PersistentSettingsStorage storage,
			String key, String defaultValue) {
		super(storage, key, defaultValue);
	}

	@Override
	protected void persistValue() {
		storage.setValue(key, get());
	}

	@Override
	protected String getPersistedValue(String defaultValue) {
		return storage.getString(key, get());
	}
}
