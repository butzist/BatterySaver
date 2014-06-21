package de.szalkowski.adamsbatterysaver.ui;

import java.util.Calendar;

import android.content.Intent;
import android.os.Bundle;
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
import android.widget.ToggleButton;
import de.szalkowski.adamsbatterysaver.AdamsBatterySaverApplication;
import de.szalkowski.adamsbatterysaver.R;
import de.szalkowski.adamsbatterysaver.service.MainService;
import de.szalkowski.adamsbatterysaver.settings.SettingsManager;

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
        boolean start_service = settings.start_service.get();

		ToggleButton toggle = (ToggleButton)rootView.findViewById(R.id.toggleButton);
        toggle.setChecked(start_service);
        toggle.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Intent service = new Intent(getActivity(),MainService.class);
				if(isChecked) {
					AdamsBatterySaverApplication.getSettings().start_service.set(true);
					getActivity().startService(service);
				} else {
					AdamsBatterySaverApplication.getSettings().start_service.set(false);
					service.setAction(MainService.ACTION_DISABLE);
					getActivity().startService(service);
				}
			}
		});
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
        interval.setProgress(settings.interval.get());
        
		TextView interval_text = (TextView)rootView.findViewById(R.id.sync_interval);
		interval_text.setText(getActivity().getString(R.string.sync_interval, interval.getProgress()));
	}

	private void loadSettings() {
        settings = AdamsBatterySaverApplication.getSettings();
	}

	@Override
	public void onDestroyView() {
		settings.startTransaction();
		
        ToggleButton toggle = (ToggleButton)rootView.findViewById(R.id.toggleButton);
        settings.start_service.set(toggle.isChecked());
        
        SeekBar interval = (SeekBar)rootView.findViewById(R.id.seekInterval);
        settings.interval.set(interval.getProgress());
        
        settings.commitTransaction();
        
        if(toggle.isChecked()) {
    		Intent service = new Intent(getActivity(),MainService.class);
    		service.setAction(MainService.ACTION_UPDATE);
        	getActivity().startService(service);
        }

        super.onDestroyView();
	}
	
	public void setFromTime() {
        int hour = settings.nightmode_from_hour.get();
        int minute = settings.nightmode_from_minute.get();
        
        Calendar time = Calendar.getInstance();
        time.set(Calendar.HOUR_OF_DAY, hour);
        time.set(Calendar.MINUTE, minute);
        
        Button button = (Button)rootView.findViewById(R.id.buttonFrom);
        button.setText(this.getText(R.string.from) + " " + DateFormat.getTimeFormat(getActivity()).format(time.getTime()));
        button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showFromTimePicker();
			}
		});
	}
	
	public void setToTime() {
        int hour = settings.nightmode_to_hour.get();
        int minute = settings.nightmode_to_minute.get();
        
        Calendar time = Calendar.getInstance();
        time.set(Calendar.HOUR_OF_DAY, hour);
        time.set(Calendar.MINUTE, minute);
        
        Button button = (Button)rootView.findViewById(R.id.buttonTo);
        button.setText(this.getText(R.string.to) + " " + DateFormat.getTimeFormat(getActivity()).format(time.getTime()));
        button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showToTimePicker();
			}
		});
	}
	
	public static class TimePickerFragment_From extends TimePickerFragment {

		@Override
		protected void setHour(SettingsManager settings, int hour) {
			settings.nightmode_from_hour.set(hour);
		}

		@Override
		protected void setMinute(SettingsManager settings, int minute) {
			settings.nightmode_from_minute.set(minute);
		}

		@Override
		protected int getHour(SettingsManager settings) {
			return settings.nightmode_from_hour.get();
		}

		@Override
		protected int getMinute(SettingsManager settings) {
			return settings.nightmode_from_minute.get();
		}
		
	};
	
	public void showFromTimePicker() {
		TimePickerFragment_From newFragment = new TimePickerFragment_From();
		newFragment.setParent(this);
	    newFragment.show(getChildFragmentManager(), "fromPicker");
	}
	
	public static class TimePickerFragment_To extends TimePickerFragment {

		@Override
		protected void setHour(SettingsManager settings, int hour) {
			settings.nightmode_to_hour.set(hour);
		}

		@Override
		protected void setMinute(SettingsManager settings, int minute) {
			settings.nightmode_to_minute.set(minute);
		}

		@Override
		protected int getHour(SettingsManager settings) {
			return settings.nightmode_to_hour.get();
		}

		@Override
		protected int getMinute(SettingsManager settings) {
			return settings.nightmode_to_minute.get();
		}	
	};

	public void showToTimePicker() {
		TimePickerFragment_To newFragment = new TimePickerFragment_To();
		newFragment.setParent(this);		
	    newFragment.show(getChildFragmentManager(), "toPicker");
	}
}
