package de.szalkowski.adamsbatterysaver;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;

public class BluetoothPowerSaver extends PowerSaver {
	static final public int DEFAULT_FLAGS = FLAG_ENABLE_WITH_POWER + FLAG_SAVE_STATE;

	public BluetoothPowerSaver(Context context, int flags) {
		super(context, "bluetooth", flags);
	}

	@Override
	protected void doStartPowersave() {
		BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();
		if(bluetooth != null) {
			bluetooth.disable();
		}
	}

	@Override
	protected void doStopPowersave() {
		BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();
		if(bluetooth != null) {
			bluetooth.enable();
		}
	}

	@Override
	protected boolean doIsEnabled() {
		BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();
		if(bluetooth == null) return true;
		
		int state = bluetooth.getState();
		return state == BluetoothAdapter.STATE_TURNING_OFF || state == BluetoothAdapter.STATE_OFF;
	}

}
