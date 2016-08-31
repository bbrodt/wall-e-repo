package org.wally.clientserver.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public abstract class ClientThread extends Thread {

	protected ServerInfo serverInfo;
	protected BufferedReader input;
	protected PrintWriter output;

	public ClientThread(ServerInfo serverInfo) {
		this.serverInfo = serverInfo;
	}

	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		try {
//			InputStream is = serverInfo.socket.getInputStream();
//			OutputStream os = serverInfo.socket.getOutputStream();
			output = new PrintWriter(serverInfo.socket.getOutputStream(), true);
			input = new BufferedReader(new InputStreamReader(serverInfo.socket.getInputStream()));
			while (!isInterrupted()) {
				Object request = prepareRequest(serverInfo);
				if (request instanceof String) {
					output.println((String)request);
				}
				else if (request instanceof byte[])
;//					os.write((byte[])request);
				else
					throw new IllegalArgumentException("Expected a String or byte[] from prepareRequest()");

				Object response = prepareResponseBuffer(serverInfo, request);
				if (response instanceof String) {
					response = input.readLine();
				}
				else if (request instanceof byte[])
;//					is.read((byte[])request);
				else
					throw new IllegalArgumentException("Expected a String or byte[] from prepareResponseBuffer()");
				handleResponse(serverInfo, response);
			}
		} catch (IOException ioe) {
			// Communication is broken
		}
	}

	public abstract Object prepareRequest(ServerInfo serverInfo);
	public Object prepareResponseBuffer(ServerInfo serverInfo2, Object request) {
		return "";
	}
	public abstract void handleResponse(ServerInfo serverInfo, Object response);
}