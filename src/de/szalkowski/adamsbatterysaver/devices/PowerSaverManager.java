package de.szalkowski.adamsbatterysaver.devices;

import java.util.Collection;
import java.util.Vector;

import de.szalkowski.adamsbatterysaver.AdamsBatterySaverApplication;

import android.content.Context;

public class PowerSaverManager {
	private static PowerSaver wifiPowerSaver; 
	private static PowerSaver mobilePowerSaver; 
	private static PowerSaver bluetoothPowerSaver; 
	private static PowerSaver globalsyncPowerSaver;
	
	public PowerSaverManager() {
		wifiPowerSaver = getWifiPowerSaver();
		mobilePowerSaver = getMobileDataPowerSaver();
		bluetoothPowerSaver = getBluetoothPowerSaver();
		globalsyncPowerSaver = getGlobalSyncPowerSaver();
	}
	
	public Collection<PowerSaver> getPowerSavers() {
		Vector<PowerSaver> powerSavers =  new Vector<PowerSaver>(4);
		powerSavers.add(wifiPowerSaver);
		powerSavers.add(mobilePowerSaver);
		powerSavers.add(bluetoothPowerSaver);
		powerSavers.add(globalsyncPowerSaver);
		
		return powerSavers;
	}
	
	protected PowerSaver getWifiPowerSaver() {
		return new PowerSaver(context, "wifi", new WifiDevice(context)) {
			@Override
			protected void readFlags() {
				flags = AdamsBatterySaverApplication.getSettings().getWifiFlags();
			}

			@Override
			protected void writeFlags() {
				AdamsBatterySaverApplication.getSettings().setWifiFlags(flags);
			}

			@Override
			protected void readWhiteList() {
				whiteList = AdamsBatterySaverApplication.getSettings().getWifiWhitelist();
			}

			@Override
			protected void writeWhiteList() {
				AdamsBatterySaverApplication.getSettings().setWifiWhitelist(whiteList);
			}

			@Override
			protected void readTrafficLimit() {
				trafficLimit = AdamsBatterySaverApplication.getSettings().getWifiTrafficLimit();
			}

			@Override
			protected void writeTrafficLimit() {
				AdamsBatterySaverApplication.getSettings().setWifiTrafficLimit(trafficLimit);
			}

			@Override
			public int getCapabilities() {
				return 0;
			}		
		};
	}
	
	protected PowerSaver getMobileDataPowerSaver() {
		return new PowerSaver(context, "data", new MobileDataDevice(context)) {
			@Override
			protected void readFlags() {
				flags = AdamsBatterySaverApplication.getSettings().getMobileDataFlags();
			}

			@Override
			protected void writeFlags() {
				AdamsBatterySaverApplication.getSettings().setMobileDataFlags(flags);
			}

			@Override
			protected void readWhiteList() {
				whiteList = AdamsBatterySaverApplication.getSettings().getMobileDataWhitelist();
			}

			@Override
			protected void writeWhiteList() {
				AdamsBatterySaverApplication.getSettings().setMobileDataWhitelist(whiteList);
			}

			@Override
			protected void readTrafficLimit() {
				trafficLimit = AdamsBatterySaverApplication.getSettings().getMobileDataTrafficLimit();
			}

			@Override
			protected void writeTrafficLimit() {
				AdamsBatterySaverApplication.getSettings().setMobileDataTrafficLimit(trafficLimit);
			}

			@Override
			public int getCapabilities() {
				return 0;
			}		
		};
	}
	
	protected PowerSaver getGlobalSyncPowerSaver() {
		return new PowerSaver(context, "wifi", new GlobalSyncSetting(context)) {
			@Override
			protected void readFlags() {
				flags = AdamsBatterySaverApplication.getSettings().getSyncFlags();
			}

			@Override
			protected void writeFlags() {
				AdamsBatterySaverApplication.getSettings().setSyncFlags(flags);
			}

			@Override
			protected void readWhiteList() {
				whiteList = AdamsBatterySaverApplication.getSettings().getSyncWhitelist();
			}

			@Override
			protected void writeWhiteList() {
				AdamsBatterySaverApplication.getSettings().setSyncWhitelist(whiteList);
			}

			@Override
			protected void readTrafficLimit() {
				trafficLimit = 1;
			}

			@Override
			protected void writeTrafficLimit() {	
			}

			@Override
			public int getCapabilities() {
				return 0;
			}
		};
	}
	
	protected PowerSaver getBluetoothPowerSaver() {
		return new PowerSaver(context, "wifi", new BluetoothDevice()) {

			@Override
			protected void readFlags() {
				flags = AdamsBatterySaverApplication.getSettings().getBluetoothFlags();
			}

			@Override
			protected void writeFlags() {
				AdamsBatterySaverApplication.getSettings().setBluetoothFlags(flags);
			}

			@Override
			protected void readWhiteList() {
				whiteList = AdamsBatterySaverApplication.getSettings().getBluetoothWhitelist();				
			}

			@Override
			protected void writeWhiteList() {
				AdamsBatterySaverApplication.getSettings().setBluetoothWhitelist(whiteList);				
			}

			@Override
			protected void readTrafficLimit() {
				trafficLimit = 1;
			}

			@Override
			protected void writeTrafficLimit() {
			}

			@Override
			public int getCapabilities() {
				return 0;
			}			
		};
	}
	
	private Context context;
}
