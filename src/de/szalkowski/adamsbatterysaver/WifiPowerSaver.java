package de.szalkowski.adamsbatterysaver;

import android.content.Context;
import android.net.wifi.WifiManager;

public class WifiPowerSaver extends PowerSaver {
	static final public int DEFAULT_FLAGS = FLAG_ENABLE_WITH_SCREEN + FLAG_ENABLE_WITH_POWER + FLAG_ENABLE_ON_INTERVAL + FLAG_SAVE_STATE;

	public WifiPowerSaver(Context context, int flags) {
		super(context, "wifi", flags);
	}

	@Override
	protected void doStartPowersave() {
		WifiManager wifi = (WifiManager)this.context.getSystemService(Context.WIFI_SERVICE);
		wifi.setWifiEnabled(false);
	}

	@Override
	protected void doStopPowersave() {
		WifiManager wifi = (WifiManager)this.context.getSystemService(Context.WIFI_SERVICE);
		wifi.setWifiEnabled(true);
	}

	@Override
	protected boolean doIsEnabled() {
		WifiManager wifi = (WifiManager)this.context.getSystemService(Context.WIFI_SERVICE);
		return !wifi.isWifiEnabled();
	}

}
