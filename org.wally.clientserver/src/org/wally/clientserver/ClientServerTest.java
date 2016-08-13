package org.wally.clientserver;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.wally.clientserver.client.ClientThread;
import org.wally.clientserver.client.IClientFactory;
import org.wally.clientserver.client.LightweightClient;
import org.wally.clientserver.client.ServerInfo;
import org.wally.clientserver.server.ClientInfo;
import org.wally.clientserver.server.LightweightServer;


public class ClientServerTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		if (args.length==0) {
			usage();
		}
		if (args[0].equals("client")) {
			IClientFactory factory = new IClientFactory() {
				@Override
				public ClientThread createClient(ServerInfo serverInfo) {
					return new ClientThread(serverInfo) {
						
						@Override
						public Object prepareRequest(ServerInfo serverInfo) {
							System.out.println("Request? ");
							BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
							String request;
							try {
								request = in.readLine();
							} catch (IOException e) {
								e.printStackTrace();
								return null;
							}
							return request;
						}
						
						@Override
						public void handleResponse(ServerInfo serverInfo, Object response) {
							System.out.println("Response: " + response);
						}
					};
				}
			};
			LightweightClient lwc = new LightweightClient(factory, "localhost", 5556);
			lwc.start();
		}
		else if (args[0].equals("server")) {
			LightweightServer lws = new LightweightServer(5556) {
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
		else
			usage();
	}
	
	private static void usage() {
		System.out.println("Usage: ClientServerTest [client|server]");
		System.exit(-1);
	}
}
