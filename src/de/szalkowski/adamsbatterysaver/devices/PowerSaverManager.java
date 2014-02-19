package de.szalkowski.adamsbatterysaver.devices;

import java.util.Collection;
import java.util.Vector;

import android.content.Context;
import de.szalkowski.adamsbatterysaver.R;
import de.szalkowski.adamsbatterysaver.SettingsStorage;

public class PowerSaverManager {
	private static PowerSaver wifiPowerSaver; 
	private static PowerSaver mobilePowerSaver; 
	private static PowerSaver bluetoothPowerSaver; 
	private static PowerSaver globalsyncPowerSaver;
	private Context context;
	private SettingsStorage settings;
	
	public PowerSaverManager(Context context, SettingsStorage settings) {
		this.context = context;
		this.settings = settings;
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
		return new PowerSaver(context, "wifi", R.drawable.device_access_network_wifi, new WifiDevice(context)) {
			@Override
			protected void readFlags() {
				flags = settings.getWifiFlags();
			}

			@Override
			protected void writeFlags() {
				settings.setWifiFlags(flags);
			}

			@Override
			protected void readWhiteList() {
				whiteList = settings.getWifiWhitelist();
			}

			@Override
			protected void writeWhiteList() {
				settings.setWifiWhitelist(whiteList);
			}

			@Override
			protected void readTrafficLimit() {
				trafficLimit = settings.getWifiTrafficLimit();
			}

			@Override
			protected void writeTrafficLimit() {
				settings.setWifiTrafficLimit(trafficLimit);
			}

			@Override
			public int getCapabilities() {
				return 0;
			}		
		};
	}
	
	protected PowerSaver getMobileDataPowerSaver() {
		return new PowerSaver(context, "data", R.drawable.device_access_network_cell, new MobileDataDevice(context)) {
			@Override
			protected void readFlags() {
				flags = settings.getMobileDataFlags();
			}

			@Override
			protected void writeFlags() {
				settings.setMobileDataFlags(flags);
			}

			@Override
			protected void readWhiteList() {
				whiteList = settings.getMobileDataWhitelist();
			}

			@Override
			protected void writeWhiteList() {
				settings.setMobileDataWhitelist(whiteList);
			}

			@Override
			protected void readTrafficLimit() {
				trafficLimit = settings.getMobileDataTrafficLimit();
			}

			@Override
			protected void writeTrafficLimit() {
				settings.setMobileDataTrafficLimit(trafficLimit);
			}

			@Override
			public int getCapabilities() {
				return 0;
			}		
		};
	}
	
	protected PowerSaver getGlobalSyncPowerSaver() {
		return new PowerSaver(context, "sync", R.drawable.navigation_refresh, new GlobalSyncSetting(context)) {
			@Override
			protected void readFlags() {
				flags = settings.getSyncFlags();
			}

			@Override
			protected void writeFlags() {
				settings.setSyncFlags(flags);
			}

			@Override
			protected void readWhiteList() {
				whiteList = settings.getSyncWhitelist();
			}

			@Override
			protected void writeWhiteList() {
				settings.setSyncWhitelist(whiteList);
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
		return new PowerSaver(context, "bluetooth", R.drawable.device_access_bluetooth, new BluetoothDevice()) {

			@Override
			protected void readFlags() {
				flags = settings.getBluetoothFlags();
			}

			@Override
			protected void writeFlags() {
				settings.setBluetoothFlags(flags);
			}

			@Override
			protected void readWhiteList() {
				whiteList = settings.getBluetoothWhitelist();				
			}

			@Override
			protected void writeWhiteList() {
				settings.setBluetoothWhitelist(whiteList);				
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
}
