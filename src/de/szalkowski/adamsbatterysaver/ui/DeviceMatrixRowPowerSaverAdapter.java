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
		
		ImageView icon = (ImageView)view.findViewById(R.id.ImageViewDeviceIcon);
		
		checkManual = (CheckBox)view.findViewById(R.id.CheckBoxManual);
		checkScreen = (CheckBox)view.findViewById(R.id.CheckBoxScreen);
		checkPower = (CheckBox)view.findViewById(R.id.CheckBoxPower);
		checkInterval = (CheckBox)view.findViewById(R.id.CheckBoxInterval);
		checkSave = (CheckBox)view.findViewById(R.id.CheckBoxSave);
		checkTraffic = (CheckBox)view.findViewById(R.id.CheckBoxTraffic);
		
		checkManual.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				setRowEnabled(!isChecked);
				
				if(isChecked) {
					powerSaver.setFlagDisable();					
				} else {
					powerSaver.;					
				}
			}
		});
		
		checkScreen.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				powerSaver.setFlagDisableWithScreenSet();
			}
		});
	}

	@Override
	public View getView() {
		return view;
	}
	
	private void setRowEnabled(boolean enabled) {
		checkScreen.setEnabled(enabled);
		checkPower.setEnabled(enabled);
		checkInterval.setEnabled(enabled);
		checkSave.setEnabled(enabled);
		checkTraffic.setEnabled(enabled);
	}
}
