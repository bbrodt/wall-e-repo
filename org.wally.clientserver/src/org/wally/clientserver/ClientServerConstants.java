package org.wally.clientserver;

public interface ClientServerConstants {
	
	public static String CONTROLLER_SERVER = "wall-e-1";
	public static String REMOTE_1_SERVER = "localhost"; //"wall-e-2";
	public static String REMOTE_2_SERVER = "wall-e-3";
	public static int CONTROLLER_SERVER_PORT = 5555;
	public static int REMOTE_1_SERVER_PORT = 5556;
	public static int REMOTE_2_SERVER_PORT = 5557;

	public static String CONTROLLER_ADDRESS = "//" + CONTROLLER_SERVER + ":" + CONTROLLER_SERVER_PORT + "/";
	public static String REMOTE_1_ADDRESS = "//" + REMOTE_1_SERVER + ":" + REMOTE_1_SERVER_PORT + "/";
	public static String REMOTE_2_ADDRESS = "//" + REMOTE_2_SERVER + ":" + REMOTE_2_SERVER_PORT + "/";

	public final static String SERVO_REQUEST = "servo";
	public final static String OPEN_REQUEST = "open";
	public final static String GET_REQUEST = "get";
	public final static String SET_REQUEST = "set";
	public final static String CLOSE_REQUEST = "close";
	
}
