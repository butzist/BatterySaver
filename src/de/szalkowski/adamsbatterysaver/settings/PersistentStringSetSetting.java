package de.szalkowski.adamsbatterysaver.settings;

import java.util.Set;

public class PersistentStringSetSetting extends PersistentSetting<Set<String>> {

	protected PersistentStringSetSetting(PersistentSettingsStorage storage,
			String key, Set<String> defaultValue) {
		super(storage, key, defaultValue);
	}

	@Override
	protected void persistValue() {
		storage.setValue(key, get());
	}

	@Override
	protected Set<String> getPersistedValue(Set<String> defaultValue) {
		return storage.getStringSet(key, get());
	}
}
