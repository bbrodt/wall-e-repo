package org.wally.control.actuators.local;

import org.wally.control.actuators.ActuatorDriver;
import org.wally.control.actuators.ActuatorEvent;
import org.wally.control.actuators.ActuatorEvent.ActuatorEventType;
import org.wally.control.actuators.ISwitchDriver;
import org.wally.control.drivers.DigitalPinDriver;
import org.wally.control.drivers.DigitalPinProvider;

import com.pi4j.io.gpio.PinMode;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

public class LocalSwitchDriver extends ActuatorDriver implements GpioPinListenerDigital, ISwitchDriver {

	private static DigitalPinProvider provider;
	DigitalPinDriver driver;

	public LocalSwitchDriver(String name, int channel) {
		super(name, channel);
	}
	
	public void connect() {
		try {
			if (driver==null) {
				if (provider==null) {
					provider = DigitalPinProvider.getInstance();
				}
				driver = provider.getDriver(channel, PinMode.DIGITAL_OUTPUT);
				driver.getPin().addListener(this);
				
				notifyListeners(new ActuatorEvent(ActuatorEventType.CONNECTED, this));
			}
		} catch (Exception e) {
			e.printStackTrace();
			driver = null;
			notifyListeners(new ActuatorEvent(ActuatorEventType.ERROR, this, e));
		}
	}

	public void disconnect() {
		if (driver!=null) {
			driver = null;
			notifyListeners(new ActuatorEvent(ActuatorEventType.DISCONNECTED, this));
		}
	}
	
	public void setValue(int value) {
		if (driver==null) {
			notifyListeners(new ActuatorEvent(ActuatorEventType.ERROR, this, "Not connected"));
		}
		else
			driver.setValue(value);
	}
	
	public int getValue() {
		if (driver==null) {
			notifyListeners(new ActuatorEvent(ActuatorEventType.ERROR, this, "Not connected"));
		}
		else
			return driver.getValue();
		return -1;
	}

	public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
		int value = event.getState()==PinState.LOW ? 0 : 1;
		notifyListeners(new ActuatorEvent(ActuatorEventType.VALUE_CHANGED, this, "State Changed", value));
	}

	public boolean toggle() {
		if (driver==null) {
			notifyListeners(new ActuatorEvent(ActuatorEventType.ERROR, this, "Not connected"));
			return false;
		}
		int value = getValue();
		setValue(value==0 ? 1 : 0);
		return value==0;
	}
}
