package org.wally.control.actuators;

public interface IActuatorUI {

	public IActuatorDriver getDriver();
	public void setValue(int value);
	public int getValue();
	public void connect();
	public void disconnect();
	public void addListener(ActuatorEventListener listener);
	public void removeListener(ActuatorEventListener listener);
}
