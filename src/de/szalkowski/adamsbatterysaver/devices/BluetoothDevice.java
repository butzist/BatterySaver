package de.szalkowski.adamsbatterysaver.devices;

import android.bluetooth.BluetoothAdapter;

public class BluetoothDevice implements Powersaveable {
	final private BluetoothAdapter bluetooth;

	public BluetoothDevice() {
		bluetooth = BluetoothAdapter.getDefaultAdapter();
	}

	@Override
	public void startPowersave() throws Exception {
		if(bluetooth != null) {
			bluetooth.disable();
		}
	}

	@Override
	public void stopPowersave() throws Exception {
		if(bluetooth != null) {
			bluetooth.enable();
		}
	}

	@Override
	public boolean isInPowersave() throws Exception {
		if(bluetooth == null) return true;
		
		int state = bluetooth.getState();
		return state == BluetoothAdapter.STATE_TURNING_OFF || state == BluetoothAdapter.STATE_OFF;
	}

	@Override
	public boolean hasTraffic() throws Exception {
		if(bluetooth.getState() == BluetoothAdapter.STATE_CONNECTED) {
			return true;
		}
		return false;
	}
}
