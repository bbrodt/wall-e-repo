package org.wally.clientserver;

public interface ClientServerConstants {
	
	public enum DeviceType {
		SERVO,
		SWITCH,
	};
	
	public static String CONTROLLER_SERVER = "wall-e-1";
	public static String REMOTE_1_SERVER = "localhost"; //"wall-e-2";
	public static String REMOTE_2_SERVER = "wall-e-3";
	public static int CONTROLLER_SERVER_PORT = 5555;
	public static int REMOTE_1_SERVER_PORT = 5556;
	public static int REMOTE_2_SERVER_PORT = 5557;

	public static String CONTROLLER_ADDRESS = "//" + CONTROLLER_SERVER + ":" + CONTROLLER_SERVER_PORT + "/";
	public static String REMOTE_1_ADDRESS = "//" + REMOTE_1_SERVER + ":" + REMOTE_1_SERVER_PORT + "/";
	public static String REMOTE_2_ADDRESS = "//" + REMOTE_2_SERVER + ":" + REMOTE_2_SERVER_PORT + "/";

	public final static String OPEN_COMMAND = "open";
	public final static String GET_COMMAND = "get";
	public final static String SET_COMMAND = "set";
	public final static String MIN_COMMAND = "min";
	public final static String MAX_COMMAND = "max";
	public final static String SPEED_COMMAND = "spd";
	public final static String ACCELERATION_COMMAND = "acc";
	public final static String CLOSE_COMMAND = "close";
	
	public final static String OK_RESPONSE = "OK";
	public final static String ERROR_RESPONSE = "ERROR";
}
