package org.wally.control.actuators;

import java.util.ArrayList;
import java.util.List;


public abstract class ActuatorDriver implements IActuatorDriver {

	protected String name;
	protected int channel;

	List<ActuatorEventListener> listeners = new ArrayList<ActuatorEventListener>();
	
	public ActuatorDriver(String name, int channel) {
		this.name = name;
		this.channel = channel;
	}

	public void setName(String name) {
		if (name!=null && !name.isEmpty())
			this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public int getChannel() {
		return channel;
	}

	public void setProperty(String name, Object value) {
	}

	public Object getProperty(String name) {
		return null;
	}

	public void addActuatorListener(ActuatorEventListener listener) {
		if (!listeners.contains(listener))
			listeners.add(listener);
	}
	
	public void removeActuatorListener(ActuatorEventListener listener) {
		listeners.remove(listener);
	}
	
	protected void notifyListeners(ActuatorEvent event) {
		for (ActuatorEventListener listener : listeners) {
			listener.handleEvent(event);
		}
	}
}
