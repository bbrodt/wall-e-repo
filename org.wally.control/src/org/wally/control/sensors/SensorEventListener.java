package org.wally.control.sensors;

import org.wally.control.WallyEventListener;

public abstract class SensorEventListener extends WallyEventListener<SensorEvent> {

	public abstract void handleEvent(SensorEvent event);
}
