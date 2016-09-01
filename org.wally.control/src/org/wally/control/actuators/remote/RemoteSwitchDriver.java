package org.wally.control.actuators.remote;

import org.wally.control.actuators.ISwitchDriver;


public class RemoteSwitchDriver extends RemoteActuatorDriver implements ISwitchDriver {
	
	public RemoteSwitchDriver(String name, String host, int port, int channel) {
		super(name, host, port, channel);
	}

	@Override
	public DeviceType getDeviceType() {
		return DeviceType.SWITCH;
	}

	public boolean toggle() {
		int value = getValue();
		if (value>=0) {
			setValue(value==0 ? 1 : 0);
		}
		return value==0;
	}
}
