package org.wally.clientserver;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.wally.clientserver.client.LightweightClient;
import org.wally.clientserver.server.ClientInfo;
import org.wally.clientserver.server.LightweightServer;


public class ClientServerTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length==0) {
			System.out.println("Usage: ClientServerTest [client|server]");
			System.exit(-1);
		}
		if (args[0].equals("client")) {
			LightweightClient lwc = new LightweightClient("localhost", 5555);
			lwc.run();
		}
		else {
			LightweightServer lws = new LightweightServer(5555) {
				@Override
				public void handleRequest(ClientInfo clientInfo, String request) {
					System.out.println("Request: " + request);
				}

				@Override
				public String prepareResponse(ClientInfo clientInfo, String request) {
					System.out.println("Response? ");
					BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
					String response;
					try {
						response = in.readLine();
					} catch (IOException e) {
						e.printStackTrace();
						return null;
					}
					return response;
				}
			};
			lws.run();
		}
	}
}
