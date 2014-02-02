package de.szalkowski.adamsbatterysaver.devices;

import java.util.List;
import java.util.Set;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import de.szalkowski.adamsbatterysaver.AdamsBatterySaverApplication;
import de.szalkowski.adamsbatterysaver.Logger;

public abstract class PowerSaver {
	static public int FLAG_DISABLE_WITH_SCREEN = 0x1;
	static public int FLAG_DISABLE_WITH_POWER = 0x2;
	static public int FLAG_DISABLE_ON_INTERVAL = 0x4;
	static public int FLAG_DISABLED_WHILE_TRAFFIC = 0x8;
	static public int FLAG_SAVE_STATE = 0x10;
	static public int FLAG_DISABLE = 0x100;
	
	private boolean inPowersave;
	private boolean savedState;
	private boolean unknownState;
	private Context context;
	private String name;
	private Powersaveable saveable;

	protected int flags;
	protected int trafficLimit;
	protected Set<String> whiteList;

	protected PowerSaver(Context context, String name, Powersaveable saveable) {
		this.context = context;
		this.name = name;
		this.saveable = saveable;
		this.unknownState = false;
		this.savedState = false;
		
		try {
			this.inPowersave = this.isReallyInPowersave();
			Logger.debug(name + " powersave was " + (this.inPowersave ? "enabled" : "disabled"));
		}
		catch(Exception e) {
			this.unknownState = true;
			Logger.error(name + " " + e.toString());
		}
		
		if((flags & FLAG_SAVE_STATE) != 0) {
			this.savedState = this.inPowersave;
		}
	}
	
	public void startPowersave() {
		if(this.getFlagDisable()) {
			return;
		}
		
		if(!this.inPowersave || this.unknownState) {
			this.inPowersave = true;
			
			if((this.flags & FLAG_SAVE_STATE) != 0 && !this.unknownState) {
				this.savedState = this.isReallyInPowersave();
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
		if(getFlagDisable()) {
			return;
		}
		
		if(this.inPowersave || this.unknownState) {
			this.inPowersave = false;
			
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
				this.savedState = this.isReallyInPowersave();
				Logger.debug(name + " powersave was " + (this.savedState ? "enabled" : "disabled"));
			} else if ((flags & FLAG_SAVE_STATE) == 0) {
				this.savedState = false;
			}
		}
		
		this.flags = flags;
	}
	
	public boolean isInPowersave() {
		return this.inPowersave;
	}
	
	public String getName() {
		return this.name;
	}
	
	public boolean isReallyInPowersave() {
		boolean enabled = this.inPowersave;
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
		boolean hasTraffic = false;
		float trafficValue;
		
		if(this.isWhitelisted())
			return true;
		
		try {
			trafficValue = saveable.getTraffic(); 
			hasTraffic =  trafficValue >= trafficLimit;
		}
		catch(Exception e) {
			Logger.error(name + " " + e.toString());
		}
		
		return hasTraffic;
	}

	@SuppressLint("NewApi")
	public boolean isWhitelisted() {
		if(android.os.Build.VERSION.SDK_INT >= 11) {
			ActivityManager am = (ActivityManager)this.context.getSystemService(Context.ACTIVITY_SERVICE);
			if(whiteList.isEmpty())
				return false;
			
			boolean onlyTop = AdamsBatterySaverApplication.getSettings().getWhitelistOnlyTopTask(); 
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
	
	public boolean getFlagDisableWithScreenSet() {
		return (this.flags & FLAG_DISABLE_WITH_SCREEN) != 0;
	}
	
	public boolean getFlagDisableWithPowerSet() {
		return (this.flags & FLAG_DISABLE_WITH_POWER) != 0;
	}
	
	public boolean getFlagDisableOnIntervalSet() {
		return (this.flags & FLAG_DISABLE_ON_INTERVAL) != 0;
	}
	
	public boolean getFlagSaveStateSet() {
		return (this.flags & FLAG_SAVE_STATE) != 0;
	}
	
	public boolean getFlagDisabledWhileTrafficSet() {
		return (this.flags & FLAG_DISABLED_WHILE_TRAFFIC) != 0;
	}
	
	public boolean getFlagDisable() {
		return (this.flags & FLAG_DISABLE) != 0;
	}	
	
	public void setFlagDisableWithScreenSet() {
		this.flags |= FLAG_DISABLE_WITH_SCREEN;
	}
	
	public void setFlagDisableWithPowerSet() {
		this.flags |= FLAG_DISABLE_WITH_POWER;
	}
	
	public void setFlagDisableOnIntervalSet() {
		this.flags |= FLAG_DISABLE_ON_INTERVAL;
	}
	
	public void setFlagSaveStateSet() {
		this.flags |= FLAG_SAVE_STATE;
	}
	
	public void setFlagDisabledWhileTrafficSet() {
		this.flags |= FLAG_DISABLED_WHILE_TRAFFIC;
	}
	
	public void setFlagDisable() {
		this.flags |= FLAG_DISABLE;
	}
	
	public void updateSettings() {
		readFlags();
		readWhiteList();
		readTrafficLimit();
	}
	
	public void saveSettings() {
		writeFlags();
		writeWhiteList();
		writeTrafficLimit();
	}
	
	// FIXME unused!
	abstract protected void readFlags();

	abstract protected void writeFlags();
	
	abstract protected void readWhiteList();
	
	abstract protected void writeWhiteList();
	
	abstract protected void readTrafficLimit();
	
	abstract protected void writeTrafficLimit();
	
	abstract public int getCapabilities();
}
