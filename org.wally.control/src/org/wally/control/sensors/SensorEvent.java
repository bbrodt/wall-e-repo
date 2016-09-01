package org.wally.control.sensors;

import org.wally.control.WallyEvent;


public class SensorEvent extends WallyEvent {
	public enum SensorEventType {
		CONNECTED,
		DISCONNECTED,
		ERROR,
	}
	
	public SensorEventType type;
	public ISensorDriver source;
	
	public SensorEvent(SensorEventType type, ISensorDriver source) {
		this.type = type;
		this.source = source;
	}
	
	public SensorEvent(SensorEventType type, ISensorDriver source, Exception exception) {
		this(type, source);
		this.exception = exception;
		if (exception!=null)
			this.message = exception.getMessage();
	}
	
	public SensorEvent(SensorEventType type, ISensorDriver source, String message) {
		this(type, source);
		this.message = message;
	}
}
