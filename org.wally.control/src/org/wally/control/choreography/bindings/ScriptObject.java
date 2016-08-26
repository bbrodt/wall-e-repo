package org.wally.control.choreography.bindings;

/*
 * Implements a scripting interface with various utility methods.
 * Intended to be bound to Javascript Choreographies. 
 */
public interface ScriptObject {
	public void run(String fileName);
	public String getScriptName();
	public void stop();
	public void sleep(int milliseconds);
}
