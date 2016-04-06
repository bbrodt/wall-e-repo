package org.wally.clientserver.server;

import java.io.*;
import java.net.*;

public class ClientListener extends Thread {
	private ClientInfo clientInfo;
	private BufferedReader in;

	public ClientListener(ClientInfo clientInfo) throws IOException {
		this.clientInfo = clientInfo;
		Socket socket = clientInfo.socket;
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	}

	/**
	 * Until interrupted, reads messages from the client socket, forwards them
	 * to the server dispatcher's queue and notifies the server dispatcher.
	 */
	public void run() {
		try {
			while (!isInterrupted()) {
				String message = in.readLine();
				if (message == null) {
					System.out.println("Client at "+clientInfo.socket.getInetAddress().getHostName()+" disconnected");
					clientInfo.server.handleRequest(clientInfo, null);
					break;
				}
				System.out.println("Request from "+clientInfo.socket.getInetAddress().getHostName()
						+": "+message);
				clientInfo.server.handleRequest(clientInfo, message);
				message = clientInfo.server.prepareResponse(clientInfo, message);
				System.out.println("Response to "+clientInfo.socket.getInetAddress().getHostName()
						+": "+message);
				if (message!=null)
					clientInfo.clientSender.sendMessage(message);
			}
		} catch (IOException ioex) {
			clientInfo.server.handleRequest(clientInfo, null);
		}

		// Communication is broken. Interrupt both listener and sender threads
		clientInfo.clientSender.interrupt();
	}

}
