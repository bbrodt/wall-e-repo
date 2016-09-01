package org.wally.control.actuators.remote;

import org.wally.control.actuators.IServoDriver;



public class RemoteServoDriver extends RemoteActuatorDriver implements IServoDriver {
	
	public RemoteServoDriver(String name, String host, int port, int channel) {
		super(name, host, port, channel);
	}

	public DeviceType getDeviceType() {
		return DeviceType.SERVO;
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

	public void setSpeed(int value) {
		sendRequest(getDeviceType().toString()+","+SPEED_COMMAND+","+channel+","+value);
	}

	public void setAcceleration(int value) {
		sendRequest(getDeviceType().toString()+","+ACCELERATION_COMMAND+","+channel+","+value);
	}

	public int getMinValue() {
		return sendRequest(getDeviceType().toString()+","+MIN_COMMAND+","+channel);
	}

	public int getMaxValue() {
		return sendRequest(getDeviceType().toString()+","+MAX_COMMAND+","+channel);
	}
}
