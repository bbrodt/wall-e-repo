package org.wally.control.ui;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class SwitchActuatorListener implements ChangeListener {

	private SwitchActuator target;
	
	public SwitchActuatorListener(SwitchActuator target) {
		this.target = target;
	}
	
	public void stateChanged(ChangeEvent e) {
	}

}
