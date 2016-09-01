package org.wally.control.actuators;

import org.wally.control.WallyEventListener;

public abstract class ActuatorEventListener extends WallyEventListener<ActuatorEvent> {

	public abstract void handleEvent(ActuatorEvent event);
}
