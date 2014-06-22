package de.szalkowski.adamsbatterysaver.settings.test;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;


import org.junit.Test;

import de.szalkowski.adamsbatterysaver.settings.Setting;

public class SettingTest {

	@Test
	public void testConstructor() {
		Setting<Integer> setting = new Setting<Integer>(42);
		assertEquals(42, (int)setting.get());		
	}

	@Test
	public void testGetAndSet() {
		Setting<Float> setting = new Setting<Float>(0.0f);
		setting.set(42.0f);
		assertEquals(42.0f, (float)setting.get(), 0.0);		
	}
	
	@Test
	public void testObserver() {
		class TestObserver implements Setting.Observer<Integer> {
			public void updateSetting(Integer value) {
				this.value = value;
			}
			public int value = 0;
		}
		
		Setting<Integer> setting = new Setting<Integer>(0);
		TestObserver observer = new TestObserver();
		setting.addObserver(observer);
		
		setting.set(42);
		
		assertEquals(42, observer.value);		
	}
	
	@Test
	public void testObserverNotCalledWhenNewValueIdentical() {
		class TestObserver implements Setting.Observer<String> {
			public void updateSetting(String value) {
				this.called = true;
			}
			public boolean called = false;
		}
		
		Setting<String> setting = new Setting<String>("42");
		TestObserver observer = new TestObserver();
		setting.addObserver(observer);
		
		String s = new String("42");
		setting.set(s);
		
		assertFalse(observer.called);		
	}
	
	@Test
	public void testStringSet() {
		class TestObserver implements Setting.Observer<Set<String>> {
			public void updateSetting(Set<String> value) {
				this.called = true;
			}
			public boolean called = false;
		}
		
		Set<String> set1 = new HashSet<String>();
		set1.add("Hans");
		set1.add("Wurst");
		
		Setting<Set<String>> setting = new Setting<Set<String>>(set1);
		TestObserver observer = new TestObserver();
		setting.addObserver(observer);
		
		Set<String> set2 = new HashSet<String>();
		set2.add("Hans");
		set2.add("Wurst");
		
		setting.set(set2);
		
		assertFalse(observer.called);		
	}
}
