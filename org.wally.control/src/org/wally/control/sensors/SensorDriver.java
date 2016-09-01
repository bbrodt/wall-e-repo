package org.wally.control.sensors;

import java.util.ArrayList;
import java.util.List;


public abstract class SensorDriver implements ISensorDriver {

	protected String name;
	protected int channel;
	private List<SensorEventListener> listeners = new ArrayList<SensorEventListener>();

	public SensorDriver(String name, int channel) {
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
	
	public void addSensorListener(SensorEventListener listener) {
		if (!listeners.contains(listener))
			listeners.add(listener);
	}
	
	public void removeSensorListener(SensorEventListener listener) {
		listeners.remove(listener);
	}
	
	protected void notifyListeners(SensorEvent event) {
		for (SensorEventListener listener : listeners) {
			listener.handleEvent(event);
		}
	}
}
