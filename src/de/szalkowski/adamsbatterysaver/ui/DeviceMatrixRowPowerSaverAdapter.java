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
import de.szalkowski.adamsbatterysaver.ui.DeviceMatrixSectionFragment.DeviceMatrixRow;

public class DeviceMatrixRowPowerSaverAdapter implements DeviceMatrixRow {
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
		
		initView(context);
		
		initIcon();
		initCheckManual();
		initCheckScreen();
		initCheckPower();
		initCheckInterval();
		initCheckSave();
		initCheckTraffic();
	}

	private void initView(Context context) {
		LayoutInflater inflater = (LayoutInflater)context.getSystemService
			      (Context.LAYOUT_INFLATER_SERVICE);
		view = inflater.inflate(R.layout.device_matrix_row, null);
	}
	
	private void initIcon() {
		icon = (ImageView)view.findViewById(R.id.ImageViewDeviceIcon);
		icon.setImageResource(powerSaver.getIcon());
	}

	private void initCheckScreen() {
		checkScreen = (CheckBox)view.findViewById(R.id.CheckBoxScreen);
		checkScreen.setChecked(powerSaver.getFlagDisableWithScreen());
		checkScreen.setEnabled(!checkManual.isChecked());
		checkScreen.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked) {
					powerSaver.setFlagDisableWithScreen();
				} else {
					powerSaver.unsetFlagDisableWithScreen();
				}
			}
		});
	}
	
	private void initCheckPower() {
		checkPower = (CheckBox)view.findViewById(R.id.CheckBoxPower);
		checkPower.setChecked(powerSaver.getFlagDisableWithPower());
		checkPower.setEnabled(!checkManual.isChecked());
		checkPower.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked) {
					powerSaver.setFlagDisableWithPower();
				} else {
					powerSaver.unsetFlagDisableWithPower();
				}
			}
		});
	}
	
	private void initCheckInterval() {
		checkInterval = (CheckBox)view.findViewById(R.id.CheckBoxInterval);
		checkInterval.setChecked(powerSaver.getFlagDisableOnInterval());
		checkInterval.setEnabled(!checkManual.isChecked());
		checkInterval.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked) {
					powerSaver.setFlagDisableOnInterval();
				} else {
					powerSaver.unsetFlagDisableOnInterval();
				}
			}
		});
	}
	
	private void initCheckSave() {
		checkSave = (CheckBox)view.findViewById(R.id.CheckBoxSave);
		checkSave.setChecked(powerSaver.getFlagSaveState());
		checkSave.setEnabled(!checkManual.isChecked());
		checkSave.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked) {
					powerSaver.setFlagSaveState();
				} else {
					powerSaver.unsetFlagSaveState();
				}
			}
		});
	}
	
	private void initCheckTraffic() {
		checkTraffic = (CheckBox)view.findViewById(R.id.CheckBoxTraffic);
		checkTraffic.setChecked(powerSaver.getFlagDisabledWhileTraffic());
		checkTraffic.setEnabled(!checkManual.isChecked());
		checkTraffic.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked) {
					powerSaver.setFlagDisabledWhileTraffic();
				} else {
					powerSaver.unsetFlagDisabledWhileTraffic();
				}
			}
		});
	}
	
	private void initCheckManual() {
		checkManual = (CheckBox)view.findViewById(R.id.CheckBoxManual);
		checkManual.setChecked(powerSaver.getFlagDisable());
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
	
	public void update() {
		powerSaver.updateSettings();
	}
	
	public void save() {
		powerSaver.saveSettings();
	}
}
