package org.wally.remote.actuators;

import org.wally.clientserver.ClientServerConstants;
import org.wally.clientserver.server.ClientInfo;
import org.wally.clientserver.server.LightweightServer;
import org.wally.control.actuators.LocalServoDriver;

public class ActuatorServer extends LightweightServer implements ClientServerConstants {

	private AllocatedServoDriver servos[] = new AllocatedServoDriver[16];
	
	public ActuatorServer(int port) {
		super(port);
	}

	@Override
	public void handleRequest(ClientInfo clientInfo, String request) {
		try {
			if (request==null) {
				// client disconnected
				// deallocate all servos assigned to this client
				for (int channel=0; channel<servos.length; ++channel) {
					AllocatedServoDriver sd = servos[channel];
					if (sd!=null) {
						if (sd.getClientInfo().socket==clientInfo.socket) {
							servos[channel].disconnect();
							servos[channel] = null;
						}
					}
				}
				return;
			}
			
			
			String parts[] = request.split(",");
			if (SERVO_REQUEST.equals(parts[0])) {
				// servo,<requestType>,<channelNumber>
				String requestType = parts[1];
				int channel = Integer.parseInt(parts[2]);
				if (OPEN_REQUEST.equals(requestType)) {
					// servo,open,<channelNumber>,<name>
					String name = parts[3];
					if (servos[channel]==null) {
						servos[channel] = new AllocatedServoDriver(clientInfo,name,channel);
						servos[channel].connect();
					}
					else {
						clientInfo.response = "ERROR,Servo channel "
								+ channel
								+ " already opened by "
								+ servos[channel].getClientInfo().socket.getInetAddress().getHostName();
					}
				}
				else if (GET_REQUEST.equals(requestType)) {
					
				}
				else if (SET_REQUEST.equals(requestType)) {
					// servo,set,<channelNumber>,<value>
					if (servos[channel]!=null) {
						if (servos[channel].getClientInfo().socket==clientInfo.socket) {
							int value = Integer.parseInt(parts[3]);
							servos[channel].setValue(value);
						}
						else {
							clientInfo.response = "ERROR,Servo channel "
									+ channel
									+ " opened by another client at "
									+ servos[channel].getClientInfo().socket.getInetAddress().getHostName();
						}
					}
					else {
						clientInfo.response = "ERROR,Servo channel "
								+ channel
								+ " is not open";
					}
				}
				else if (CLOSE_REQUEST.equals(requestType)) {
					// servo,close,<channelNumber>
					if (servos[channel]!=null) {
						if (servos[channel].getClientInfo().socket==clientInfo.socket) {
							servos[channel].disconnect();
							servos[channel] = null;
						}
						else {
							clientInfo.response = "ERROR,Servo channel "
									+ channel
									+ " opened by another client at "
									+ servos[channel].getClientInfo().socket.getInetAddress().getHostName();
						}
					}
					else {
						clientInfo.response = "ERROR,Servo channel "
								+ channel
								+ " is not open";
					}
				}
				else {
					clientInfo.response = "ERROR,Unknown request " + request;
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			clientInfo.response = "ERROR,Exception: " + e.getMessage();
		}
	}

	@Override
	public String prepareResponse(ClientInfo clientInfo, String request) {
		String response = clientInfo.response;
		clientInfo.response = null;
		if (response!=null)
			return response;
		return "OK";
	}

	private static class AllocatedServoDriver extends LocalServoDriver {

		private ClientInfo clientInfo;
		
		public AllocatedServoDriver(ClientInfo clientInfo, String name, int channel) {
			super(name, channel);
			this.clientInfo = clientInfo;
		}
		
		ClientInfo getClientInfo() {
			return clientInfo;
		}
	}
}
