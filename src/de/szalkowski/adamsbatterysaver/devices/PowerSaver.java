package de.szalkowski.adamsbatterysaver.devices;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.szalkowski.adamsbatterysaver.Logger;
import de.szalkowski.adamsbatterysaver.R;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public abstract class PowerSaver {
	static public int FLAG_DISABLE_WITH_SCREEN = 0x1;
	static public int FLAG_DISABLE_WITH_POWER = 0x2;
	static public int FLAG_DISABLE_ON_INTERVAL = 0x4;
	static public int FLAG_DISABLED_WHILE_TRAFFIC = 0x8;
	static public int FLAG_SAVE_STATE = 0x10;
	static public int FLAG_DISABLE = 0x100;
	
	protected boolean isEnabled;
	protected boolean savedState;
	protected boolean unknownState;
	protected Context context;
	protected int flags;
	private String name;
	private Powersaveable saveable;

	protected PowerSaver(Context context, String name, Powersaveable saveable) {
		this.context = context;
		this.name = name;
		this.saveable = saveable;
		this.unknownState = false;
		this.savedState = false;
		
		try {
			this.isEnabled = this.isReallyEnabled();
			Logger.debug(name + " powersave was " + (this.isEnabled ? "enabled" : "disabled"));
		}
		catch(Exception e) {
			this.unknownState = true;
			Logger.error(name + " " + e.toString());
		}
		
		if((flags & FLAG_SAVE_STATE) != 0) {
			this.savedState = this.isEnabled;
		}
	}
	
	public void startPowersave() {
		if(this.flagDisable()) {
			return;
		}
		
		if(!this.isEnabled || this.unknownState) {
			this.isEnabled = true;
			
			if((this.flags & FLAG_SAVE_STATE) != 0 && !this.unknownState) {
				this.savedState = this.isReallyEnabled();
				Logger.debug(name + " powersave was " + (this.savedState ? "enabled" : "disabled"));
			}
			
			try {
				saveable.startPowersave();
				Logger.debug(name + " powersave enabled");
				this.unknownState = false;
			}
			catch(Exception e) {
				Logger.error(name + " " + e.toString());
			}
		}
	}

	public void stopPowersave() {
		if(flagDisable()) {
			return;
		}
		
		if(this.isEnabled || this.unknownState) {
			this.isEnabled = false;
			
			if(!this.savedState) {
				try {
					saveable.stopPowersave();
					Logger.debug(name + " powersave disabled");
					this.unknownState = false;
				}
				catch(Exception e) {
					Logger.error(name + " " + e.toString());
				}
			}
		}
	}
	
	public void setFlags(int flags) {
		if((this.flags & FLAG_SAVE_STATE) != (flags & FLAG_SAVE_STATE)) {
			if((flags & FLAG_SAVE_STATE) != 0 && !this.unknownState) {
				this.savedState = this.isReallyEnabled();
				Logger.debug(name + " powersave was " + (this.savedState ? "enabled" : "disabled"));
			} else if ((flags & FLAG_SAVE_STATE) == 0) {
				this.savedState = false;
			}
		}
		
		this.flags = flags;
	}
	
	public boolean isEnabled() {
		return this.isEnabled;
	}
	
	public String getName() {
		return this.name;
	}
	
	public boolean isReallyEnabled() {
		boolean enabled = this.isEnabled;
		try {
			enabled = saveable.isInPowersave();
		}
		catch(Exception e) {
			Logger.error(name + " " + e.toString());
			this.unknownState = true;
		}
		return enabled;
	}
	
	public boolean getSavedState() {
		return this.savedState;
	}
	
	public boolean hasTraffic() {
		boolean traffic = false;
		
		if(this.isWhitelisted())
			return true;
		
		try {
			traffic = saveable.hasTraffic();
		}
		catch(Exception e) {
			Logger.error(name + " " + e.toString());
		}
		
		return traffic;
	}

	@SuppressLint("NewApi")
	public boolean isWhitelisted() {
		if(android.os.Build.VERSION.SDK_INT >= 11) {
			ActivityManager am = (ActivityManager)this.context.getSystemService(Context.ACTIVITY_SERVICE);
			Set<String> whiteList = settings.getStringSet(this.name + "_whitelist", new HashSet<String>());
			if(whiteList.isEmpty())
				return false;
			
			boolean onlyTop = settings.getBoolean("only_top_task", context.getResources().getBoolean(R.bool.pref_only_top_task_default));
			List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(100);
			for (ActivityManager.RunningTaskInfo task : tasks) {
				if(task.numRunning < 1) continue;
				String currentTaskPackage = task.topActivity.getPackageName();
				
				if(whiteList.contains(currentTaskPackage)) {
					Logger.error(currentTaskPackage + " is on whitelist");
					return true;
				} else {
					if(onlyTop)
						return false;
				}
			}
		}
		
		return false;
	}
	
	public boolean flagDisableWithScreenSet() {
		return (this.flags & FLAG_DISABLE_WITH_SCREEN) != 0;
	}
	
	public boolean flagDisableWithPowerSet() {
		return (this.flags & FLAG_DISABLE_WITH_POWER) != 0;
	}
	
	public boolean flagDisableOnIntervalSet() {
		return (this.flags & FLAG_DISABLE_ON_INTERVAL) != 0;
	}
	
	public boolean flagSaveStateSet() {
		return (this.flags & FLAG_SAVE_STATE) != 0;
	}
	
	public boolean flagDisabledWhileTrafficSet() {
		return (this.flags & FLAG_DISABLED_WHILE_TRAFFIC) != 0;
	}
	
	public boolean flagDisable() {
		return (this.flags & FLAG_DISABLE) != 0;
	}
	
	public abstract void updateSettings();
}
