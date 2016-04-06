package org.wally.control.actuators;

import org.wally.control.actuators.ActuatorEvent.ActuatorEventType;

import com.pi4j.component.servo.ServoDriver;
import com.pi4j.component.servo.impl.RPIServoBlasterProvider;

public class LocalServoDriver extends ActuatorDriver {
	
	private static RPIServoBlasterProvider servoBlasterProvider;
	private ServoDriver driver;

	public LocalServoDriver(String name, int channel) {
		super(name, channel);
	}
	
	public void connect() {
		try {
			if (driver==null) {
				if (servoBlasterProvider==null) {
					servoBlasterProvider = new RPIServoBlasterProvider();
				}
				driver = servoBlasterProvider.getServoDriver(
						servoBlasterProvider.getDefinedServoPins().get(channel));
				notifyListeners(new ActuatorEvent(ActuatorEventType.CONNECTED, this));
			}
		} catch (Exception e) {
			e.printStackTrace();
			driver = null;
			notifyListeners(new ActuatorEvent(ActuatorEventType.ERROR, this, e));
		}
	}

	public void disconnect() {
		if (driver!=null) {
			driver = null;
			notifyListeners(new ActuatorEvent(ActuatorEventType.DISCONNECTED, this));
		}
	}
	
	public void setValue(int uiValue) {
		if (driver==null) {
			notifyListeners(new ActuatorEvent(ActuatorEventType.ERROR, this, "Not connected"));
		}
		else
			driver.setServoPulseWidth(toActuatorValue(uiValue));
	}
	
	public int toActuatorValue(int uiValue) {
		return uiValue * 2 + UI_TO_ACTUATOR_OFFSET;
	}
	
	public int toUiValue(int actuatorValue) {
		actuatorValue -= UI_TO_ACTUATOR_OFFSET;
		return actuatorValue <= 0 ? 0 : actuatorValue/2;
	}

}
