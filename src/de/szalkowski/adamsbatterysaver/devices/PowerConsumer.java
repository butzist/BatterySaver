package de.szalkowski.adamsbatterysaver.devices;

public abstract class PowerConsumer {
	abstract public void startPowersave() throws Exception;
	abstract public void stopPowersave() throws Exception;
	abstract public boolean isActive() throws Exception;
	abstract public boolean hasTraffic() throws Exception;
}
