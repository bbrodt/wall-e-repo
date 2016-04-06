package org.wally.control.ui;

import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JSlider;

import org.wally.control.actuators.ActuatorEventListener;
import org.wally.control.actuators.ActuatorFactory;
import org.wally.control.actuators.IActuatorDriver;

public class ServoActuator extends JSlider {
	public final static int SLIDER_TICK = 20;
	
	private boolean reversed = false;
	private int channel = -1;
	private IActuatorDriver driver;
	
	public ServoActuator(String label, int channel) {
		super(JSlider.HORIZONTAL, IActuatorDriver.UI_MIN, IActuatorDriver.UI_MAX,
				ServoActuator.SLIDER_TICK);
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
		setMajorTickSpacing(IActuatorDriver.UI_MAX / 2);
		setMinorTickSpacing(ServoActuator.SLIDER_TICK);
		setPaintTicks(true);
		setPaintLabels(true);
		setValue(IActuatorDriver.UI_MAX / 2);
		addChangeListener(new ServoActuatorListener(this));
	}

	public void setValue(int value) {
		super.setValue(value);
		if (reversed) {
			value = IActuatorDriver.UI_MIN + IActuatorDriver.UI_MAX - value;
		}
		if (driver!=null) {
			driver.setValue(value);
			System.out.println(getName() + " servo " + channel + "=" + driver.toActuatorValue(value));
		}
	}

	public int getServoPosition() {
		int value = getValue();
		if (reversed)
			value = IActuatorDriver.UI_MIN + IActuatorDriver.UI_MAX - value;
		return driver.toActuatorValue(value);
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