package de.szalkowski.adamsbatterysaver.devices;

import org.thirdparty.MobileDataSwitch;

import de.szalkowski.adamsbatterysaver.Logger;
import de.szalkowski.adamsbatterysaver.SettingsManager;
import android.content.Context;
import android.net.TrafficStats;
import android.os.SystemClock;
import android.telephony.TelephonyManager;

public class MobileDataPowerSaver extends PowerSaver {
	static final public int DEFAULT_FLAGS = FLAG_DISABLE_WITH_SCREEN + FLAG_DISABLE_WITH_POWER + FLAG_DISABLE_ON_INTERVAL + FLAG_SAVE_STATE;
	private long traffic;
	private long time;
	private SettingsManager settings;

	public MobileDataPowerSaver(Context context) {
		super(context, "data", DEFAULT_FLAGS);
        settings = SettingsManager.getSettingsManager(context.getApplicationContext());
        setFlags(settings.getMobileDataFlags(DEFAULT_FLAGS));
	}

	@Override
	protected void doStartPowersave() throws Exception {
		MobileDataSwitch.setMobileDataEnabled(context,false);
	}

	@Override
	protected void doStopPowersave() throws Exception {
		MobileDataSwitch.setMobileDataEnabled(context,true);
		this.time = SystemClock.elapsedRealtime();
		this.traffic =  TrafficStats.getMobileRxBytes() + TrafficStats.getMobileTxBytes();
	}

	@Override
	protected boolean doIsEnabled() throws Exception {
		return !MobileDataSwitch.getMobileDataEnabled(context);
	}

	@Override
	protected boolean doHasTraffic() throws Exception {
		TelephonyManager telephonyManager = (TelephonyManager) this.context.getSystemService(Context.TELEPHONY_SERVICE);
		if(telephonyManager.getDataState() == TelephonyManager.DATA_CONNECTED) {
			long time_diff = SystemClock.elapsedRealtime() - this.time;
			long traffic_diff = TrafficStats.getMobileRxBytes() + TrafficStats.getMobileTxBytes() - this.traffic;
			this.time = SystemClock.elapsedRealtime();
			this.traffic = TrafficStats.getMobileRxBytes() + TrafficStats.getMobileTxBytes();
			
			final long traffic_limit = (long)settings.getTrafficLimit();
			final double traffic_per_minute = traffic_diff/(time_diff/60000.0);
			Logger.verbose("mobile traffic: " + traffic_per_minute + " bytes / minute ("+ traffic_diff + "/" + time_diff/1000.0 + "s)");
			if(traffic_per_minute > traffic_limit) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	protected void doUpdateSettings() throws Exception {
        setFlags(settings.getMobileDataFlags(DEFAULT_FLAGS));
	}	
}
