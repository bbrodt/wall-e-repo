package org.wally.control.drivers;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: Device Abstractions
 * FILENAME      :  MaestroServoProvider.java
 *
 * This file is part of the Pi4J project. More information about
 * this project can be found here:  http://www.pi4j.com/
 * **********************************************************************
 * %%
 * Copyright (C) 2012 - 2016 Pi4J
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pi4j.component.servo.ServoDriver;
import com.pi4j.component.servo.ServoProvider;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinMode;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.impl.PinImpl;
import com.pi4j.io.serial.Serial;
import com.pi4j.io.serial.SerialFactory;
import com.pi4j.io.serial.SerialPortException;

public class MaestroServoProvider implements ServoProvider {

	public enum InterfaceType {
		USB, UART
	};

	public static final String PROVIDER_NAME = "Pololu Maestro Servo Controller";
    public static final String USB_DEVICE = "/dev/ttyACM0";
    public static final String UART_DEVICE = "/dev/ttyAMA0";
    // Maestro "device number", useful if daisy-chaining multiple controllers.
    // The factory default is 12.
    public static final int DEFAULT_DEVICE_ADDRESS = 12;
    public static final int DEFAULT_BAUDRATE = 9600;

    public static Map<Pin, String> PIN_MAP;
    public static Map<String, Pin> REVERSE_PIN_MAP;

    protected InterfaceType interfaceType;
    protected Serial device;
    private String deviceName = "undefined";
    private byte deviceAddress;
    protected Map<Pin, MaestroServoDriver> servoDrivers = new HashMap<Pin, MaestroServoDriver>();

    static {
        PIN_MAP = new HashMap<Pin, String>();
        REVERSE_PIN_MAP = new HashMap<String, Pin>();
        for (int i=0; i<12; ++i) {
        	definePin(createDigitalPin(i, ""+i), ""+i);
        }
    }

    public MaestroServoProvider() throws SerialPortException, IOException {
    	this(InterfaceType.UART, -1, DEFAULT_BAUDRATE);
    }

    public MaestroServoProvider(InterfaceType interfaceType) throws SerialPortException, IOException {
    	this(interfaceType, -1, DEFAULT_BAUDRATE);
    }

    public MaestroServoProvider(InterfaceType interfaceType, int deviceAddress, int baudrate) throws SerialPortException, IOException {
    	this.interfaceType = interfaceType;
    	if (deviceAddress==-1) {
    		deviceAddress = DEFAULT_DEVICE_ADDRESS;
    	}
    	this.deviceAddress = (byte) deviceAddress;
    	if (interfaceType==InterfaceType.USB) {
    		deviceName = USB_DEVICE;
    	}
    	else {
    		deviceName = UART_DEVICE;
    	}
   		device = SerialFactory.createInstance();
   		device.open(deviceName, baudrate);
    }

    public void dispose() {
		try {
			if (device!=null && device.isOpen()) {
				device.close();
			}
	    	device = null;
	    	servoDrivers.clear();
		} catch (Exception e) {
			e.printStackTrace();
		}

    }

	public List<Pin> getDefinedServoPins() throws IOException {
        List<Pin> servoPins = new ArrayList<Pin>();
        for (Pin pin : PIN_MAP.keySet()) {
        	servoPins.add(pin);
        }
		return servoPins;
	}


    /**
     * Returns new instance of {@link MaestroServoDriver}.
     *
     * @param servoPin servo pin.
     * @return instance of {@link MaestroServoDriver}.
     */
    public synchronized ServoDriver getServoDriver(Pin servoPin) throws IOException {
        List<Pin> servoPins = getDefinedServoPins();
        int index = servoPins.indexOf(servoPin);
        if (index < 0) {
            throw new IOException("Servo driver cannot drive pin " + servoPin);
        }

        MaestroServoDriver driver = servoDrivers.get(servoPin);
        if (driver == null) {
            driver = new MaestroServoDriver(this, servoPin);
            servoDrivers.put(servoPin, driver);
        }

        return driver;
    }
    
    public synchronized ServoDriver getServoDriver(int address) throws IOException {
        for (Pin pin : getDefinedServoPins()) {
        	if (pin.getAddress() == address)
        		return getServoDriver(pin);
        }
        return null;
    }
    
    protected synchronized void setServoPosition(int pinAddress, int value) {
    	byte command[] = { (byte)0x84, (byte)pinAddress, (byte)(value & 0x7F), (byte)(value >> 7 & 0x7F) };
//    	while (isMoving()) {
//    		try {
//				Thread.sleep(100);
//			} catch (InterruptedException e) {
//			}
//    	}
    	write(command);
    }

    protected synchronized int getServoPosition(int pinAddress) {
    	byte command[] = { (byte)0x90, (byte)pinAddress };
    	write(command);

        byte response[] = read(2);
        return response[0] + 256*response[1];
    }

    protected void setSpeed(int pinAddress, int value) {
    	byte command[] = { (byte)0x87, (byte)pinAddress, (byte)(value & 0x7F), (byte)((value >> 7) & 0x7F) };
    	write(command);
    }

    protected void setAcceleration(int pinAddress, int value) {
    	byte command[] = { (byte)0x89, (byte)pinAddress, (byte)(value & 0x7F), (byte)((value >> 7) & 0x7F) };
    	write(command);
    }

    protected boolean isMoving() {
    	byte command[] = { (byte)0x93 };
    	write(command);

        byte response[] = read(1);
        return response[0] == 0x01;
    }

    private void write(byte command[]) {
        try {
        	if (interfaceType==InterfaceType.UART) {
        		// use the so-called "Pololu Protocol" instead of "Compact Protocol"
        		device.write(new byte[] { (byte)0xAA, deviceAddress });
        		// clear the MSB of first command byte
        		command[0] = (byte)(command[0] & 0x7F);
        	}
            device.write(command);
            device.flush();
        } catch (Exception e) {
            throw new RuntimeException("Error writing to " + deviceName, e);
        }
    }

    private byte[] read(int len) {
    	byte response[] = new byte[len];
        try {
        	int tries = 0;
        	int avail = device.available();
        	while (avail<len && ++tries<10) {
        		Thread.sleep(100);
        		avail = device.available();
        	}
        	if (avail<len)
            	return response; //throw new RuntimeException("Timeout reading from " + deviceName);
       		response = device.read(len);
        }
        catch (Exception e) {
        	throw new RuntimeException("Error reading from " + deviceName, e);
        }
        return response;
    }

    private static void definePin(Pin pin, String s) {
        PIN_MAP.put(pin, s);
        REVERSE_PIN_MAP.put(s, pin);
    }

    private static Pin createDigitalPin(int address, String name) {
        Pin pin = new PinImpl(PROVIDER_NAME, address, name,
                    EnumSet.of(PinMode.DIGITAL_INPUT, PinMode.DIGITAL_OUTPUT),
                    PinPullResistance.all());
        return pin;
    }
}
