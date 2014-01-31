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
	private long trafficLimit;
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
	public boolean hasTraffic() throws Exception {
		if(wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
			double trafficPerMinute = getTrafficPerMinute();
			recordTrafficStats();
			
			Logger.verbose("wifi traffic: " + trafficPerMinute + " bytes / minute");
			if(trafficPerMinute > trafficLimit) {
				return true;
			}
		}
		return false;
	}

	private double getTrafficPerMinute() {
		long time_diff = SystemClock.elapsedRealtime() - this.time;
		long traffic_diff = getWifiTraffic() - this.traffic;
		final double traffic_per_minute = traffic_diff/(time_diff/60000.0);
		return traffic_per_minute;
	}
	
	public void setTrafficLimit(long trafficLimit) {
		this.trafficLimit = trafficLimit;	
	}
}
