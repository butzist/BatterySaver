package de.szalkowski.adamsbatterysaver.devices;

public interface Powersaveable {
	abstract public void startPowersave() throws Exception;
	abstract public void stopPowersave() throws Exception;
	abstract public boolean isInPowersave() throws Exception;
	abstract public float getTraffic() throws Exception;
}
