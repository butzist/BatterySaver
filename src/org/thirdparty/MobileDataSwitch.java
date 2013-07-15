/**
 * Based on code from Stackoverflow.com under CC BY-SA 3.0
 * Url: http://stackoverflow.com/questions/3644144/how-to-disable-mobile-data-on-android
 * By:  http://stackoverflow.com/users/410724/vladimir-sorokin
 * and  http://stackoverflow.com/users/167269/phanikumar
 * 
 * and
 * 
 * Url: http://stackoverflow.com/questions/12806709/android-how-to-tell-if-mobile-network-data-is-enabled-or-disabled-even-when
 * By:  http://stackoverflow.com/users/769265/david-wasser
 */

package org.thirdparty;

import java.lang.reflect.Method;

import android.content.Context;
import android.net.ConnectivityManager;
import android.telephony.TelephonyManager;
import android.util.Log;

public abstract class MobileDataSwitch {
	static final private String LOG = "org.thirdparty.MobileDataSwitch";

	public static void setMobileDataEnabled(Context context, boolean enabled) throws Exception {
		
		try {
		    ConnectivityManager conman = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		    Method setmeth = conman.getClass().getDeclaredMethod("setMobileDataEnabled", boolean.class);
		    setmeth.setAccessible(true);
		    setmeth.invoke(conman, enabled);
		}
		catch(Exception e) {
			Log.w(LOG, "setMobileDataEnabled failed -- trying alternative method: "+e.toString());
			
			try {    
				Method dataConnSwitchmethod;
				Class<?> telephonyManagerClass;
				Object ITelephonyStub;
				Class<?> ITelephonyClass;
				
				  TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

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
				Log.e(LOG, "setMobileDataEnabled failed: "+e.toString());
			}
		}
	}
	
	public static boolean getMobileDataEnabled(Context context) throws Exception {
		try {
		    ConnectivityManager conman = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	        Method method = conman.getClass().getDeclaredMethod("getMobileDataEnabled");
	        method.setAccessible(true);
	        boolean mobileDataEnabled = (Boolean)method.invoke(conman);

	        return mobileDataEnabled;
		}
		catch(Exception e) {
			Log.w(LOG,"getMobileDataEnabled failed -- trying alternative method: "+e.toString());
			TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			return telephonyManager.getDataState() != TelephonyManager.DATA_DISCONNECTED;
		}
	}
}
