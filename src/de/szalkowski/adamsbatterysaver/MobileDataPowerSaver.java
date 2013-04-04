package de.szalkowski.adamsbatterysaver;

import org.thirdparty.MobileDataSwitch;
import android.content.Context;
import android.telephony.TelephonyManager;

public class MobileDataPowerSaver extends PowerSaver {
	//static private final String LOG = "de.szalkowski.adamsbatterysaver.MobileDataPowerSaver";
	static final public int DEFAULT_FLAGS = FLAG_ENABLE_WITH_SCREEN + FLAG_ENABLE_WITH_POWER + FLAG_ENABLE_ON_INTERVAL + FLAG_SAVE_STATE;

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
	}

	@Override
	protected boolean doIsEnabled() throws Exception {
		TelephonyManager telephonyManager = (TelephonyManager) this.context.getSystemService(Context.TELEPHONY_SERVICE);
		return telephonyManager.getDataState() == TelephonyManager.DATA_DISCONNECTED;
	}

}
