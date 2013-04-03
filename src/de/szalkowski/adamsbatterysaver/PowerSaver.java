package de.szalkowski.adamsbatterysaver;

import android.content.Context;
import android.util.Log;

public abstract class PowerSaver {
	static private final String LOG = "de.szalkowski.adamsbatterysaver.PowerSaver";
	
	static public int FLAG_ENABLE_WITH_SCREEN = 0x1;
	static public int FLAG_ENABLE_WITH_POWER = 0x2;
	static public int FLAG_ENABLE_ON_INTERVAL = 0x4;
	static public int FLAG_ENABLED_WHILE_TRAFFIC = 0x8;
	static public int FLAG_SAVE_STATE = 0x10;
	
	static final public int DEFAULT_FLAGS = FLAG_ENABLE_WITH_SCREEN + FLAG_ENABLE_WITH_POWER + FLAG_ENABLE_ON_INTERVAL + FLAG_ENABLED_WHILE_TRAFFIC + FLAG_SAVE_STATE;

	protected boolean isEnabled;
	protected boolean savedState;
	protected boolean unknownState;
	protected Context context;
	protected int flags;
	private String name;

	public PowerSaver(Context context, String name, int flags) {
		this.context = context;
		this.name = name;
		this.flags = flags;
		this.unknownState = false;
		this.savedState = false;
		
		try {
			this.isEnabled = this.isReallyEnabled();
			Log.d(LOG, name + " powersave was " + (this.isEnabled ? "enabled" : "disabled"));
		}
		catch(Exception e) {
			this.unknownState = true;
			Log.e(LOG, name + " " + e.toString());
		}
		
		if((flags & FLAG_SAVE_STATE) != 0) {
			this.savedState = this.isEnabled;
		}
	}
	
	public void startPowersave() {
		if(!this.isEnabled || this.unknownState) {
			this.isEnabled = true;
			
			if((this.flags & FLAG_SAVE_STATE) != 0 && !this.unknownState) {
				this.savedState = this.isReallyEnabled();
				Log.d(LOG, name + " powersave was " + (this.savedState ? "enabled" : "disabled"));
			}
			
			try {
				this.doStartPowersave();
				Log.d(LOG, name + " powersave enabled");
				this.unknownState = false;
			}
			catch(Exception e) {
				Log.e(LOG, name + " " + e.toString());
			}
		}
	}

	public void stopPowersave() {
		if(this.isEnabled || this.unknownState) {
			this.isEnabled = false;
			
			if(!this.savedState) {
				try {
					this.doStopPowersave();
					Log.d(LOG, name + " powersave disabled");
					this.unknownState = false;
				}
				catch(Exception e) {
					Log.e(LOG, name + " " + e.toString());
				}
			}
		}
	}
	
	public void setFlags(int flags) {
		this.flags = flags;
	}
	
	public boolean isEnabled() {
		return this.isEnabled;
	}
	
	public boolean isReallyEnabled() {
		boolean enabled = this.isEnabled;
		try {
			enabled = this.doIsEnabled();
		}
		catch(Exception e) {
			Log.e(LOG,name + " " + e.toString());
			this.unknownState = true;
		}
		return enabled;
	}
	
	public boolean getSavedState() {
		return this.savedState;
	}
	
	public boolean flagEnableWithScreenSet() {
		return (this.flags & FLAG_ENABLE_WITH_SCREEN) != 0;
	}
	
	public boolean flagEnableWithPowerSet() {
		return (this.flags & FLAG_ENABLE_WITH_POWER) != 0;
	}
	
	public boolean flagEnableOnIntervalSet() {
		return (this.flags & FLAG_ENABLE_ON_INTERVAL) != 0;
	}
	
	public boolean flagSaveStateSet() {
		return (this.flags & FLAG_SAVE_STATE) != 0;
	}
	
	abstract protected void doStartPowersave() throws Exception;
	abstract protected void doStopPowersave() throws Exception;
	abstract protected boolean doIsEnabled() throws Exception;

}
