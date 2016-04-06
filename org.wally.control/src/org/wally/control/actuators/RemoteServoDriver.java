package org.wally.control.actuators;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.wally.control.actuators.ActuatorEvent.ActuatorEventType;


public class RemoteServoDriver extends ActuatorDriver {
	
	private Socket socket;
	private String host;
	private int port;
	BufferedReader input;
	PrintWriter output;
	
	public RemoteServoDriver(String name, String host, int port, int channel) {
		super(name, channel);
		this.host = host;
		this.port = port;
	}

	public void setValue(int uiValue) {
		if (socket==null) {
			notifyListeners(new ActuatorEvent(ActuatorEventType.ERROR, this, "Not connected"));
			return;
		}
		try {
			output.println("servo,set,"+channel+","+uiValue);
			String response = input.readLine();
			handleResponse(response);
		} catch (Exception e) {
			handleException(e);
		}
	}

	public void connect() {
		try {
			if (socket==null) {
				socket = new Socket(host, port);
				output = new PrintWriter(socket.getOutputStream(), true);
				input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				output.println("servo,open,"+channel+","+name);
				String response = input.readLine();
				handleResponse(response);
			}
		} catch (Exception e) {
			handleException(e);
		}
	}

	public void disconnect() {
		if (socket!=null) {
			try {
				socket.close();
			}
			catch (Exception e2) {
			}
			socket = null;
		}
		notifyListeners(new ActuatorEvent(ActuatorEventType.DISCONNECTED, this));
	}

	public int toActuatorValue(int actuatorValue) {
		return actuatorValue;
	}

	public int toUiValue(int uiValue) {
		return uiValue;
	}

	private void handleResponse(String response) {
		if (response!=null) {
			String parts[] = response.split(",");
			if (parts.length==1) {
				if ("OK".equals(parts[0])) {
					notifyListeners(new ActuatorEvent(
							ActuatorEventType.CONNECTED, this));
				}
				else {
					notifyListeners(new ActuatorEvent(
							ActuatorEventType.ERROR, this,
							"Unknown server response: " + parts[0]));
				}
			}
			else if (parts.length>1) {
				if ("ERROR".equals(parts[0])) {
					notifyListeners(new ActuatorEvent(
							ActuatorEventType.ERROR, this, parts[1]));
				}
				else {
					notifyListeners(new ActuatorEvent(
							ActuatorEventType.ERROR, this,
							"Unknown server response: " +response));
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
					this, "Server disconnected"));
		}
	}

	private void handleException(Exception e) {
		e.printStackTrace();
		try {
			socket.close();
		}
		catch (Exception e2) {
		}
		socket = null;
		notifyListeners(new ActuatorEvent(ActuatorEventType.ERROR, this, e));
	}
}
