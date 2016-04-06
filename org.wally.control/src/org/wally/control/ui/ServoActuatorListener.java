package org.wally.control.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ServoActuatorListener implements ChangeListener {
	private ServoActuator target;
	private boolean ignoreStateChange = false;
	private boolean coupled = false;
	List<ServoActuator> coupledSliders = new ArrayList<ServoActuator>();
	
	public ServoActuatorListener(ServoActuator target) {
		this.target = target;
	}
	public void stateChanged(ChangeEvent event) {
		if (!ignoreStateChange) {
			try {
				ignoreStateChange = true;
				ServoActuator slider = (ServoActuator) event.getSource();
				int value = slider.getValue();
				if (coupled) {
					for (ServoActuator s : coupledSliders) {
						s.setValue(value);
					}
				}
			}
			finally {
				ignoreStateChange = false;
			}
		}
	}
	public void setValue(int value) {
		target.setValue(value);
	}
	
	public boolean isCoupled() {
		return coupled;
	}
	public void setCoupled(boolean coupled) {
		this.coupled = coupled;
	}
	public void addCoupledSlider(ServoActuator slider) {
		if (!coupledSliders.contains(slider) && slider!=target) {
			coupledSliders.add(slider);
		}
	}
}