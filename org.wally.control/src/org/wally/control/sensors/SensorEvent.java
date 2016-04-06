package org.wally.control.sensors;


public class SensorEvent {
	public enum SensorEventType {
		CONNECTED,
		DISCONNECTED,
		ERROR,
	}
	
	public SensorEventType type;
	public ISensorDriver source;
	public Exception exception;
	public String message;
	
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
