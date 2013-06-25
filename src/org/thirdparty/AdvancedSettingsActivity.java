/**
 * Based on code from Stackoverflow.com under CC BY-SA 3.0
 * Url:  http://stackoverflow.com/questions/6822319/what-to-use-instead-of-addpreferencesfromresource-in-a-preferenceactivity
 * By: http://stackoverflow.com/users/421049/garret-wilson
 */


package org.thirdparty;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

public class AdvancedSettingsActivity extends PreferenceActivity {
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final int resource = de.szalkowski.adamsbatterysaver.R.xml.settings;
		
		if(android.os.Build.VERSION.SDK_INT >= 11) {
			getFragmentManager().beginTransaction().replace(android.R.id.content, new PreferenceFragment() {
		        @Override
		        public void onCreate(final Bundle savedInstanceState)
		        {
		            super.onCreate(savedInstanceState);
		            addPreferencesFromResource(resource);
		        }
			}).commit();
		} else {
			this.addPreferencesFromResource(resource);
		}
    }
}
