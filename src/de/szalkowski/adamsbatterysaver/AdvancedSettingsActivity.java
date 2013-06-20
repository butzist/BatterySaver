package de.szalkowski.adamsbatterysaver;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class AdvancedSettingsActivity extends PreferenceActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.addPreferencesFromResource(R.xml.settings);
	}
}
