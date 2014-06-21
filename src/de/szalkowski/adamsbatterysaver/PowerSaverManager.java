package de.szalkowski.adamsbatterysaver;

import android.content.Context;
import de.szalkowski.adamsbatterysaver.devices.AllPowerSavers;
import de.szalkowski.adamsbatterysaver.settings.SettingsManager;

public class PowerSaverManager extends AllPowerSavers {
	
	protected PowerSaverManager(Context context, SettingsManager settings) {
		super(context, settings);
	}
}
