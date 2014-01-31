package de.szalkowski.adamsbatterysaver.devices;

import java.util.Collection;
import java.util.Vector;

import android.content.Context;

public class PowerSaverManager {
	public Collection<PowerSaver> getPowerSavers() {
		return new Vector<PowerSaver>();
	}
	
	private PowerSaver getWifiPowerSaver() {
		return new PowerSaver(context, "wifi", new WifiDevice(context)) {
			@Override
			public void updateSettings() {
			}
			
			@Override
			public int getDefaultFlags() {
				return FLAG_DISABLE_WITH_SCREEN + FLAG_DISABLE_WITH_POWER + FLAG_DISABLE_ON_INTERVAL + FLAG_SAVE_STATE;
			}
		};
	}
	
	private PowerSaver getMobileDataPowerSaver() {
		return new PowerSaver(context, "data", new MobileDataDevice(context)) {
			@Override
			public void updateSettings() {
			}
		};
	}
	
	private PowerSaver getWifiPowerSaver() {
		return new PowerSaver(context, "wifi", new WifiDevice(context)) {
			@Override
			public void updateSettings() {
			}
			
			@Override
			public int getDefaultFlags() {
				return FLAG_DISABLE_WITH_SCREEN + FLAG_DISABLE_WITH_POWER + FLAG_DISABLE_ON_INTERVAL + FLAG_SAVE_STATE;
			}
		};
	}
	
	private PowerSaver getWifiPowerSaver() {
		return new PowerSaver(context, "wifi", new WifiDevice(context)) {
			@Override
			public void updateSettings() {
			}
			
			@Override
			public int getDefaultFlags() {
				return FLAG_DISABLE_WITH_SCREEN + FLAG_DISABLE_WITH_POWER + FLAG_DISABLE_ON_INTERVAL + FLAG_SAVE_STATE;
			}
		};
	}
	
	protected Context context;
}
