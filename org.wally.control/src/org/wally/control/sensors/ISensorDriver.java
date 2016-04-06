package org.wally.control.sensors;

public interface ISensorDriver {
	public void setName(String name);
	public String getName();
	public void connect();
	public void disconnect();

	public void addSensorListener(SensorEventListener listener);
	public void removeSensorListener(SensorEventListener listener);
}
