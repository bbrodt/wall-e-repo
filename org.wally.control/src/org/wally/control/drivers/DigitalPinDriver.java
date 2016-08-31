package org.wally.control.drivers;

import java.util.EnumSet;

import org.wally.control.actuators.IDigitalPinDriver;

import com.pi4j.io.gpio.GpioPin;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinMode;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.impl.PinImpl;

public class DigitalPinDriver implements IDigitalPinDriver {
	
	private GpioPinDigitalInput pinDigitalInput;
	private GpioPinDigitalOutput pinDigitalOutput;

	public DigitalPinDriver(int channel, PinMode mode) {
		if (mode!=PinMode.DIGITAL_INPUT && mode!=PinMode.DIGITAL_OUTPUT)
			throw new IllegalArgumentException("Pin mode must be DIGITAL_INPUT or DIGITAL_OUTPUT");
		Pin pin = new PinImpl(DigitalPinProvider.NAME, channel, "GPIO-"+channel,
                EnumSet.of(mode));
		if (mode==PinMode.DIGITAL_INPUT) {
			pinDigitalInput = DigitalPinProvider.getInstance().getGpioController().provisionDigitalInputPin(pin);
		}
		else {
			pinDigitalOutput = DigitalPinProvider.getInstance().getGpioController().provisionDigitalOutputPin(pin, PinState.LOW);
		}
	}
	
	public void setValue(int value) {
		if (pinDigitalOutput==null) {
			throw new RuntimeException("Digital pin is not configured for output");
		}
		pinDigitalOutput.setState(value==0 ? PinState.LOW : PinState.HIGH);
	}
	
	public int getValue() {
		if (pinDigitalInput==null) {
			throw new RuntimeException("Digital pin is not configured for input");
		}
		PinState state = pinDigitalInput.getState();
		return state==PinState.LOW ? 0 : 1;
	}

	public GpioPin getPin() {
		if (pinDigitalInput!=null)
			return pinDigitalInput;
		if (pinDigitalOutput!=null)
			return pinDigitalOutput;
		return null;
	}
}
