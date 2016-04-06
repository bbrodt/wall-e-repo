package org.wally.clientserver.server;

import java.net.Socket;

public class ClientInfo
{
	public LightweightServer server = null;
    public Socket socket = null;
    public ClientListener clientListener = null;
    public ClientSender clientSender = null;
    public String response = null;
}
