package de.szalkowski.adamsbatterysaver;

import android.content.Context;
import de.szalkowski.adamsbatterysaver.devices.PowerSaverManager;

public class AdamsBatterySaverApplication {
	protected AdamsBatterySaverApplication(Context context) {
		this.context = context;
		this.settings = new SettingsManager(context);
		this.powersavers = new PowerSaverManager(); 
	}
	
	public static void initApplication(Context context) {
		instance = new AdamsBatterySaverApplication(context);
	}
	
	public static SettingsManager getSettings() {
		return instance.settings;		
	}

	public static PowerSaverManager getPowersavers() {
		return instance.powersavers;		
	}
	
	public static Context getContext() {
		return instance.context;
	}

	static private AdamsBatterySaverApplication instance;
	
	private SettingsManager settings;
	private PowerSaverManager powersavers;
	private Context context;
}
