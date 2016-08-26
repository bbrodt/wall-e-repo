package org.wally.control.choreography.bindings;

/*
 * Implements a simple console output interface.
 * Intended to be bound to Javascript Choreographies. 
 */
public interface ConsoleObject {
	public void print(String text);
	public void println(String text);
}
