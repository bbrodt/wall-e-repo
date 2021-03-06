package org.wally.control.actuators;

import java.util.List;

public interface IActuatorDriver {
	public void setName(String name);
	public String getName();
	public int getChannel();
	public void connect();
	public void disconnect();
	public void addActuatorListener(ActuatorEventListener listener);
	public void removeActuatorListener(ActuatorEventListener listener);
	public List<ActuatorEventListener> getListeners();
	public void setValue(int value);
	public int getValue();
	public void setProperty(String name, Object value);
	public Object getProperty(String name);
}
