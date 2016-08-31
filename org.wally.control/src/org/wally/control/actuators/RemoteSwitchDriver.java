package org.wally.control.actuators;

import org.wally.clientserver.ClientServerConstants;

public class RemoteSwitchDriver extends RemoteActuatorDriver implements
		ClientServerConstants {
	
	public RemoteSwitchDriver(String name, String host, int port, int channel) {
		super(name, host, port, channel);
	}

	@Override
	public DeviceType getDeviceType() {
		return DeviceType.SWITCH;
	}
}
