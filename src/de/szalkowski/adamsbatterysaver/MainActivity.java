package de.szalkowski.adamsbatterysaver;

import java.util.Calendar;

import android.os.Bundle;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends FragmentActivity {
	static final public int DEFAULT_FROM = 22;
	static final public int DEFAULT_TO = 8;
	static final public int DEFAULT_INTERVAL = 15;
	static final public int DEFAULT_INTERVAL_SHORT = 5;
	static final public int DEFAULT_TIMEOUT = 60;
	static final public int DEFAULT_TRAFFIC_LIMIT = 750000;
	
	static final public String SETTINGS_INTERVAL = "interval";
	static final public String SETTINGS_INTERVAL_SHORT = "interval_short";
	static final public String SETTINGS_TIMEOUT = "timeout";
	static final public String SETTINGS_START_SERVICE = "start_service";
	static final public String SETTINGS_NIGHTMODE_FROM_HOUR = "from_hour";
	static final public String SETTINGS_NIGHTMODE_FROM_MINUTE = "from_minute";
	static final public String SETTINGS_NIGHTMODE_TO_HOUR = "to_hour";
	static final public String SETTINGS_NIGHTMODE_TO_MINUTE = "to_minute";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        SharedPreferences settings = this.getApplicationContext().getSharedPreferences("settings", MODE_PRIVATE);
        
        EditText interval = (EditText)this.findViewById(R.id.editInterval);
        interval.setText(Integer.toString(settings.getInt(SETTINGS_INTERVAL, DEFAULT_INTERVAL)));
        
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
					MainActivity.this.stopService(service);
				}
			}
		});
        
        setFromTime();
        setToTime();
        setFlags();
        
        toggle.setChecked(start_service);
        if(start_service != MainService.is_running) {
        	String text = "service was " + (start_service ? "not " : "") + "running";
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
	protected void onDestroy() {
        SharedPreferences settings = this.getApplicationContext().getSharedPreferences("settings", MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        
        ToggleButton toggle = (ToggleButton)this.findViewById(R.id.toggleButton);
        editor.putBoolean(SETTINGS_START_SERVICE, toggle.isChecked());
        
        EditText interval = (EditText)this.findViewById(R.id.editInterval);
        editor.putInt(SETTINGS_INTERVAL, Integer.parseInt(interval.getText().toString()));
        
        saveFlags(editor);
        
        editor.commit();

        super.onDestroy();
	}
	
	public void setFromTime() {
        SharedPreferences settings = this.getApplicationContext().getSharedPreferences("settings", MODE_PRIVATE);
        int hour = settings.getInt(SETTINGS_NIGHTMODE_FROM_HOUR, DEFAULT_FROM);
        int minute = settings.getInt(SETTINGS_NIGHTMODE_FROM_MINUTE, 0);
        
        Calendar time = Calendar.getInstance();
        time.set(Calendar.HOUR_OF_DAY, hour);
        time.set(Calendar.MINUTE, minute);
        
        Button button = (Button)this.findViewById(R.id.buttonFrom);
        button.setText("from " + DateFormat.getTimeFormat(this).format(time.getTime()));
	}
	
	public void setToTime() {
        SharedPreferences settings = this.getApplicationContext().getSharedPreferences("settings", MODE_PRIVATE);
        int hour = settings.getInt(SETTINGS_NIGHTMODE_TO_HOUR, DEFAULT_TO);
        int minute = settings.getInt(SETTINGS_NIGHTMODE_TO_MINUTE, 0);
        
        Calendar time = Calendar.getInstance();
        time.set(Calendar.HOUR_OF_DAY, hour);
        time.set(Calendar.MINUTE, minute);
        
        Button button = (Button)this.findViewById(R.id.buttonTo);
        button.setText("to " + DateFormat.getTimeFormat(this).format(time.getTime()));
	}
	
	public void showFromTimePicker(View v) {
		class TimePickerFragment_From extends DialogFragment
		implements TimePickerDialog.OnTimeSetListener {

			@Override
			public Dialog onCreateDialog(Bundle savedInstanceState) {
		        SharedPreferences settings = this.getActivity().getApplicationContext().getSharedPreferences("settings", MODE_PRIVATE);
		        
				int hour = settings.getInt(SETTINGS_NIGHTMODE_FROM_HOUR, DEFAULT_FROM);
				int minute = settings.getInt(SETTINGS_NIGHTMODE_FROM_MINUTE, 0);

				// Create a new instance of TimePickerDialog and return it
				return new TimePickerDialog(getActivity(), this, hour, minute,
						DateFormat.is24HourFormat(getActivity()));
			}

			public void onTimeSet(TimePicker view, int hour, int minute) {
		        SharedPreferences settings = this.getActivity().getApplicationContext().getSharedPreferences("settings", MODE_PRIVATE);
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
		        SharedPreferences settings = this.getActivity().getApplicationContext().getSharedPreferences("settings", MODE_PRIVATE);
		        
				int hour = settings.getInt(SETTINGS_NIGHTMODE_TO_HOUR, DEFAULT_FROM);
				int minute = settings.getInt(SETTINGS_NIGHTMODE_TO_MINUTE, 0);

				// Create a new instance of TimePickerDialog and return it
				return new TimePickerDialog(getActivity(), this, hour, minute,
						DateFormat.is24HourFormat(getActivity()));
			}

			public void onTimeSet(TimePicker view, int hour, int minute) {
		        SharedPreferences settings = this.getActivity().getApplicationContext().getSharedPreferences("settings", MODE_PRIVATE);
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
	
	protected void setFlags() {
		final String[] devices = {"wifi", "data", "blue", "sync"};
		final int[] flag_values = {PowerSaver.FLAG_ENABLE_WITH_SCREEN, PowerSaver.FLAG_ENABLE_WITH_POWER, PowerSaver.FLAG_ENABLE_ON_INTERVAL, PowerSaver.FLAG_SAVE_STATE};
		final int[] default_flags = {
				WifiPowerSaver.DEFAULT_FLAGS,
				MobileDataPowerSaver.DEFAULT_FLAGS,
				BluetoothPowerSaver.DEFAULT_FLAGS,
				SyncPowerSaver.DEFAULT_FLAGS
		};
		final int IDs[][] = {
				{R.id.CheckBoxWifiScreen, R.id.CheckBoxWifiPower, R.id.CheckBoxWifiInterval, R.id.CheckBoxWifiSave},
				{R.id.CheckBoxDataScreen, R.id.CheckBoxDataPower, R.id.CheckBoxDataInterval, R.id.CheckBoxDataSave},
				{R.id.CheckBoxBlueScreen, R.id.CheckBoxBluePower, R.id.CheckBoxBlueInterval, R.id.CheckBoxBlueSave},
				{R.id.CheckBoxSyncScreen, R.id.CheckBoxSyncPower, R.id.CheckBoxSyncInterval, R.id.CheckBoxSyncSave}
		};
		
        SharedPreferences settings = this.getApplicationContext().getSharedPreferences("settings", MODE_PRIVATE);

		for(int i=0; i<4; i++) {
			int flags = settings.getInt(devices[i]+"_flags", default_flags[i]);
			for(int j=0; j<4; j++) {
				CheckBox check = (CheckBox)this.findViewById(IDs[i][j]);
				check.setChecked((flags & flag_values[j]) != 0);
			}
		}
	}
	
	protected void saveFlags(SharedPreferences.Editor settings) {
		final String[] devices = {"wifi", "data", "blue", "sync"};
		final int[] flag_values = {PowerSaver.FLAG_ENABLE_WITH_SCREEN, PowerSaver.FLAG_ENABLE_WITH_POWER, PowerSaver.FLAG_ENABLE_ON_INTERVAL, PowerSaver.FLAG_SAVE_STATE};
		final int IDs[][] = {
				{R.id.CheckBoxWifiScreen, R.id.CheckBoxWifiPower, R.id.CheckBoxWifiInterval, R.id.CheckBoxWifiSave},
				{R.id.CheckBoxDataScreen, R.id.CheckBoxDataPower, R.id.CheckBoxDataInterval, R.id.CheckBoxDataSave},
				{R.id.CheckBoxBlueScreen, R.id.CheckBoxBluePower, R.id.CheckBoxBlueInterval, R.id.CheckBoxBlueSave},
				{R.id.CheckBoxSyncScreen, R.id.CheckBoxSyncPower, R.id.CheckBoxSyncInterval, R.id.CheckBoxSyncSave}
		};
		
		for(int i=0; i<4; i++) {
			int flags = 0;
			for(int j=0; j<4; j++) {
				CheckBox check = (CheckBox)this.findViewById(IDs[i][j]);
				if(check.isChecked()) {
					flags += flag_values[j];
				}
			}
			settings.putInt(devices[i]+"_flags", flags);
		}
	}
}
