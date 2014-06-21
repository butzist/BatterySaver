package de.szalkowski.adamsbatterysaver.settings;

public class PersistentBooleanSetting extends PersistentSetting<Boolean> {

	protected PersistentBooleanSetting(PersistentSettingsStorage storage,
			String key, Boolean defaultValue) {
		super(storage, key, defaultValue);
	}
	
	@Override
	protected void persistValue() {
		storage.setValue(key, get());
	}

	@Override
	protected Boolean getPersistedValue(Boolean defaultValue) {
		return storage.getBoolean(key, get());
	}
}
