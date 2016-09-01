package org.wally.control.ui;

import java.util.List;

import org.wally.control.actuators.ActuatorEventListener;
import org.wally.control.actuators.IActuatorDriver;

public interface IActuatorUI {

	public IActuatorDriver getDriver();
	public void setValue(int value);
	public int getValue();
	public void connect();
	public void disconnect();
	public void addListener(ActuatorEventListener listener);
	public void removeListener(ActuatorEventListener listener);
	public List<ActuatorEventListener> getListeners();
}
