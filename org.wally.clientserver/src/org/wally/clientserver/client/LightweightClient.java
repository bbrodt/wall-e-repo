package org.wally.clientserver.client;

import java.io.IOException;
import java.net.Socket;

public class LightweightClient {

	private IClientFactory factory;
	private String host;
	private int port;
	
	public LightweightClient(IClientFactory factory, String host, int port) {
		this.factory = factory;
		this.host = host;
		this.port = port;
	}
	
	public void start() {
		Socket socket = null;
		try {
			// Connect to server
			socket = new Socket(host, port);
			System.out.println("Connected to server " + host + ":" + port);
		} catch (IOException ioe) {
			System.err.println("Can not establish connection to " + host + ":" + port);
			ioe.printStackTrace();
			System.exit(-1);
		}

		// Create and start Client Implementation thread
		ServerInfo serverInfo = new ServerInfo();
		serverInfo.socket = socket;
		serverInfo.client = this;
		ClientThread clientImpl = factory.createClient(serverInfo);
		clientImpl.setDaemon(true);
		clientImpl.start();
		
		while (!clientImpl.isInterrupted())
			;
	}
}
