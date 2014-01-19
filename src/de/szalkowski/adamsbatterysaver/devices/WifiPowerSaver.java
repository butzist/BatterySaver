package de.szalkowski.adamsbatterysaver.devices;

import de.szalkowski.adamsbatterysaver.Logger;
import de.szalkowski.adamsbatterysaver.SettingsManager;
import android.content.Context;
import android.net.TrafficStats;
import android.net.wifi.WifiManager;
import android.os.SystemClock;

public class WifiPowerSaver extends PowerSaver {
	static final public int DEFAULT_FLAGS = FLAG_DISABLE_WITH_SCREEN + FLAG_DISABLE_WITH_POWER + FLAG_DISABLE_ON_INTERVAL + FLAG_SAVE_STATE;
	private long traffic;
	private long time;
	private SettingsManager settings;

	public WifiPowerSaver(Context context) {
		super(context, "wifi", DEFAULT_FLAGS);
        settings = SettingsManager.getSettingsManager(context.getApplicationContext());
        setFlags(settings.getWifiFlags(DEFAULT_FLAGS));
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
		this.time = SystemClock.elapsedRealtime();
		this.traffic = TrafficStats.getTotalRxBytes() + TrafficStats.getTotalTxBytes() - TrafficStats.getMobileRxBytes() - TrafficStats.getMobileTxBytes();
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

	@Override
	protected boolean doHasTraffic() throws Exception {
		WifiManager wifi = (WifiManager)this.context.getSystemService(Context.WIFI_SERVICE);
		if(wifi.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
			long time_diff = SystemClock.elapsedRealtime() - this.time;
			long traffic_diff = TrafficStats.getTotalRxBytes() + TrafficStats.getTotalTxBytes() - TrafficStats.getMobileRxBytes() - TrafficStats.getMobileTxBytes() - this.traffic;
			this.time = SystemClock.elapsedRealtime();
			this.traffic = TrafficStats.getTotalRxBytes() + TrafficStats.getTotalTxBytes() - TrafficStats.getMobileRxBytes() - TrafficStats.getMobileTxBytes();
			
			final long traffic_limit = (long)settings.getTrafficLimit();
			final double traffic_per_minute = traffic_diff/(time_diff/60000.0);
			Logger.verbose("wifi traffic: " + traffic_per_minute + " bytes / minute ("+ traffic_diff + "/" + time_diff/1000.0 + "s)");
			if(traffic_per_minute > traffic_limit) {
				return true;
			}
		}
		return false;
	}

	
	@Override
	protected void doUpdateSettings() throws Exception {
        setFlags(settings.getWifiFlags(DEFAULT_FLAGS));
	}
}
