package de.szalkowski.adamsbatterysaver.devices;

import de.szalkowski.adamsbatterysaver.SettingsManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;

public class BluetoothPowerSaver implements Powersaveable {
	private SettingsManager settings;

	public BluetoothPowerSaver(Context context) {
		super(context, "bluetooth", DEFAULT_FLAGS);
        settings = SettingsManager.getSettingsManager(context.getApplicationContext());
        setFlags(settings.getBluetoothFlags(DEFAULT_FLAGS));
	}

	@Override
	protected void doStartPowersave() throws Exception {
		BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();
		if(bluetooth != null) {
			bluetooth.disable();
		}
	}

	@Override
	protected void doStopPowersave() throws Exception {
		BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();
		if(bluetooth != null) {
			bluetooth.enable();
		}
	}

	@Override
	protected boolean doIsEnabled() throws Exception {
		BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();
		if(bluetooth == null) return true;
		
		int state = bluetooth.getState();
		return state == BluetoothAdapter.STATE_TURNING_OFF || state == BluetoothAdapter.STATE_OFF;
	}

	@Override
	protected boolean doHasTraffic() throws Exception {
		BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();
		if(bluetooth.getState() == BluetoothAdapter.STATE_CONNECTED) {
			return true;
		}
		return false;
	}
	
	@Override
	protected void doUpdateSettings() throws Exception {
        setFlags(settings.getBluetoothFlags(DEFAULT_FLAGS));
	}
}
