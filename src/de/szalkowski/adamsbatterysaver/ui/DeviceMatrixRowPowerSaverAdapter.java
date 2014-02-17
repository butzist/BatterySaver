package de.szalkowski.adamsbatterysaver.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import de.szalkowski.adamsbatterysaver.R;
import de.szalkowski.adamsbatterysaver.devices.PowerSaver;
import de.szalkowski.adamsbatterysaver.ui.DeviceMatrixSectionFragment.DeviceMatrixRowAdapter;

public class DeviceMatrixRowPowerSaverAdapter implements DeviceMatrixRowAdapter {
	private View view;
	private ImageView icon;
	private CheckBox checkManual;
	private CheckBox checkScreen;
	private CheckBox checkPower;
	private CheckBox checkInterval;
	private CheckBox checkSave;
	private CheckBox checkTraffic;
	
	private PowerSaver powerSaver;
	
	public DeviceMatrixRowPowerSaverAdapter(PowerSaver powersaver, Context context) {	
		this.powerSaver = powersaver;
		
		LayoutInflater inflater = (LayoutInflater)context.getSystemService
			      (Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.device_matrix_row, null);
		
		icon = (ImageView)view.findViewById(R.id.ImageViewDeviceIcon);
		
		initCheckManual();
		initCheckScreen();
		initCheckPower();
		initCheckInterval();
		initCheckSave();
		initCheckTraffic();
	}

	private void initCheckScreen() {
		checkScreen = (CheckBox)view.findViewById(R.id.CheckBoxScreen);
		checkScreen.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked) {
					powerSaver.setFlagDisableWithScreenSet();
				} else {
					powerSaver.unsetFlagDisableWithScreenSet();
				}
			}
		});
	}
	
	private void initCheckPower() {
		checkPower = (CheckBox)view.findViewById(R.id.CheckBoxPower);
		checkPower.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked) {
					powerSaver.setFlagDisableWithPowerSet();
				} else {
					powerSaver.unsetFlagDisableWithPowerSet();
				}
			}
		});
	}
	
	private void initCheckInterval() {
		checkInterval = (CheckBox)view.findViewById(R.id.CheckBoxInterval);
		checkInterval.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked) {
					powerSaver.setFlagDisableOnIntervalSet();
				} else {
					powerSaver.unsetFlagDisableOnIntervalSet();
				}
			}
		});
	}
	
	private void initCheckSave() {
		checkSave = (CheckBox)view.findViewById(R.id.CheckBoxSave);
		checkSave.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked) {
					powerSaver.setFlagSaveStateSet();
				} else {
					powerSaver.unsetFlagSaveStateSet();
				}
			}
		});
	}
	
	private void initCheckTraffic() {
		checkTraffic = (CheckBox)view.findViewById(R.id.CheckBoxTraffic);
		checkTraffic.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked) {
					powerSaver.setFlagDisabledWhileTrafficSet();
				} else {
					powerSaver.unsetFlagDisabledWhileTrafficSet();
				}
			}
		});
	}
	
	private void initCheckManual() {
		checkManual = (CheckBox)view.findViewById(R.id.CheckBoxManual);
		checkManual.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				setCheckManual(isChecked);
			}
		});
		
	}
	
	private void setCheckManual(boolean isChecked) {
		if(isChecked) {
			setRowDisabled();
			powerSaver.setFlagDisable();					
		} else {
			setRowEnabled();
			powerSaver.unsetFlagDisable();					
		}		
	}

	@Override
	public View getView() {
		return view;
	}
	
	private void setRowDisabled() {
		setRowEnabledState(false);
	}
	
	private void setRowEnabled() {
		setRowEnabledState(true);
	}
	
	private void setRowEnabledState(boolean enabled) {
		checkScreen.setEnabled(enabled);
		checkPower.setEnabled(enabled);
		checkInterval.setEnabled(enabled);
		checkSave.setEnabled(enabled);
		checkTraffic.setEnabled(enabled);
	}
}
