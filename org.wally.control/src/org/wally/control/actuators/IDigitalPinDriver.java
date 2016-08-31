package org.wally.control.actuators;

import com.pi4j.io.gpio.GpioPin;

public interface IDigitalPinDriver {
	public GpioPin getPin();
	public void setValue(int value);
	public int getValue();
}
