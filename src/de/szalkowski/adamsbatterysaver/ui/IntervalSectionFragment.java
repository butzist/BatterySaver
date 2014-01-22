package de.szalkowski.adamsbatterysaver.ui;

import java.util.Calendar;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;
import de.szalkowski.adamsbatterysaver.R;
import de.szalkowski.adamsbatterysaver.SettingsManager;
import de.szalkowski.adamsbatterysaver.service.MainService;

public class IntervalSectionFragment extends Fragment {
    private SettingsManager settings;
    private View rootView;
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
    	rootView = inflater.inflate(R.layout.interval_frament, container, false);		

    	loadSettings();
		setupWidgets();
        
		return rootView;
    }

	private void setupWidgets() {
		setIntervalSeekBar();
        setStartStopToggle();
        setFromTime();
        setToTime();
	}

	private void setStartStopToggle() {
        boolean start_service = settings.getStartService();

		ToggleButton toggle = (ToggleButton)rootView.findViewById(R.id.toggleButton);
        toggle.setChecked(MainService.is_running);
        toggle.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Intent service = new Intent(getActivity(),MainService.class);
				if(isChecked) {
					getActivity().startService(service);
				} else {
					service.setAction(MainService.ACTION_DISABLE);
					getActivity().startService(service);
				}
			}
		});
        toggle.setChecked(start_service);
	}

	private void setIntervalSeekBar() {
		SeekBar interval = (SeekBar)rootView.findViewById(R.id.seekInterval);
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
				
				TextView text = (TextView)rootView.findViewById(R.id.sync_interval);
				text.setText(getActivity().getString(R.string.sync_interval, seekBar.getProgress()));
			}
		});
        interval.setProgress(settings.getInterval());
        
		TextView interval_text = (TextView)rootView.findViewById(R.id.sync_interval);
		interval_text.setText(getActivity().getString(R.string.sync_interval, interval.getProgress()));
	}

	private void loadSettings() {
        settings = SettingsManager.getSettingsManager(getActivity().getApplicationContext());
	}

	@Override
	public void onDestroyView() {
		settings.startTransaction();
		
        ToggleButton toggle = (ToggleButton)rootView.findViewById(R.id.toggleButton);
        settings.setStartService(toggle.isChecked());
        
        SeekBar interval = (SeekBar)rootView.findViewById(R.id.seekInterval);
        settings.setInterval(interval.getProgress());
        
        settings.commitTransaction();
        
        if(toggle.isChecked()) {
    		Intent service = new Intent(getActivity(),MainService.class);
    		service.setAction(MainService.ACTION_UPDATE);
        	getActivity().startService(service);
        }

        super.onDestroyView();
	}
	
	public void setFromTime() {
        int hour = settings.getNightModeFromHour();
        int minute = settings.getNightModeFromMinute();
        
        Calendar time = Calendar.getInstance();
        time.set(Calendar.HOUR_OF_DAY, hour);
        time.set(Calendar.MINUTE, minute);
        
        Button button = (Button)rootView.findViewById(R.id.buttonFrom);
        button.setText(this.getText(R.string.from) + " " + DateFormat.getTimeFormat(getActivity()).format(time.getTime()));
	}
	
	public void setToTime() {
        int hour = settings.getNightModeToHour();
        int minute = settings.getNightModeToMinute();
        
        Calendar time = Calendar.getInstance();
        time.set(Calendar.HOUR_OF_DAY, hour);
        time.set(Calendar.MINUTE, minute);
        
        Button button = (Button)rootView.findViewById(R.id.buttonTo);
        button.setText(this.getText(R.string.to) + " " + DateFormat.getTimeFormat(getActivity()).format(time.getTime()));
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
	    newFragment.show(getChildFragmentManager(), "fromPicker");
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
	    newFragment.show(getChildFragmentManager(), "toPicker");
	}
}
