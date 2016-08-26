package org.wally.control.ui;

import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JSlider;

import org.wally.control.WallyController;
import org.wally.control.actuators.ActuatorEventListener;
import org.wally.control.actuators.ActuatorFactory;
import org.wally.control.actuators.IActuatorDriver;

public class ServoActuator extends JSlider {
	
	private boolean reversed = false;
	private int channel = -1;
	private IActuatorDriver driver;
	
	public ServoActuator(String label, int channel) {
		super(JSlider.HORIZONTAL);
		try {
			label = label.replaceAll(" ", "%20");
			URI partialUri = new URI(label);
			URI uri = ActuatorFactory.createURI(ActuatorFactory.SERVO_SCHEME,
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
		addChangeListener(new ServoActuatorListener(this));
	}

	public IActuatorDriver getDriver() {
		return driver;
	}
	
	public void setValue(int value) {
		super.setValue(value);
		if (reversed) {
			value = driver.getMinValue() + driver.getMaxValue() - value;
		}
		if (driver!=null) {
			driver.setValue(value);
			WallyController.println(getName() + " servo " + channel + "=" + value);
		}
	}

	public int getServoPosition() {
		int value = getValue();
		if (reversed)
			value = driver.getMinValue() + driver.getMaxValue() - value;
		return value;
	}
	
	public boolean isReversed() {
		return reversed;
	}

	public void setReversed(boolean reversed) {
		this.reversed = reversed;
		this.setInverted(true);
	}

	public void connect() {
		driver.connect();
		int min = driver.getMinValue();
		int max = driver.getMaxValue();
		setMinimum(min);
		setMaximum(max);
		setMajorTickSpacing((max-min)/4);
		//setMinorTickSpacing(1);
		setPaintTicks(true);
		setPaintLabels(true);
		setValue(min + (max-min)/2);
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