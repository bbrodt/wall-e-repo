package org.wally.control.choreography.bindings;

public interface ServoObject {
	public String getName();
	public int getValue();
	public void setValue(int value);
	public void addListener(Object listener);
	public boolean isReversed();
	public void setReversed(boolean reversed);
	public void setSpeed(int value);
	public void setAcceleration(int value);
	public int getMinValue();
	public int getMaxValue();
}
