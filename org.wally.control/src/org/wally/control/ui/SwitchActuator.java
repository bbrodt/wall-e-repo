package org.wally.control.ui;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.wally.control.actuators.ActuatorEventListener;
import org.wally.control.actuators.ActuatorFactory;
import org.wally.control.actuators.IActuatorDriver;

public class SwitchActuator extends JToggleButton implements IActuatorUI, ChangeListener {

	private int channel = -1;
	private IActuatorDriver driver;
	private int value = 0;

	public SwitchActuator(String label, int channel) {
		super(label+" ["+channel+"]");
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
		addChangeListener(this);
	}

	public IActuatorDriver getDriver() {
		return driver;
	}
	
	public void setValue(int value) {
		this.value = value == 0 ? 0 : 1;
		super.setSelected(value!=0);
		if (driver!=null) {
			driver.setValue(value);
		}
	}

	public int getValue() {
		return value;
	}

	public boolean toggle() {
		setValue(getValue()==0 ? 1 : 0);
		return getValue()!=0;
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

	public List<ActuatorEventListener> getListeners() {
		return driver.getListeners();
	}

	public void stateChanged(ChangeEvent e) {
		// button was pressed or released: update gpio pin state
		boolean selected = isSelected();
		boolean state = (value!=0);
		if (selected!=state)
			setValue(selected ? 1 : 0);
	}
}
