package org.wally.control.actuators;

import org.wally.control.actuators.ActuatorEvent.ActuatorEventType;

public class LocalServoDriver extends ActuatorDriver {
	
	private static MaestroServoProvider servoProvider;
	private MaestroServoDriver driver;

	public LocalServoDriver(String name, int channel) {
		super(name, channel);
	}
	
	public void connect() {
		try {
			if (driver==null) {
				if (servoProvider==null) {
					servoProvider = new MaestroServoProvider();
				}
				driver = (MaestroServoDriver) servoProvider.getServoDriver(
						servoProvider.getDefinedServoPins().get(channel));
				
				driver.setAcceleration(20);
				
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
	
	public void setValue(int value) {
		if (driver==null) {
			notifyListeners(new ActuatorEvent(ActuatorEventType.ERROR, this, "Not connected"));
		}
		else
			driver.setServoPulseWidth(value);
	}
	
	public int getValue() {
		if (driver==null) {
			notifyListeners(new ActuatorEvent(ActuatorEventType.ERROR, this, "Not connected"));
		}
		else
			return driver.getServoPulseWidth();
		return -1;
	}

	public int getMinValue() {
		if (driver==null) {
			notifyListeners(new ActuatorEvent(ActuatorEventType.ERROR, this, "Not connected"));
		}
		else
			return driver.getMinValue();
		return -1;
	}

	public int getMaxValue() {
		if (driver==null) {
			notifyListeners(new ActuatorEvent(ActuatorEventType.ERROR, this, "Not connected"));
		}
		else
			return driver.getMaxValue();
		return -1;
	}

	public void setSpeed(int value) {
		if (driver==null) {
			notifyListeners(new ActuatorEvent(ActuatorEventType.ERROR, this, "Not connected"));
		}
		else
			driver.setSpeed(value);
	}

	public void setAcceleration(int value) {
		if (driver==null) {
			notifyListeners(new ActuatorEvent(ActuatorEventType.ERROR, this, "Not connected"));
		}
		else
			driver.setAcceleration(value);
	}

}
