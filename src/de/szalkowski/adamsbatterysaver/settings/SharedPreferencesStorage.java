package de.szalkowski.adamsbatterysaver.settings;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

public class SharedPreferencesStorage implements PersistentSettingsStorage {
	private SharedPreferences preferences;
	private SharedPreferences.Editor editor;
	
	class TransactionFailed extends RuntimeException {
		private static final long serialVersionUID = -4552986045852705974L;
		
		public TransactionFailed(String message) {
			super(message);
		}
	}
		
	public SharedPreferencesStorage(Context context) {
		preferences = PreferenceManager.getDefaultSharedPreferences(context);
		editor = null;
	}
	
	private boolean hasPendingTransaction() {
		return editor != null;
	}
	
	@Override
	public void startTransaction() {
		if(!hasPendingTransaction()) {
			createEditor();
		}
	}
	
	@SuppressLint("CommitPrefEdits")
	private void createEditor() {
		editor = preferences.edit();
	}
	
	@Override
	public void commitTransaction() throws TransactionFailed {
		if(hasPendingTransaction()) {
			commitEditor();
		} else {
			throw new TransactionFailed("no transaction started");
		}
	}
	
	private void commitEditor() throws TransactionFailed {
		boolean successful;
		successful = editor.commit();
		
		if(!successful) {
			throw new TransactionFailed("editor commit failed");
		}
	}
	
	@Override
	public void setValue(String key, int value) {
		setGenericValue(key, Integer.valueOf(value));		
	}
	
	@Override
	public void setValue(String key, boolean value) {
		setGenericValue(key, Boolean.valueOf(value));		
	}
	
	@Override
	public void setValue(String key, float value) {
		setGenericValue(key, Float.valueOf(value));		
	}
	
	@Override
	public void setValue(String key, String value) {
		setGenericValue(key, value);		
	}
		
	@Override
	public void setValue(String key, Set<String> value) {
		setGenericValue(key, value);		
	}
	
	private void setGenericValue(String key, Object value) {
		if(hasPendingTransaction()) {
			setValueInTransaction(key, value);
		} else {
			setSingleValue(key, value);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void setValueInTransaction(String key, Object value) {
		if(value instanceof Integer) {
			editor.putInt(key, (Integer)value);
		} else if(value instanceof Boolean) {
			editor.putBoolean(key, (Boolean)value);
		} else if(value instanceof Float) {
			editor.putFloat(key, (Float)value);
		} else if(value instanceof Set<?>) {
			setStringSet(key, (Set<String>) value);
		} else {
			editor.putString(key, (String)value);
		}
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setStringSet(String key, Set<String> value) {
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			editor.putStringSet(key, value);
		} else {
			setValue(key, joinStringSet(value));
		}
	}
	
	private String joinStringSet(Set<String> value) {
		StringBuilder builder = new StringBuilder();
		Iterator<String> iter = value.iterator();

		if(iter.hasNext()) {
			builder.append(iter.next());
		}
		
		while(iter.hasNext()) {
			builder.append(":");
			builder.append(iter.next());
		}
		
		return builder.toString();
	}

	private void setSingleValue(String key, Object value) {
		startTransaction();
		setValueInTransaction(key, value);		
		commitTransaction();
	}
	
	@Override
	public int getInteger(String key, int defaultValue) {
		return preferences.getInt(key, defaultValue);
	}
	
	@Override
	public boolean getBoolean(String key, boolean defaultValue) {
		return preferences.getBoolean(key, defaultValue);
	}
	
	@Override
	public float getFloat(String key, float defaultValue) {
		return preferences.getFloat(key, defaultValue);
	}
	
	@Override
	public String getString(String key, String defaultValue) {
		return preferences.getString(key, defaultValue);
	}
	
	@Override
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public Set<String> getStringSet(String key, Set<String> defaultValue) {
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			return preferences.getStringSet(key, defaultValue);
		} else {
			return splitString(getString(key, joinStringSet(defaultValue)));
		}
	}
	
	private Set<String> splitString(String string) {
		String[] strings = string.split(":");
		return new HashSet<String>(Arrays.asList(strings));
	}
}
