package de.szalkowski.adamsbatterysaver;

import android.content.Context;
import android.net.wifi.WifiManager;

public class WifiPowerSaver extends PowerSaver {
	//static private final String LOG = "de.szalkowski.adamsbatterysaver.WifiPowerSaver";
	static final public int DEFAULT_FLAGS = FLAG_DISABLE_WITH_SCREEN + FLAG_DISABLE_WITH_POWER + FLAG_DISABLE_ON_INTERVAL + FLAG_SAVE_STATE;

	public WifiPowerSaver(Context context, int flags) {
		super(context, "wifi", flags);
	}

	@Override
	protected void doStartPowersave() throws Exception {
		WifiManager wifi = (WifiManager)this.context.getSystemService(Context.WIFI_SERVICE);
		wifi.setWifiEnabled(false);
	}

	@Override
	protected void doStopPowersave() throws Exception {
		WifiManager wifi = (WifiManager)this.context.getSystemService(Context.WIFI_SERVICE);
		wifi.setWifiEnabled(true);
	}

	@Override
	protected boolean doIsEnabled() throws Exception {
		WifiManager wifi = (WifiManager)this.context.getSystemService(Context.WIFI_SERVICE);
		int state = wifi.getWifiState();
		if(state == WifiManager.WIFI_STATE_DISABLED || state == WifiManager.WIFI_STATE_DISABLING) {
			return true;
		} else if(state == WifiManager.WIFI_STATE_ENABLED || state == WifiManager.WIFI_STATE_ENABLING) {
			return false;
		} else {
			throw new Exception("unknown state: " + state);
		}
	}

}
