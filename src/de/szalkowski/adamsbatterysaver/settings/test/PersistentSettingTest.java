package de.szalkowski.adamsbatterysaver.settings.test;

import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.jmock.Expectations;

import de.szalkowski.adamsbatterysaver.settings.*;

public class PersistentSettingTest {
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private final PersistentSettingsStorage storage = context.mock(PersistentSettingsStorage.class);

	@Test
	public void testPersistentDefaultValue() {
		final int defaultValue = 42;
		final String key = "xxx";

		context.checking(new Expectations() {{
			oneOf(storage).getInteger(key, defaultValue); will(returnValue(defaultValue));
		}});

		PersistentIntegerSetting setting = new PersistentIntegerSetting(storage, key, defaultValue);
		assertEquals(defaultValue, (int)setting.get());
	}

	@Test
	public void testPersistentStoredValue() {
		final String defaultValue = "42";
		final String newValue = "43";
		final String key = "xxx";

		context.checking(new Expectations() {{
			oneOf(storage).getString(key, defaultValue); will(returnValue(newValue));
		}});

		PersistentStringSetting setting = new PersistentStringSetting(storage, key, defaultValue);
		assertEquals(newValue, setting.get());
	}
	

	@Test
	public void testStoreValue() {
		final boolean defaultValue = true;
		final boolean newValue = false;
		final String key = "xxx";
		final Setting.Observer<Boolean> observer = context.mock(Setting.Observer.class);

		context.checking(new Expectations() {{
			oneOf(storage).getBoolean(key, defaultValue); will(returnValue(defaultValue));
			oneOf(storage).setValue(key, newValue);
			oneOf(observer).updateSetting(newValue);
		}});

		PersistentBooleanSetting setting = new PersistentBooleanSetting(storage, key, defaultValue);
		setting.addObserver(observer);
		setting.set(newValue);
		assertEquals(newValue, setting.get());
	}
}
