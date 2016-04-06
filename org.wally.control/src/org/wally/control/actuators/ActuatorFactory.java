package org.wally.control.actuators;

import java.net.URI;
import java.net.URISyntaxException;


public class ActuatorFactory {

	public final static String SERVO_SCHEME = "servo";
	public final static String MOTOR_SCHEME = "motor";
	public final static String CHANNEL_QUERY = "channel";
	
	public static String getActuatorType(URI uri) {
		return uri.getScheme();
	}

	public static String getActuatorName(URI uri) throws URISyntaxException {
		String path = uri.getPath();
		if (path!=null)
			path = path.replaceAll("/", "");
		if (path==null||path.isEmpty())
			throw new URISyntaxException(path, "Path must not be empty");
		return path;
	}
	
	public static int getActuatorChannel(URI uri) throws URISyntaxException {
		String query = uri.getQuery();
		int channel = -1;
		// parse query: should be "channel=<number>"
		for (String queryElement : query.split("\\&")) {
			String queryParts[] = queryElement.split("=");
			if (queryParts.length==2) {
				if (CHANNEL_QUERY.equals(queryParts[0])) {
					channel = Integer.parseInt(queryParts[1]);
				}
				else
					throw new URISyntaxException(query, "Unknown query "+queryParts[0]+"="+queryParts[1]);
			}
			else if (queryParts.length==1) {
				throw new URISyntaxException(query, "Unknown query "+queryParts[0]);
			}
			else {
				throw new URISyntaxException(query, "Incorrect query syntax");
			}
		}
		return channel;
	}
	
	public static URI createURI(String actuatorType, String host, int port, String actuatorName, int channel) throws URISyntaxException {
		String uriString = actuatorType+":";
		if (host!=null && !host.isEmpty()) {
			uriString += "//"+host;
			if (port>0) {
				uriString += ":"+port;
			}
		}
		if (actuatorName!=null && !actuatorName.isEmpty()) {
			uriString += "/"+actuatorName.replaceAll(" ", "%20").replaceFirst("/", "");
		}
		if (channel>=0) {
			uriString += "?channel="+channel;
		}
		return new URI(uriString);
	}
	
	public static ActuatorDriver createDriver(URI uri) throws Exception {
//		System.out.println("Scheme="+uri.getScheme());
//		System.out.println("Host="+uri.getHost());
//		System.out.println("Port="+uri.getPort());
//		System.out.println("Path="+uri.getPath());
//		System.out.println("Query="+uri.getQuery());
		String actuatorType = getActuatorType(uri);
		String host = uri.getHost();
		int port = uri.getPort();
		String actuatorName = getActuatorName(uri);
		int channel = getActuatorChannel(uri);
		ActuatorDriver driver = null;
		if (SERVO_SCHEME.equals(actuatorType)) {
			if (host!=null && port>0) {
				// remote servo
				driver = new RemoteServoDriver(actuatorName, host, port, channel);
			}
			else {
				// local servo
				driver = new LocalServoDriver(actuatorName, channel);
			}
		}
		else if (MOTOR_SCHEME.equals(actuatorType)) {
			
		}
		return driver;
	}
}
