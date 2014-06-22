package de.szalkowski.adamsbatterysaver.settings;

import java.util.LinkedList;
import java.util.List;

public class Setting<T> {
	
	public interface Observer<T> {
		public void updateSetting(T value);
	}

	public Setting(T value) {
		this.value = value;
	}
	
	public T get() {
		return value;
	}
	
	public void set(T value) {
		if (!value.equals(this.value)) {
			this.value = value;
			notifyObservers();
		}
	}
	
	public void addObserver(Observer<T> observer) {
		observers.add(observer);
	}
	
	private void notifyObservers() {
		for (Observer<T> observer : observers) {
			observer.updateSetting(value);
		}
	}
	
	private T value;
	private List<Observer<T>> observers = new LinkedList<Observer<T>>();
}
