package org.wally.control.drivers;

import java.util.Hashtable;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioProvider;
import com.pi4j.io.gpio.GpioProviderBase;
import com.pi4j.io.gpio.PinMode;
import com.pi4j.io.gpio.impl.GpioControllerImpl;

public class DigitalPinProvider extends GpioProviderBase implements GpioProvider {

	final static String NAME = "DigitalPinProvider";
	private static DigitalPinProvider providerInstance;
	private static GpioController gpioController;
	private static Hashtable<Integer, DigitalPinDriver> allocatedDrivers = new Hashtable<Integer, DigitalPinDriver>();
	
	private DigitalPinProvider() {
		// hidden
	}
	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return NAME;
	}

	public static DigitalPinProvider getInstance() {
		if (providerInstance==null) {
			providerInstance = new DigitalPinProvider();
		}
		return providerInstance;
	}
	
	/**
	 * This should be package-private: this method should only be called by DigitalPinDriver
	 * 
	 * @return the GPIO Controller object
	 */
	static GpioController getGpioController() {
		if (gpioController==null) {
			gpioController = new GpioControllerImpl(getInstance());
		}
		return gpioController;
	}

	public DigitalPinDriver getDriver(int channel, PinMode mode) {
		Integer key = new Integer(channel);
		DigitalPinDriver driver = allocatedDrivers.get(key);
		if (driver==null) {
			driver = new DigitalPinDriver(channel, mode);
			allocatedDrivers.put(key, driver);
		}
		return driver;
	}
}
