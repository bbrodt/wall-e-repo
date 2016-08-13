package org.wally.control;

import org.wally.clientserver.ClientServerConstants;
import org.wally.clientserver.server.ClientInfo;
import org.wally.clientserver.server.LightweightServer;

public class SensorServer extends LightweightServer implements ClientServerConstants {

	public SensorServer(int port) {
		super(port);
	}

	@Override
	public void handleRequest(ClientInfo clientInfo, String request) {
	}

	@Override
	public String prepareResponse(ClientInfo clientInfo, String request) {
		return null;
	}

}
