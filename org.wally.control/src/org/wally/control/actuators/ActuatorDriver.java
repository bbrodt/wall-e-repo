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
		if (!listeners.contains(listener)) {
			// if the listener has a target, check if we already have
			// a listener with the same target and don't add if so
			Object target = listener.getTarget();
			if (target!=null) {
				for (ActuatorEventListener l : listeners) {
					if (l.getTarget()==target)
						return;
				}
			}
			listeners.add(listener);
		}
	}
	
	public void removeActuatorListener(ActuatorEventListener listener) {
		if (!listeners.contains(listener)) {
			// remove the listener based on its target
			// instead of the actual listener object
			Object target = listener.getTarget();
			if (target!=null) {
				for (ActuatorEventListener l : listeners) {
					if (l.getTarget()==target) {
						listeners.remove(l);
						return;
					}
				}
			}
		}
		else
			listeners.remove(listener);
	}

	public List<ActuatorEventListener> getListeners() {
		List<ActuatorEventListener> copy = new ArrayList<ActuatorEventListener>();
		copy.addAll(listeners);
		return copy;
	}

	protected void notifyListeners(ActuatorEvent event) {
		for (ActuatorEventListener listener : listeners) {
			listener.handleEvent(event);
		}
	}
}
