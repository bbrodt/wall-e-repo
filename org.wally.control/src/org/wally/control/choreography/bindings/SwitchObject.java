package org.wally.control.choreography.bindings;

public interface SwitchObject {
	public String getName();
	public int getValue();
	public void setValue(int value);
	public void addListener(Object listener);
	public boolean toggle();
}
