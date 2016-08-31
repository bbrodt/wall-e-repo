package org.wally.control.actuators;

public interface IServoDriver extends IActuatorDriver {
	public static String MAX_PROPERTY = "max";
	public static String MIN_PROPERTY = "min";
	public static String SPEED_PROPERTY = "speed";
	public static String ACCELERATION_PROPERTY = "accel";
	
	public int getMinValue();
	public int getMaxValue();
	public void setSpeed(int value);
	public void setAcceleration(int value);
}
