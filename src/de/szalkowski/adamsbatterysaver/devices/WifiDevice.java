package de.szalkowski.adamsbatterysaver.devices;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.TrafficStats;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.SystemClock;
import de.szalkowski.adamsbatterysaver.Logger;

public class WifiDevice implements Powersaveable {
	private long traffic;
	private long time;
	private Context context;
	private WifiManager wifiManager;
	
	public WifiDevice(Context context) {
		this.context = context;
		this.wifiManager = (WifiManager)this.context.getSystemService(Context.WIFI_SERVICE);
	}

	@Override
	public void startPowersave() throws Exception {
		wifiManager.setWifiEnabled(false);
	}

	@Override
	public void stopPowersave() throws Exception {
		wifiManager.setWifiEnabled(true);
		recordTrafficStats();
	}
	
	private void recordTrafficStats() {
		time = SystemClock.elapsedRealtime();
		traffic = getWifiTraffic();
	}

	@TargetApi(Build.VERSION_CODES.FROYO)
	private long getWifiTraffic() {
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
			return TrafficStats.getTotalRxBytes() + TrafficStats.getTotalTxBytes() - TrafficStats.getMobileRxBytes() - TrafficStats.getMobileTxBytes();
		} else {
			return 0;
		}
	}

	@Override
	public boolean isInPowersave() throws Exception {
		int state = wifiManager.getWifiState();
		if(state == WifiManager.WIFI_STATE_DISABLED || state == WifiManager.WIFI_STATE_DISABLING) {
			return true;
		} else if(state == WifiManager.WIFI_STATE_ENABLED || state == WifiManager.WIFI_STATE_ENABLING) {
			return false;
		} else {
			throw new Exception("unknown state: " + state);
		}
	}

	@Override
	public float getTraffic() throws Exception {
		if(wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
			float trafficPerMinute = getTrafficPerMinute();
			recordTrafficStats();
			
			Logger.verbose("wifi traffic: " + trafficPerMinute + " kiB/min");
			return trafficPerMinute;
		}
		return 0;
	}

	private float getTrafficPerMinute() {
		long time_diff = SystemClock.elapsedRealtime() - this.time;
		long traffic_diff = getWifiTraffic() - this.traffic;
		final float traffic_per_minute = (traffic_diff/1024f)/(time_diff/60000.0f);
		return traffic_per_minute;
	}
}
