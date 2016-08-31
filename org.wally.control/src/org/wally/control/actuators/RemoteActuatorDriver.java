package org.wally.control.actuators;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.wally.clientserver.ClientServerConstants;
import org.wally.control.actuators.ActuatorEvent.ActuatorEventType;

public abstract class RemoteActuatorDriver extends ActuatorDriver implements ClientServerConstants {
	
	private Socket socket;
	private String host;
	private int port;
	BufferedReader input;
	PrintWriter output;

	public RemoteActuatorDriver(String name, String host, int port, int channel) {
		super(name, channel);
		this.host = host;
		this.port = port;
	}

	public abstract DeviceType  getDeviceType();
	
	public void connect() {
		try {
			if (socket==null) {
				socket = new Socket(host, port);
				output = new PrintWriter(socket.getOutputStream(), true);
				input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				sendRequest(getDeviceType().toString()+","+OPEN_COMMAND+","+channel+","+name);
			}
		} catch (Exception e) {
			handleException(e);
		}
	}

	public void disconnect() {
		if (socket!=null) {
			try {
				sendRequest(getDeviceType().toString()+","+CLOSE_COMMAND+","+channel);
				socket.close();
			}
			catch (Exception e2) {
			}
			socket = null;
		}
		notifyListeners(new ActuatorEvent(ActuatorEventType.DISCONNECTED, this));
	}

	protected int sendRequest(String request) {
		if (socket==null) {
			notifyListeners(new ActuatorEvent(ActuatorEventType.ERROR, this, "Not connected, request: "+request));
			return -1;
		}
		try {
			output.println(request);
			String response = input.readLine();
			return handleResponse(request, response);
		} catch (Exception e) {
			handleException(e);
		}
		return -1;
	}
	
	protected int handleResponse(String request, String response) {
		if (response!=null) {
			String parts[] = response.split(",");
			if (parts.length==1) {
				if (OK_RESPONSE.equals(parts[0])) {
					notifyListeners(new ActuatorEvent(
							ActuatorEventType.CONNECTED, this));
				}
				else {
					try {
						return Integer.parseInt(parts[0]);
					}
					catch (Exception e) {
					}
					notifyListeners(new ActuatorEvent(
							ActuatorEventType.ERROR, this,
							"Unknown server response: " + parts[0] +
							"\nfor request: "+request));
				}
			}
			else if (parts.length>1) {
				if (ERROR_RESPONSE.equals(parts[0])) {
					notifyListeners(new ActuatorEvent(
							ActuatorEventType.ERROR, this,
							"Error response: " + parts[1] +
							"\nfor request: "+request));
				}
				else {
					notifyListeners(new ActuatorEvent(
							ActuatorEventType.ERROR, this,
							"Unknown server response: " + response +
							"\nfor request: "+request));
				}
			}
		}
		else {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			socket = null;
			notifyListeners(new ActuatorEvent(ActuatorEventType.ERROR,
					this, "Server disconnected after request: "+request));
		}
		return -1;
	}

	protected void handleException(Exception e) {
		e.printStackTrace();
		try {
			socket.close();
		}
		catch (Exception e2) {
		}
		socket = null;
		notifyListeners(new ActuatorEvent(ActuatorEventType.ERROR, this, e));
	}

	public void setValue(int value) {
		sendRequest(getDeviceType().toString()+","+SET_COMMAND+","+channel+","+value);
	}

	public int getValue() {
		return sendRequest(getDeviceType().toString()+","+GET_COMMAND+","+channel);
	}

	public void setProperty(String name, Object value) {
	}

	public Object getProperty(String name) {
		return null;
	}
}
