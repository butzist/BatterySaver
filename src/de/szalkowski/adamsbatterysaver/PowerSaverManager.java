package de.szalkowski.adamsbatterysaver;

import android.content.Context;
import de.szalkowski.adamsbatterysaver.devices.AllPowerSavers;

public class PowerSaverManager extends AllPowerSavers {
	
	protected PowerSaverManager(Context context, SettingsStorage settings) {
		super(context, settings);
	}
}
