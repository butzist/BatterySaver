package de.szalkowski.adamsbatterysaver.devices;

import java.util.Collection;
import java.util.Vector;

import android.content.Context;

public class PowerSaverManager {
	public Collection<PowerSaver> getPowerSavers() {
		Vector<PowerSaver> powerSavers =  new Vector<PowerSaver>(4);
		powerSavers.add(getWifiPowerSaver());
		powerSavers.add(getMobileDataPowerSaver());
		powerSavers.add(getBluetoothPowerSaver());
		powerSavers.add(getGlobalSyncPowerSaver());
		
		return powerSavers;
	}
	
	protected PowerSaver getWifiPowerSaver() {
		return new PowerSaver(context, "wifi", new WifiDevice(context));
	}
	
	protected PowerSaver getMobileDataPowerSaver() {
		return new PowerSaver(context, "data", new MobileDataDevice(context));
	}
	
	protected PowerSaver getGlobalSyncPowerSaver() {
		return new PowerSaver(context, "wifi", new GlobalSyncSetting(context));
	}
	
	protected PowerSaver getBluetoothPowerSaver() {
		return new PowerSaver(context, "wifi", new BluetoothDevice());
	}
	
	private Context context;
}
