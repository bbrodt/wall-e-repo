package org.wally.control.actuators.local;

import org.wally.control.actuators.ActuatorDriver;
import org.wally.control.actuators.ActuatorEvent;
import org.wally.control.actuators.ActuatorEvent.ActuatorEventType;
import org.wally.control.actuators.IServoDriver;
import org.wally.control.drivers.MaestroServoDriver;
import org.wally.control.drivers.MaestroServoProvider;
import org.wally.control.drivers.MaestroServoProvider.InterfaceType;

public class LocalServoDriver extends ActuatorDriver implements IServoDriver{
	
	private static MaestroServoProvider provider;
	private MaestroServoDriver driver;

	public LocalServoDriver(String name, int channel) {
		super(name, channel);
	}
	
	public void connect() {
		try {
			if (driver==null) {
				if (provider==null) {
					provider = new MaestroServoProvider(InterfaceType.USB);
				}
				driver = (MaestroServoDriver) provider.getServoDriver(channel);
				
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

	public void setProperty(String name, Object value) {
		if (SPEED_PROPERTY.equals(name)) {
			setSpeed(((Integer)value).intValue());
		}
		if (ACCELERATION_PROPERTY.equals(name)) {
			setAcceleration(((Integer)value).intValue());
		}
	}

	public Object getProperty(String name) {
		if (MIN_PROPERTY.equals(name)) {
			return new Integer(getMinValue());
		}
		if (MAX_PROPERTY.equals(name)) {
			return new Integer(getMaxValue());
		}
		return null;
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
