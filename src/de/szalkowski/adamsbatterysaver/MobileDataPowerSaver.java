package de.szalkowski.adamsbatterysaver;

import java.lang.reflect.Method;

import org.thirdparty.MobileDataSwitch;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.TrafficStats;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;

public class MobileDataPowerSaver extends PowerSaver {
	static private final String LOG = "de.szalkowski.adamsbatterysaver.MobileDataPowerSaver";
	static final public int DEFAULT_FLAGS = FLAG_DISABLE_WITH_SCREEN + FLAG_DISABLE_WITH_POWER + FLAG_DISABLE_ON_INTERVAL + FLAG_SAVE_STATE;
	private long traffic;
	private long time;

	public MobileDataPowerSaver(Context context, int flags) {
		super(context, "data", flags);
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
		try {
		    ConnectivityManager conman = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		    Method setmeth = conman.getClass().getMethod("getMobileDataEnabled", boolean.class);
		    boolean enabled = (Boolean)setmeth.invoke(conman);
		    return !enabled;
		}
		catch(Exception e) {
			TelephonyManager telephonyManager = (TelephonyManager) this.context.getSystemService(Context.TELEPHONY_SERVICE);
			return telephonyManager.getDataState() == TelephonyManager.DATA_DISCONNECTED;
		}
	}

	@Override
	protected boolean doHasTraffic() throws Exception {
		TelephonyManager telephonyManager = (TelephonyManager) this.context.getSystemService(Context.TELEPHONY_SERVICE);
		if(telephonyManager.getDataState() == TelephonyManager.DATA_CONNECTED) {
			long time_diff = SystemClock.elapsedRealtime() - this.time;
			long traffic_diff = TrafficStats.getMobileRxBytes() + TrafficStats.getMobileTxBytes() - this.traffic;
			this.time = SystemClock.elapsedRealtime();
			this.traffic = TrafficStats.getMobileRxBytes() + TrafficStats.getMobileTxBytes();
			
	        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
			final long traffic_limit = (long)settings.getInt(MainActivity.SETTINGS_TRAFFIC_LIMIT, context.getResources().getInteger(R.integer.pref_traffic_limit_default));
			final double traffic_per_minute = traffic_diff/(time_diff/60000.0);
			Log.v(LOG,"mobile traffic: " + traffic_per_minute + " bytes / minute ("+ traffic_diff + "/" + time_diff/1000.0 + "s)");
			if(traffic_per_minute > traffic_limit) {
				return true;
			}
		}
		return false;
	}
	
	

}
