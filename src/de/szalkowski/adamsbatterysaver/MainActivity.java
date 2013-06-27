package de.szalkowski.adamsbatterysaver;

import java.util.Calendar;

import org.thirdparty.AdvancedSettingsActivity;

import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

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
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		if(android.os.Build.VERSION.SDK_INT >= 11) {
			this.requestWindowFeature(Window.FEATURE_ACTION_BAR);
		}

		setContentView(R.layout.activity_main);
        
        PreferenceManager.setDefaultValues(this, R.xml.settings, false);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        
        SeekBar interval = (SeekBar)this.findViewById(R.id.seekInterval);
        interval.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if(fromUser && progress < 2) {
					seekBar.setProgress(2);
				}
				
				TextView text = (TextView)MainActivity.this.findViewById(R.id.sync_interval);
				text.setText(MainActivity.this.getString(R.string.sync_interval, seekBar.getProgress()));
			}
		});
        interval.setProgress(settings.getInt(SETTINGS_INTERVAL, this.getResources().getInteger(R.integer.pref_interval_default)));
        
		TextView interval_text = (TextView)MainActivity.this.findViewById(R.id.sync_interval);
		interval_text.setText(MainActivity.this.getString(R.string.sync_interval, interval.getProgress()));
        
        ToggleButton toggle = (ToggleButton)this.findViewById(R.id.toggleButton);
        boolean start_service = settings.getBoolean(SETTINGS_START_SERVICE, true);
        toggle.setChecked(MainService.is_running);
        toggle.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Intent service = new Intent(MainActivity.this,MainService.class);
				if(isChecked) {
					MainActivity.this.startService(service);
				} else {
					service.setAction(MainService.ACTION_DISABLE);
					MainActivity.this.startService(service);
				}
			}
		});
        
        setFromTime();
        setToTime();
        setFlags();
        
        toggle.setChecked(start_service);
        if(start_service != MainService.is_running) {
        	String text;
        	if(start_service) {
        		text = this.getString(R.string.service_not_running);
        	} else {
        		text = this.getString(R.string.service_running);
        	}
        	Toast.makeText(this.getApplicationContext(), text, Toast.LENGTH_LONG).show();
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings:
			Intent i1 = new Intent(this, AdvancedSettingsActivity.class);
			this.startActivity(i1);
			return true;

		case R.id.action_view_source:
			Intent i2 = new Intent(Intent.ACTION_VIEW);
			i2.setData(Uri.parse(this.getString(R.string.url_source)));
			this.startActivity(i2);
			return true;

		case R.id.action_view_translation:
			Intent i3 = new Intent(Intent.ACTION_VIEW);
			i3.setData(Uri.parse(this.getString(R.string.url_translation)));
			this.startActivity(i3);
			return true;
			
		case R.id.action_view_bugs:
			Intent i4 = new Intent(Intent.ACTION_VIEW);
			i4.setData(Uri.parse(this.getString(R.string.url_bugs)));
			this.startActivity(i4);
			return true;
			
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onDestroy() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        SharedPreferences.Editor editor = settings.edit();
        
        ToggleButton toggle = (ToggleButton)this.findViewById(R.id.toggleButton);
        editor.putBoolean(SETTINGS_START_SERVICE, toggle.isChecked());
        
        SeekBar interval = (SeekBar)this.findViewById(R.id.seekInterval);
        editor.putInt(SETTINGS_INTERVAL, interval.getProgress());
        
        saveFlags(editor);
        
        editor.commit();
        
        if(toggle.isChecked()) {
    		Intent service = new Intent(MainActivity.this,MainService.class);
    		service.setAction(MainService.ACTION_UPDATE);
        	this.startService(service);
        }

        super.onDestroy();
	}
	
	public void setFromTime() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        int hour = settings.getInt(SETTINGS_NIGHTMODE_FROM_HOUR, this.getResources().getInteger(R.integer.pref_from_hour_default));
        int minute = settings.getInt(SETTINGS_NIGHTMODE_FROM_MINUTE, 0);
        
        Calendar time = Calendar.getInstance();
        time.set(Calendar.HOUR_OF_DAY, hour);
        time.set(Calendar.MINUTE, minute);
        
        Button button = (Button)this.findViewById(R.id.buttonFrom);
        button.setText(this.getText(R.string.from) + " " + DateFormat.getTimeFormat(this).format(time.getTime()));
	}
	
	public void setToTime() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        int hour = settings.getInt(SETTINGS_NIGHTMODE_TO_HOUR, this.getResources().getInteger(R.integer.pref_to_hour_default));
        int minute = settings.getInt(SETTINGS_NIGHTMODE_TO_MINUTE, 0);
        
        Calendar time = Calendar.getInstance();
        time.set(Calendar.HOUR_OF_DAY, hour);
        time.set(Calendar.MINUTE, minute);
        
        Button button = (Button)this.findViewById(R.id.buttonTo);
        button.setText(this.getText(R.string.to) + " " + DateFormat.getTimeFormat(this).format(time.getTime()));
	}
	
	public void showFromTimePicker(View v) {
		class TimePickerFragment_From extends DialogFragment
		implements TimePickerDialog.OnTimeSetListener {

			@Override
			public Dialog onCreateDialog(Bundle savedInstanceState) {
		        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this.getActivity().getApplicationContext());
		        
				int hour = settings.getInt(SETTINGS_NIGHTMODE_FROM_HOUR, this.getResources().getInteger(R.integer.pref_from_hour_default));
				int minute = settings.getInt(SETTINGS_NIGHTMODE_FROM_MINUTE, 0);

				// Create a new instance of TimePickerDialog and return it
				return new TimePickerDialog(getActivity(), this, hour, minute,
						DateFormat.is24HourFormat(getActivity()));
			}

			public void onTimeSet(TimePicker view, int hour, int minute) {
		        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this.getActivity().getApplicationContext());
		        SharedPreferences.Editor editor = settings.edit();
		        
				editor.putInt(SETTINGS_NIGHTMODE_FROM_HOUR, hour);
				editor.putInt(SETTINGS_NIGHTMODE_FROM_MINUTE, minute);
				
				editor.commit();
				setFromTime();
			}
		}
		
		DialogFragment newFragment = new TimePickerFragment_From();
	    newFragment.show(getSupportFragmentManager(), "fromPicker");
	}

	public void showToTimePicker(View v) {
		class TimePickerFragment_To extends DialogFragment
		implements TimePickerDialog.OnTimeSetListener {

			@Override
			public Dialog onCreateDialog(Bundle savedInstanceState) {
		        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this.getActivity().getApplicationContext());
		        
				int hour = settings.getInt(SETTINGS_NIGHTMODE_TO_HOUR, this.getResources().getInteger(R.integer.pref_to_hour_default));
				int minute = settings.getInt(SETTINGS_NIGHTMODE_TO_MINUTE, 0);

				// Create a new instance of TimePickerDialog and return it
				return new TimePickerDialog(getActivity(), this, hour, minute,
						DateFormat.is24HourFormat(getActivity()));
			}

			public void onTimeSet(TimePicker view, int hour, int minute) {
		        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this.getActivity().getApplicationContext());
		        SharedPreferences.Editor editor = settings.edit();
		        
				editor.putInt(SETTINGS_NIGHTMODE_TO_HOUR, hour);
				editor.putInt(SETTINGS_NIGHTMODE_TO_MINUTE, minute);
				
				editor.commit();
				setToTime();
			}
		}
		
		
		DialogFragment newFragment = new TimePickerFragment_To();
	    newFragment.show(getSupportFragmentManager(), "toPicker");
	}
	
	public void onCheckboxDisable(View v) {
		for(int i=0; i < CHECKBOX_IDS.length; ++i) {
			if(v.getId() == CHECKBOX_IDS[i][0]) {
				CheckBox check = (CheckBox)this.findViewById(CHECKBOX_IDS[i][0]);
				boolean enabled = check.isChecked();
				
				for(int j=1; j < CHECKBOX_IDS[i].length; ++j) {
					View vv = this.findViewById(CHECKBOX_IDS[i][j]);
					vv.setEnabled(!enabled);
				}
			}
		}
		
	}
	
	private static final String[] DEVICES = {"wifi", "data", "blue", "sync"};
	private static final int[] FLAG_VALUES = {PowerSaver.FLAG_DISABLE, PowerSaver.FLAG_DISABLE_WITH_SCREEN, PowerSaver.FLAG_DISABLE_WITH_POWER, PowerSaver.FLAG_DISABLE_ON_INTERVAL, PowerSaver.FLAG_SAVE_STATE, PowerSaver.FLAG_DISABLED_WHILE_TRAFFIC};
	private static final int CHECKBOX_IDS[][] = {
			{R.id.CheckBoxWifiDisable, R.id.CheckBoxWifiScreen, R.id.CheckBoxWifiPower, R.id.CheckBoxWifiInterval, R.id.CheckBoxWifiSave, R.id.CheckBoxWifiTraffic},
			{R.id.CheckBoxDataDisable, R.id.CheckBoxDataScreen, R.id.CheckBoxDataPower, R.id.CheckBoxDataInterval, R.id.CheckBoxDataSave, R.id.CheckBoxDataTraffic},
			{R.id.CheckBoxBlueDisable, R.id.CheckBoxBlueScreen, R.id.CheckBoxBluePower, R.id.CheckBoxBlueInterval, R.id.CheckBoxBlueSave, R.id.CheckBoxBlueTraffic},
			{R.id.CheckBoxSyncDisable, R.id.CheckBoxSyncScreen, R.id.CheckBoxSyncPower, R.id.CheckBoxSyncInterval, R.id.CheckBoxSyncSave, R.id.CheckBoxSyncTraffic}
	};

	protected void setFlags() {
		final String[] devices = {"wifi", "data", "blue", "sync"};
		final int[] flag_values = {PowerSaver.FLAG_DISABLE, PowerSaver.FLAG_DISABLE_WITH_SCREEN, PowerSaver.FLAG_DISABLE_WITH_POWER, PowerSaver.FLAG_DISABLE_ON_INTERVAL, PowerSaver.FLAG_SAVE_STATE, PowerSaver.FLAG_DISABLED_WHILE_TRAFFIC};
		final int[] default_flags = {
				WifiPowerSaver.DEFAULT_FLAGS,
				MobileDataPowerSaver.DEFAULT_FLAGS,
				BluetoothPowerSaver.DEFAULT_FLAGS,
				SyncPowerSaver.DEFAULT_FLAGS
		};
		
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());

		for(int i=0; i<devices.length; i++) {
			int flags = settings.getInt(devices[i]+"_flags", default_flags[i]);
			for(int j=0; j<CHECKBOX_IDS[i].length; j++) {
				CheckBox check = (CheckBox)this.findViewById(CHECKBOX_IDS[i][j]);
				check.setChecked((flags & flag_values[j]) != 0);
				
				if(j>0) {
					check.setEnabled((flags & PowerSaver.FLAG_DISABLE) == 0);
				}
			}
		}
	}
	
	protected void saveFlags(SharedPreferences.Editor settings) {
		
		for(int i=0; i<DEVICES.length; i++) {
			int flags = 0;
			for(int j=0; j<CHECKBOX_IDS[i].length; j++) {
				CheckBox check = (CheckBox)this.findViewById(CHECKBOX_IDS[i][j]);
				if(check.isChecked()) {
					flags += FLAG_VALUES[j];
				}
			}
			settings.putInt(DEVICES[i]+"_flags", flags);
		}
	}
}
