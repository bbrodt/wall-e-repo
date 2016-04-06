package org.wally.control.actuators;

import java.io.IOException;


public interface IActuatorDriver {
	public final static int UI_MIN = 0;
	public final static int UI_MAX = 100;
	public final static int UI_TO_ACTUATOR_OFFSET = 50;

	public void setName(String name);
	public String getName();
	public void connect();
	public void disconnect();
	public void addActuatorListener(ActuatorEventListener listener);
	public void removeActuatorListener(ActuatorEventListener listener);
	public void setValue(int uiValue);
	public int toActuatorValue(int actuatorValue);
	public int toUiValue(int uiValue);
}
