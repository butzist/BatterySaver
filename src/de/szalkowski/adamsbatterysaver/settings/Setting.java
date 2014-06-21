package de.szalkowski.adamsbatterysaver.settings;

public class Setting<T> {
	public Setting(T value) {
		this.value = value;
	}
	
	public T get() {
		return value;
	}
	
	public void set(T value) {
		this.value = value;
	}
	
	private T value;
}
