package de.szalkowski.adamsbatterysaver.settings;

public class PersistentFloatSetting extends PersistentSetting<Float> {
		protected PersistentFloatSetting(PersistentSettingsStorage storage,
			String key, Float defaultValue) {
		super(storage, key, defaultValue);
	}

	@Override
	protected void persistValue() {
		storage.setValue(key, get());
	}

	@Override
	protected Float getPersistedValue(Float defaultValue) {
		return storage.getFloat(key, get());
	}
}
