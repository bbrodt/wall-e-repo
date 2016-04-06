package org.wally.clientserver.server;

import java.net.*;
import java.io.*;

public abstract class LightweightServer {

	private int port;
	private ServerSocket serverSocket = null;
	
	public LightweightServer(int port) {
		this.port = port;
	}
	public void run() {
		// Open server socket for listening
		try {
			serverSocket = new ServerSocket(port);
			System.out.println("Server started on port " + port);
		} catch (IOException se) {
			System.err.println("Can not start listening on port " + port);
			se.printStackTrace();
			System.exit(-1);
		}

		// Accept and handle client connections
		while (true) {
			try {
				Socket socket = serverSocket.accept();
				ClientInfo clientInfo = new ClientInfo();
				clientInfo.server = this;
				clientInfo.socket = socket;
				ClientListener clientListener = new ClientListener(clientInfo);
				ClientSender clientSender = new ClientSender(clientInfo);
				clientInfo.clientListener = clientListener;
				clientInfo.clientSender = clientSender;
				clientListener.start();
				clientSender.start();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}

	public abstract void handleRequest(ClientInfo clientInfo, String request);
	public abstract String prepareResponse(ClientInfo clientInfo, String request);
}
