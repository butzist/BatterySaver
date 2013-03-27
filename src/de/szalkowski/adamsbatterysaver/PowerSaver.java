package de.szalkowski.adamsbatterysaver;

import android.content.Context;
import android.util.Log;

public abstract class PowerSaver {
	static private final String LOG = "de.szalkowski.adamsbatterysaver.PowerSaver";
	
	static public int FLAG_ENABLE_WITH_SCREEN = 0x1;
	static public int FLAG_ENABLE_WITH_POWER = 0x2;
	static public int FLAG_ENABLE_ON_INTERVAL = 0x4;
	static public int FLAG_SAVE_STATE = 0x10;

	protected boolean isEnabled;
	protected boolean savedState;
	protected Context context;
	protected int flags;
	private String name;

	public PowerSaver(Context context, String name, int flags) {
		this.context = context;
		this.name = name;
		this.flags = flags;
		this.isEnabled = this.isReallyEnabled();
		Log.d(LOG, name + " powersave was " + (this.isEnabled ? "enabled" : "disabled"));
		
		if((flags & FLAG_SAVE_STATE) == 0) {
			this.savedState = false;
		} else {
			this.savedState = this.isEnabled;
		}
	}
	
	public void startPowersave() {
		if(!this.isEnabled) {
			this.isEnabled = true;
			
			if((this.flags & FLAG_SAVE_STATE) != 0) {
				this.savedState = this.isReallyEnabled();
				Log.d(LOG, name + " powersave was " + (this.savedState ? "enabled" : "disabled"));
			}
			this.doStartPowersave();
			Log.d(LOG, name + " powersave enabled");
		}
	}

	public void stopPowersave() {
		if(this.isEnabled) {
			this.isEnabled = false;
			
			if(!this.savedState) {
				this.doStopPowersave();
				Log.d(LOG, name + " powersave disabled");
			}
		}
	}
	
	public boolean isEnabled() {
		return this.isEnabled;
	}
	
	public boolean isReallyEnabled() {
		return this.doIsEnabled();
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
	
	abstract protected void doStartPowersave();
	abstract protected void doStopPowersave();
	abstract protected boolean doIsEnabled();

}
