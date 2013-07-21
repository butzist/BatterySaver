/**
 * Based on code from Stackoverflow.com under CC BY-SA 3.0
 * Url:  http://stackoverflow.com/questions/6822319/what-to-use-instead-of-addpreferencesfromresource-in-a-preferenceactivity
 * By: http://stackoverflow.com/users/421049/garret-wilson
 */


package de.szalkowski.adamsbatterysaver;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class AdvancedSettingsActivity extends PreferenceActivity {
	private final int resource = de.szalkowski.adamsbatterysaver.R.xml.settings;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.addPreferencesFromResource(resource);
    }
}
