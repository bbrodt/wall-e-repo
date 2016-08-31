package org.wally.control.ui;

import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JToggleButton;

import org.wally.control.WallyController;
import org.wally.control.actuators.ActuatorEventListener;
import org.wally.control.actuators.ActuatorFactory;
import org.wally.control.actuators.IActuatorDriver;
import org.wally.control.actuators.IActuatorUI;

public class SwitchActuator extends JToggleButton implements IActuatorUI {

	private int channel = -1;
	private IActuatorDriver driver;
	private int value = 0;

	public SwitchActuator(String label, int channel) {
		super(label);
		try {
			label = label.replaceAll(" ", "%20");
			URI partialUri = new URI(label);
			URI uri = ActuatorFactory.createURI(ActuatorFactory.SWITCH_SCHEME,
					partialUri.getHost(), partialUri.getPort(), partialUri.getPath(), channel);
			initialize(uri);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
	
	private void initialize(URI uri) {
		try {
			channel = ActuatorFactory.getActuatorChannel(uri);
			setName(ActuatorFactory.getActuatorName(uri));
			driver = ActuatorFactory.createDriver(uri);
		} catch (Exception e) {
			e.printStackTrace();
		}
		addChangeListener(new SwitchActuatorListener(this));
	}

	public IActuatorDriver getDriver() {
		return driver;
	}
	
	public void setValue(int value) {
		this.value = value;
		super.setSelected(value!=0);
		if (driver!=null) {
			driver.setValue(value);
		}
	}

	public int getValue() {
		return value;
	}

	public void connect() {
		driver.connect();
		setValue(value);
	}
	
	public void disconnect() {
		driver.disconnect();
	}
	
	public void addListener(ActuatorEventListener listener) {
		driver.addActuatorListener(listener);
	}
	
	public void removeListener(ActuatorEventListener listener) {
		driver.removeActuatorListener(listener);
	}
}
