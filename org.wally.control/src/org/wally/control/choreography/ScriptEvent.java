package org.wally.control.choreography;


public class ScriptEvent {
	public ScriptEventType type;
	public ScriptRunner source;
	public String msg;
	public Throwable error;
	
	public ScriptEvent(ScriptEventType type, ScriptRunner source) {
		this.type = type;
		this.source = source;
	}
	
	public ScriptEvent(ScriptRunner source, String msg) {
		this.type = ScriptEventType.MSG;
		this.source = source;
		this.msg = msg;
	}
	
	public ScriptEvent(ScriptRunner source, Throwable error) {
		this.type = ScriptEventType.ERROR;
		this.source = source;
		this.error = error;
	}
}