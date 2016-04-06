package org.wally.control.actuators;


public class ActuatorEvent {
	public enum ActuatorEventType {
		CONNECTED,
		DISCONNECTED,
		ERROR,
	}
	
	public ActuatorEventType type;
	public IActuatorDriver source;
	public Exception exception;
	public String message;
	
	public ActuatorEvent(ActuatorEventType type, IActuatorDriver source) {
		this.type = type;
		this.source = source;
	}
	
	public ActuatorEvent(ActuatorEventType type, IActuatorDriver source, Exception exception) {
		this(type, source);
		this.exception = exception;
		if (exception!=null)
			this.message = exception.getMessage();
	}
	
	public ActuatorEvent(ActuatorEventType type, IActuatorDriver source, String message) {
		this(type, source);
		this.message = message;
	}
}
