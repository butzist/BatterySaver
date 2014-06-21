package de.szalkowski.adamsbatterysaver.ui;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TimePicker;
import de.szalkowski.adamsbatterysaver.AdamsBatterySaverApplication;
import de.szalkowski.adamsbatterysaver.settings.SettingsManager;

abstract class TimePickerFragment extends DialogFragment
implements TimePickerDialog.OnTimeSetListener {	
	private IntervalSectionFragment parent;
	
	public void setParent(IntervalSectionFragment parent)
	{
		this.parent = parent;		
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		SettingsManager settings = AdamsBatterySaverApplication.getSettings();
		int hour = this.getHour(settings);
		int minute = this.getMinute(settings);

		// Create a new instance of TimePickerDialog and return it
		return new TimePickerDialog(getActivity(), this, hour, minute,
				DateFormat.is24HourFormat(getActivity()));
	}

	public void onTimeSet(TimePicker view, int hour, int minute) {
		SettingsManager settings = AdamsBatterySaverApplication.getSettings();
		settings.startTransaction();
		this.setHour(settings,hour);
		this.setMinute(settings,minute);
		settings.commitTransaction();
		
		parent.setFromTime();
	}
	
	abstract protected void setHour(SettingsManager settings, int hour);
	abstract protected void setMinute(SettingsManager settings, int minute);
	abstract protected int getHour(SettingsManager settings);
	abstract protected int getMinute(SettingsManager settings);
}