package de.szalkowski.adamsbatterysaver.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import de.szalkowski.adamsbatterysaver.R;
import de.szalkowski.adamsbatterysaver.SettingsManager;
import de.szalkowski.adamsbatterysaver.devices.PowerSaver;

public class DeviceMatrixSectionFragment extends Fragment {
    private SettingsManager settings;
    private View rootView;
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
    	rootView = inflater.inflate(R.layout.device_matrix_fragment, container, false);		

    	loadSettings();
		setupWidgets();
        
		return rootView;
    }

	private void setupWidgets() {
        setFlags();
	}

	private void loadSettings() {
        settings = SettingsManager.getSettingsManager(getActivity().getApplicationContext());
	}

	@Override
	public void onDestroyView() {
		settings.startTransaction();
        saveFlags();
        settings.commitTransaction();
        
        super.onDestroyView();
	}
	
	public void onCheckboxDisable(View v) {
		for(int i=0; i < CHECKBOX_IDS.length; ++i) {
			if(v.getId() == CHECKBOX_IDS[i][0]) {
				CheckBox check = (CheckBox)rootView.findViewById(CHECKBOX_IDS[i][0]);
				boolean enabled = check.isChecked();
				
				for(int j=1; j < CHECKBOX_IDS[i].length; ++j) {
					View vv = rootView.findViewById(CHECKBOX_IDS[i][j]);
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
				BluetoothDevice.DEFAULT_FLAGS,
				SyncPowerSaver.DEFAULT_FLAGS
		};
		
		for(int i=0; i<devices.length; i++) {
			// FIXME avoid direct access by name
			int flags = settings.getInteger(devices[i]+"_flags", default_flags[i]);
			for(int j=0; j<CHECKBOX_IDS[i].length; j++) {
				CheckBox check = (CheckBox)rootView.findViewById(CHECKBOX_IDS[i][j]);
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
				CheckBox check = (CheckBox)rootView.
						findViewById(CHECKBOX_IDS[i][j]);
				if(check.isChecked()) {
					flags += FLAG_VALUES[j];
				}
			}
			// FIXME avoid direct access by name
			settings.setValue(DEVICES[i]+"_flags", flags);
		}
	}
}
