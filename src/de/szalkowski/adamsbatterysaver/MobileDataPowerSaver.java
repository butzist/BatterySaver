package de.szalkowski.adamsbatterysaver;

import java.lang.reflect.Method;

import android.content.Context;
import android.net.ConnectivityManager;
import android.telephony.TelephonyManager;
import android.util.Log;

public class MobileDataPowerSaver extends PowerSaver {
	static private final String LOG = "de.szalkowski.adamsbatterysaver.MobileDataPowerSaver";

	public MobileDataPowerSaver(Context context, int flags) {
		super(context, "data", flags);
	}

	@Override
	protected void doStartPowersave() {
		this.setMobileDataEnabled(false);
	}

	@Override
	protected void doStopPowersave() {
		this.setMobileDataEnabled(true);
	}

	@Override
	protected boolean doIsEnabled() {
		TelephonyManager telephonyManager = (TelephonyManager) this.context.getSystemService(Context.TELEPHONY_SERVICE);
		return telephonyManager.getDataState() == TelephonyManager.DATA_DISCONNECTED;
	}

	protected void setMobileDataEnabled(boolean enabled) {
		// BASED ON http://stackoverflow.com/questions/3644144/how-to-disable-mobile-data-on-android
		
		try {
		    ConnectivityManager conman = (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
		    Method setmeth = conman.getClass().getMethod("setMobileDataEnabled", boolean.class);
		    setmeth.invoke(conman, enabled);
		}
		catch(Exception e) {
			Log.w(LOG, "setMobileDataEnabled failed -- trying alternative method");
			
			try {    
				Method dataConnSwitchmethod;
				Class<?> telephonyManagerClass;
				Object ITelephonyStub;
				Class<?> ITelephonyClass;
				
				  TelephonyManager telephonyManager = (TelephonyManager) this.context.getSystemService(Context.TELEPHONY_SERVICE);

				    telephonyManagerClass = Class.forName(telephonyManager.getClass().getName());
				    Method getITelephonyMethod = telephonyManagerClass.getDeclaredMethod("getITelephony");
				    getITelephonyMethod.setAccessible(true);
				    ITelephonyStub = getITelephonyMethod.invoke(telephonyManager);
				    ITelephonyClass = Class.forName(ITelephonyStub.getClass().getName());

				    if (enabled) {
				        dataConnSwitchmethod = ITelephonyClass
				                .getDeclaredMethod("enableDataConnectivity");
				    } else {
				        dataConnSwitchmethod = ITelephonyClass
				                .getDeclaredMethod("disableDataConnectivity");   
				    }
				    dataConnSwitchmethod.setAccessible(true);
				    dataConnSwitchmethod.invoke(ITelephonyStub);
			}
			catch(Exception ee) {
				Log.e(LOG, "setMobileDataEnabled failed");
			}
		}
	}
}
