package de.szalkowski.adamsbatterysaver.devices;

import java.util.Collection;
import java.util.Vector;

import android.content.Context;
import de.szalkowski.adamsbatterysaver.R;
import de.szalkowski.adamsbatterysaver.settings.SettingsManager;

public class AllPowerSavers {
	private static PowerSaver wifiPowerSaver; 
	private static PowerSaver mobilePowerSaver; 
	private static PowerSaver bluetoothPowerSaver; 
	private static PowerSaver globalsyncPowerSaver;
	private Context context;
	private SettingsManager settings;
	
	protected AllPowerSavers(Context context, SettingsManager settings) {
		this.context = context;
		this.settings = settings;
		wifiPowerSaver = createWifiPowerSaver();
		mobilePowerSaver = createMobileDataPowerSaver();
		bluetoothPowerSaver = createBluetoothPowerSaver();
		globalsyncPowerSaver = createGlobalSyncPowerSaver();
	}
	
	public Collection<PowerSaver> getPowerSavers() {
		Vector<PowerSaver> powerSavers =  new Vector<PowerSaver>(4);
		powerSavers.add(wifiPowerSaver);
		powerSavers.add(mobilePowerSaver);
		powerSavers.add(bluetoothPowerSaver);
		powerSavers.add(globalsyncPowerSaver);
		
		return powerSavers;
	}
	
	public PowerSaver getWifiPowerSaver() {
		return wifiPowerSaver;		
	}
	
	public PowerSaver getMobilePowerSaver() {
		return mobilePowerSaver;		
	}
	
	public PowerSaver getBluetoothPowerSaver() {
		return bluetoothPowerSaver;		
	}
	
	public PowerSaver getGlobalSyncPowerSaver() {
		return globalsyncPowerSaver;		
	}
	
	protected PowerSaver createWifiPowerSaver() {
		return new PowerSaver(context, "wifi", R.drawable.device_access_network_wifi, new WifiDevice(context)) {
			@Override
			protected void readFlags() {
				flags = settings.wifi_flags.get();
			}

			@Override
			protected void writeFlags() {
				settings.wifi_flags.set(flags);
			}

			@Override
			protected void readWhiteList() {
				whiteList = settings.wifi_white_list.get();
			}

			@Override
			protected void writeWhiteList() {
				settings.wifi_white_list.set(whiteList);
			}

			@Override
			protected void readTrafficLimit() {
				trafficLimit = settings.wifi_traffic_limit.get();
			}

			@Override
			protected void writeTrafficLimit() {
				settings.wifi_traffic_limit.set(trafficLimit);
			}

			@Override
			public int getCapabilities() {
				return 0;
			}		
		};
	}
	
	protected PowerSaver createMobileDataPowerSaver() {
		return new PowerSaver(context, "data", R.drawable.device_access_network_cell, new MobileDataDevice(context)) {
			@Override
			protected void readFlags() {
				flags = settings.mobile_data_flags.get();
			}

			@Override
			protected void writeFlags() {
				settings.mobile_data_flags.set(flags);
			}

			@Override
			protected void readWhiteList() {
				whiteList = settings.mobile_data_white_list.get();
			}

			@Override
			protected void writeWhiteList() {
				settings.mobile_data_white_list.set(whiteList);
			}

			@Override
			protected void readTrafficLimit() {
				trafficLimit = settings.mobile_data_traffic_limit.get();
			}

			@Override
			protected void writeTrafficLimit() {
				settings.mobile_data_traffic_limit.set(trafficLimit);
			}

			@Override
			public int getCapabilities() {
				return 0;
			}		
		};
	}
	
	protected PowerSaver createGlobalSyncPowerSaver() {
		return new PowerSaver(context, "sync", R.drawable.navigation_refresh, new GlobalSyncSetting(context)) {
			@Override
			protected void readFlags() {
				flags = settings.sync_flags.get();
			}

			@Override
			protected void writeFlags() {
				settings.sync_flags.set(flags);
			}

			@Override
			protected void readWhiteList() {
				whiteList = settings.sync_white_list.get();
			}

			@Override
			protected void writeWhiteList() {
				settings.sync_white_list.set(whiteList);
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
	
	protected PowerSaver createBluetoothPowerSaver() {
		return new PowerSaver(context, "bluetooth", R.drawable.device_access_bluetooth, new BluetoothDevice()) {

			@Override
			protected void readFlags() {
				flags = settings.bluetooth_flags.get();
			}

			@Override
			protected void writeFlags() {
				settings.bluetooth_flags.set(flags);
			}

			@Override
			protected void readWhiteList() {
				whiteList = settings.bluetooth_white_list.get();				
			}

			@Override
			protected void writeWhiteList() {
				settings.bluetooth_white_list.set(whiteList);				
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
