package org.wally.control;

public abstract class WallyEventListener<T extends WallyEvent> {
	
	public abstract void handleEvent(T event);
	
	public Object getTarget() {
		return null;
	}
}
