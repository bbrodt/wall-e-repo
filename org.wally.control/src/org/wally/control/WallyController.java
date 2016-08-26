package org.wally.control;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.wally.clientserver.ClientServerConstants;
import org.wally.control.ui.MainWindow;
import org.wally.control.util.FileUtils;

public class WallyController {
	private static MainWindow mainWindow;

	
    public static void main(String[] args) throws URISyntaxException, Exception {
    	initServoBlaster();
		mainWindow = new MainWindow();
		mainWindow.setVisible(true);
    	initServoBlaster();
    	mainWindow.run();
    	
		SensorServer sensorServer = new SensorServer(ClientServerConstants.CONTROLLER_SERVER_PORT);
		sensorServer.run();
    }
    
    public static MainWindow getMainWindow() {
    	return mainWindow;
    }
    
    public static void setStatus(String status) {
    	if (mainWindow!=null)
    		mainWindow.setStatus(status);
    	else
    		System.err.println("Status: "+status);
    }
    
    public static void println(String text) {
    	if (mainWindow!=null)
    		mainWindow.println(text);
    	else
    		System.err.println("Console Output: "+text);
    }
    
    public static void print(String text) {
    	if (mainWindow!=null)
    		mainWindow.print(text);
    	else
    		System.err.println("Console Output: "+text);
    }
    
    private static void initServoBlaster() {
    	String os = System.getProperty("os.name");
    	if (os.startsWith("Windows")) {
    		// empty the servoblaster dummy device file
    		File servoblaster = new File("C:/dev/servoblaster");
    		if (servoblaster.exists()) {
    			servoblaster.delete();
    		}
    		try {
				servoblaster.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
    		// create a config file
    		File servoblasterCfg = new File("C:/dev/servoblaster");
    		if (!servoblasterCfg.exists()) {
				FileUtils.save(servoblasterCfg,
						"p1pins=7,11,12,13,15,16,18,22,23\n"
								+ "p5pins=\n"
								+ "\n"
								+ "Servo mapping:\n"
								+ "     0 on P1-7           GPIO-4\n"
								+ "     1 on P1-11          GPIO-17\n"
								+ "     2 on P1-12          GPIO-18\n"
								+ "     3 on P1-13          GPIO-27\n"
								+ "     4 on P1-15          GPIO-22\n"
								+ "     5 on P1-16          GPIO-23\n"
								+ "     6 on P1-18          GPIO-24\n"
								+ "     7 on P1-22          GPIO-25\n"
								+ "     8 on P1-23          GPIO-26\n"
				);
    		}
    	}
    }
}
