package de.szalkowski.adamsbatterysaver.devices;

import org.thirdparty.MobileDataSwitch;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.TrafficStats;
import android.os.Build;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import de.szalkowski.adamsbatterysaver.Logger;

public class MobileDataDevice implements Powersaveable {
	private long traffic;
	private long time;
	final private Context context;
	final private TelephonyManager telephonyManager;

	public MobileDataDevice(Context context) {
		this.context = context;
		telephonyManager = (TelephonyManager) this.context.getSystemService(Context.TELEPHONY_SERVICE);
	}

	@Override
	public void startPowersave() throws Exception {
		MobileDataSwitch.setMobileDataEnabled(context,false);
	}

	@Override
	public void stopPowersave() throws Exception {
		MobileDataSwitch.setMobileDataEnabled(context,true);
		recordTrafficStats();
	}

	private void recordTrafficStats() {
		time = SystemClock.elapsedRealtime();
		traffic = getMobileTraffic();
	}

	@TargetApi(Build.VERSION_CODES.FROYO)
	private long getMobileTraffic() {
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
			return TrafficStats.getMobileRxBytes() + TrafficStats.getMobileTxBytes();
		} else {
			return 0;
		}
	}
	@Override
	public boolean isInPowersave() throws Exception {
		return !MobileDataSwitch.getMobileDataEnabled(context);
	}

	@Override
	public float getTraffic() throws Exception {
		if(telephonyManager.getDataState() == TelephonyManager.DATA_CONNECTED) {
			float trafficPerMinute = getTrafficPerMinute();
			recordTrafficStats();
			
			Logger.verbose("mobile data traffic: " + trafficPerMinute + "  kiB/min");
			return trafficPerMinute;
		}
		return 0;
	}

	private float getTrafficPerMinute() {
		long time_diff = SystemClock.elapsedRealtime() - this.time;
		long traffic_diff = getMobileTraffic() - this.traffic;
		final float traffic_per_minute = (traffic_diff/1024f)/(time_diff/60000.0f);
		return traffic_per_minute;
	}
}
