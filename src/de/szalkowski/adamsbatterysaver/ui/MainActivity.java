package de.szalkowski.adamsbatterysaver.ui;

import java.util.Calendar;

import org.thirdparty.AdvancedSettingsActivity;

import de.szalkowski.adamsbatterysaver.R;
import de.szalkowski.adamsbatterysaver.SettingsManager;
import de.szalkowski.adamsbatterysaver.devices.BluetoothPowerSaver;
import de.szalkowski.adamsbatterysaver.devices.MobileDataPowerSaver;
import de.szalkowski.adamsbatterysaver.devices.PowerSaver;
import de.szalkowski.adamsbatterysaver.devices.SyncPowerSaver;
import de.szalkowski.adamsbatterysaver.devices.WifiPowerSaver;
import de.szalkowski.adamsbatterysaver.service.MainService;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
    private SettingsManager settings;
	
    @Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
        loadSettings();
        setupWidgets();
        checkAndStartServiceIfNecessary();
    }

	private void setupWidgets() {
		setIntervalSeekBar();
        setStartStopToggle();
        setFromTime();
        setToTime();
        setFlags();
	}

	private void checkAndStartServiceIfNecessary() {
        boolean start_service = settings.getStartService();

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

	private void setStartStopToggle() {
        boolean start_service = settings.getStartService();

		ToggleButton toggle = (ToggleButton)this.findViewById(R.id.toggleButton);
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
        toggle.setChecked(start_service);
	}

	private void setIntervalSeekBar() {
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
        interval.setProgress(settings.getInterval());
        
		TextView interval_text = (TextView)MainActivity.this.findViewById(R.id.sync_interval);
		interval_text.setText(MainActivity.this.getString(R.string.sync_interval, interval.getProgress()));
	}

	private void loadSettings() {
		PreferenceManager.setDefaultValues(this, R.xml.settings, false);
        settings = SettingsManager.getSettingsManager(this.getApplicationContext());
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
		settings.startTransaction();
		
        ToggleButton toggle = (ToggleButton)this.findViewById(R.id.toggleButton);
        settings.setStartService(toggle.isChecked());
        
        SeekBar interval = (SeekBar)this.findViewById(R.id.seekInterval);
        settings.setInterval(interval.getProgress());
        
        saveFlags();

        settings.commitTransaction();
        
        if(toggle.isChecked()) {
    		Intent service = new Intent(MainActivity.this,MainService.class);
    		service.setAction(MainService.ACTION_UPDATE);
        	this.startService(service);
        }

        super.onDestroy();
	}
	
	public void setFromTime() {
        int hour = settings.getNightModeFromHour();
        int minute = settings.getNightModeFromMinute();
        
        Calendar time = Calendar.getInstance();
        time.set(Calendar.HOUR_OF_DAY, hour);
        time.set(Calendar.MINUTE, minute);
        
        Button button = (Button)this.findViewById(R.id.buttonFrom);
        button.setText(this.getText(R.string.from) + " " + DateFormat.getTimeFormat(this).format(time.getTime()));
	}
	
	public void setToTime() {
        int hour = settings.getNightModeToHour();
        int minute = settings.getNightModeToMinute();
        
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
				int hour = settings.getNightModeFromHour();
				int minute = settings.getNightModeFromMinute();

				// Create a new instance of TimePickerDialog and return it
				return new TimePickerDialog(getActivity(), this, hour, minute,
						DateFormat.is24HourFormat(getActivity()));
			}

			public void onTimeSet(TimePicker view, int hour, int minute) {
				settings.startTransaction();
				settings.setNightModeFromHour(hour);
				settings.setNightModeFromMinute(minute);
				settings.commitTransaction();
				
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
				int hour = settings.getNightModeToHour();
				int minute = settings.getNightModeToMinute();

				// Create a new instance of TimePickerDialog and return it
				return new TimePickerDialog(getActivity(), this, hour, minute,
						DateFormat.is24HourFormat(getActivity()));
			}

			public void onTimeSet(TimePicker view, int hour, int minute) {
				settings.startTransaction();
				settings.setNightModeToHour(hour);
				settings.setNightModeToMinute(minute);
				settings.commitTransaction();
				
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
		
		for(int i=0; i<devices.length; i++) {
			// FIXME avoid direct access by name
			int flags = settings.getInteger(devices[i]+"_flags", default_flags[i]);
			for(int j=0; j<CHECKBOX_IDS[i].length; j++) {
				CheckBox check = (CheckBox)this.findViewById(CHECKBOX_IDS[i][j]);
				check.setChecked((flags & flag_values[j]) != 0);
				
				if(j>0) {
					check.setEnabled((flags & PowerSaver.FLAG_DISABLE) == 0);
				}
			}
		}
	}
	
	protected void saveFlags() {
		
		for(int i=0; i<DEVICES.length; i++) {
			int flags = 0;
			for(int j=0; j<CHECKBOX_IDS[i].length; j++) {
				CheckBox check = (CheckBox)this.findViewById(CHECKBOX_IDS[i][j]);
				if(check.isChecked()) {
					flags += FLAG_VALUES[j];
				}
			}
			// FIXME avoid direct access by name
			settings.setValue(DEVICES[i]+"_flags", flags);
		}
	}
}
