package org.wally.remote;

import java.io.IOException;

import org.wally.clientserver.ClientServerConstants;
import org.wally.remote.actuators.ActuatorServer;

public class WallyServers {

	public static void main(String[] args) throws IOException {
		ActuatorServer actuatorServer = new ActuatorServer(ClientServerConstants.REMOTE_1_SERVER_PORT);
		actuatorServer.run();
	}
}
