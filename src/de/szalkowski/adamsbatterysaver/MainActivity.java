package de.szalkowski.adamsbatterysaver;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.os.Bundle;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

public class MainActivity extends FragmentActivity {
	static final public String SETTINGS_INTERVAL = "interval";
	static final public String SETTINGS_INTERVAL_SHORT = "interval_short";
	static final public String SETTINGS_TIMEOUT = "timeout";
	static final public String SETTINGS_TRAFFIC_LIMIT = "traffic_limit";
	static final public String SETTINGS_START_SERVICE = "start_service";
	static final public String SETTINGS_NIGHTMODE_FROM_HOUR = "from_hour";
	static final public String SETTINGS_NIGHTMODE_FROM_MINUTE = "from_minute";
	static final public String SETTINGS_NIGHTMODE_TO_HOUR = "to_hour";
	static final public String SETTINGS_NIGHTMODE_TO_MINUTE = "to_minute";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        boolean start_service = settings.getBoolean(MainActivity.SETTINGS_START_SERVICE, true);
        if(start_service != MainService.is_running) {
        	String text;
        	if(start_service) {
        		text = this.getString(R.string.service_not_running);
        	} else {
        		text = this.getString(R.string.service_running);
        	}
        	Toast.makeText(this.getApplicationContext(), text, Toast.LENGTH_LONG).show();
        }

		getSupportFragmentManager().beginTransaction()
		.replace(R.id.container, new MainFragment()).commit();
	}

	/**
	 * Backward-compatible version of {@link ActionBar#getThemedContext()} that
	 * simply returns the {@link android.app.Activity} if
	 * <code>getThemedContext</code> is unavailable.
	 */
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	private Context getActionBarThemedContextCompat() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			return getActionBar().getThemedContext();
		} else {
			return this;
		}
	}
}
