package org.wally.clientserver.client;


public interface IClientFactory {

	public ClientThread createClient(ServerInfo serverInfo);
}
