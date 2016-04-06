package org.wally.clientserver.client;

import java.io.*;
import java.net.*;

public class LightweightClient {

	private String host;
	private int port;
	
	public LightweightClient(String host, int port) {
		this.host = host;
		this.port = port;
	}
	
	public void run() {
		BufferedReader in = null;
		PrintWriter out = null;
		Socket socket = null;
		try {
			// Connect to server
			socket = new Socket(host, port);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
			System.out.println("Connected to server " + host + ":" + port);
		} catch (IOException ioe) {
			System.err.println("Can not establish connection to " + host + ":" + port);
			ioe.printStackTrace();
			System.exit(-1);
		} finally {
			if (socket != null) {
				// socket.close();
			}
		}

		// Create and start Sender thread
		Sender sender = new Sender(out);
		sender.setDaemon(true);
		sender.start();

		try {
			// Read messages from the server and print them
			String message;
			while ((message = in.readLine()) != null) {
				System.out.println(message);
			}
		} catch (IOException ioe) {
			System.err.println("Connection to server broken.");
			ioe.printStackTrace();
		}

	}
}
