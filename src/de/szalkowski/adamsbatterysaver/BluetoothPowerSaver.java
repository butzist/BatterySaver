package de.szalkowski.adamsbatterysaver;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;

public class BluetoothPowerSaver extends PowerSaver {
	static final public int DEFAULT_FLAGS = FLAG_DISABLE_WITH_POWER + FLAG_DISABLED_WHILE_TRAFFIC + FLAG_SAVE_STATE;

	public BluetoothPowerSaver(Context context, int flags) {
		super(context, "bluetooth", flags);
	}

	@Override
	protected void doStartPowersave() throws Exception {
		BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();
		if(bluetooth != null) {
			if((this.flags & FLAG_DISABLED_WHILE_TRAFFIC) != 0 && (bluetooth.getState() == BluetoothAdapter.STATE_CONNECTED)) {
				throw new Exception("still connected");
			}
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

}
