package org.wally.control.actuators;



public interface IActuatorDriver {
	public void setName(String name);
	public String getName();
	public void connect();
	public void disconnect();
	public void addActuatorListener(ActuatorEventListener listener);
	public void removeActuatorListener(ActuatorEventListener listener);
	public void setValue(int value);
	public void setSpeed(int value);
	public void setAcceleration(int value);
	public int getMinValue();
	public int getMaxValue();
}
