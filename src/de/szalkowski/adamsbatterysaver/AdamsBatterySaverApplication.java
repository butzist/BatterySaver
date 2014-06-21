package de.szalkowski.adamsbatterysaver;

import java.util.Collection;

import android.content.Context;
import de.szalkowski.adamsbatterysaver.devices.PowerSaver;
import de.szalkowski.adamsbatterysaver.settings.SettingsManager;

public class AdamsBatterySaverApplication {
	protected AdamsBatterySaverApplication(Context context) {
		this.context = context.getApplicationContext();
		this.settings = getSettingsStorage(this.context);
		this.powersavers = getPowerSaverManager(this.context, settings); 
	}
	
	public static void initApplication(Context context) {
		if(instance == null) {
			instance = new AdamsBatterySaverApplication(context);
		}
	}
	
	public static SettingsManager getSettings() {
		return instance.settings;
	}

	public static Collection<PowerSaver> getPowerSavers() {
		return instance.powersavers.getPowerSavers();		
	}
	
	public static PowerSaverManager getPowerSaverManager() {
		return instance.powersavers;		
	}
	
	public static Context getContext() {
		return instance.context;
	}
	
	static protected SettingsManager getSettingsStorage(Context context) {
		return new SettingsManager(context);
	}

	static protected PowerSaverManager getPowerSaverManager(Context context, SettingsManager settings) {
		return new PowerSaverManager(context, settings);
	}

	static private AdamsBatterySaverApplication instance = null;
	
	private SettingsManager settings;
	private PowerSaverManager powersavers;
	private Context context;
}
