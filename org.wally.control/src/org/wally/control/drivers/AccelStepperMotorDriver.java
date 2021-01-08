package org.wally.control.drivers;

import java.util.Map;

import com.pi4j.component.motor.MotorState;
import com.pi4j.component.motor.StepperMotor;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

////////////////////////////////////////////////////////////////////////////////
// This is a work in progress...
////////////////////////////////////////////////////////////////////////////////

public class AccelStepperMotorDriver implements StepperMotor {

	public static enum MotorInterfaceType {
		FUNCTION(0), /// < Use the functional interface, implementing your own
						/// driver functions (internal use only)
		DRIVER(1), /// < Stepper Driver, 2 driver pins required
		FULL2WIRE(2), /// < 2 wire stepper, 2 motor pins required
		FULL3WIRE(3), /// < 3 wire stepper, such as HDD spindle, 3 motor pins
						/// required
		FULL4WIRE(4), /// < 4 wire full stepper, 4 motor pins required
		HALF3WIRE(6), /// < 3 wire half stepper, such as HDD spindle, 3 motor
						/// pins required
		HALF4WIRE(8); /// < 4 wire half stepper, 4 motor pins required

		private final int value;

		/**
		 * @param value
		 * @return
		 */
		MotorInterfaceType(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	};

	// Direction indicator
	// Symbolic names for the direction the motor is turning
	enum Direction {
		DIRECTION_CCW, // < Counter-Clockwise
		DIRECTION_CW // < Clockwise
	};

	private static int HIGH = 1;
	private static int LOW = 0;
	private static int INPUT = 0;
	private static int OUTPUT = 1;

	// Current direction motor is spinning in
	// Protected because some peoples subclasses need it to be so
	Direction _direction; // 1 == CW

	// Number of pins on the stepper motor. Permits 2 or 4. 2 pins is a
	// bipolar, and 4 pins is a unipolar.
	MotorInterfaceType _interface; // 0, 1, 2, 4, 8, See MotorInterfaceType

	// Arduino pin number assignments for the 2 or 4 pins required to interface
	// to the
	// stepper motor or driver
	byte _pin[];

	// Whether the _pins is inverted or not
	byte _pinInverted[];

	// The current absolution position in steps.
	long _currentPos; // Steps

	// The target position in steps. The AccelStepper library will move the
	// motor from the _currentPos to the _targetPos, taking into account the
	// max speed, acceleration and deceleration
	long _targetPos; // Steps

	// The current motos speed in steps per second
	// Positive is clockwise
	float _speed; // Steps per second

	// The maximum permitted speed in steps per second. Must be > 0.
	float _maxSpeed;

	// The acceleration to use to accelerate or decelerate the motor in steps
	// per second per second. Must be > 0
	float _acceleration;
	float _sqrt_twoa; // Precomputed sqrt(2*_acceleration)

	// The current interval between steps in microseconds.
	// 0 means the motor is currently stopped with _speed == 0
	long _stepInterval;

	// The last step time in microseconds
	long _lastStepTime;

	// The minimum allowed pulse width in microseconds
	int _minPulseWidth;

	// Is the direction pin inverted?
	// bool _dirInverted; // Moved to _pinInverted[1]

	// Is the step pin inverted?
	// bool _stepInverted; // Moved to _pinInverted[0]

	// Is the enable pin inverted?
	boolean _enableInverted;

	// Enable pin for stepper driver, or 0xFF if unused.
	byte _enablePin;

	// The step counter for speed calculations
	long _n;

	// Initial step size in microseconds
	float _c0;

	// Last step size in microseconds
	float _cn;

	// Min step size in microseconds based on maxSpeed
	float _cmin; // at max speed

	////////////////////////////////////////////////////////////////////////////////
	// Constructor
	////////////////////////////////////////////////////////////////////////////////

	AccelStepperMotorDriver(MotorInterfaceType type, byte pin1, byte pin2, byte pin3, byte pin4, boolean enable) {
		_interface = type;
		_currentPos = 0;
		_targetPos = 0;
		_speed = 0f;
		_maxSpeed = 1f;
		_acceleration = 0f;
		_sqrt_twoa = 1f;
		_stepInterval = 0;
		_minPulseWidth = 1;
		_enablePin = (byte) 0xff;
		_lastStepTime = 0;
		_pin[0] = pin1;
		_pin[1] = pin2;
		_pin[2] = pin3;
		_pin[3] = pin4;
		_enableInverted = false;

		// NEW
		_n = 0;
		_c0 = 0f;
		_cn = 0f;
		_cmin = 1f;
		_direction = Direction.DIRECTION_CCW;

		int i;
		for (i = 0; i < 4; i++)
			_pinInverted[i] = 0;
		if (enable)
			enableOutputs();
		// Some reasonable default
		setAcceleration(1);
	}

	////////////////////////////////////////////////////////////////////////////////
	// Interface methods
	////////////////////////////////////////////////////////////////////////////////

	public void forward() {
		// TODO Auto-generated method stub

	}

	public void forward(long arg0) {
		// TODO Auto-generated method stub

	}

	public MotorState getState() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isState(MotorState arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isStopped() {
		// TODO Auto-generated method stub
		return false;
	}

	public void reverse() {
		// TODO Auto-generated method stub

	}

	public void reverse(long arg0) {
		// TODO Auto-generated method stub

	}

	public void setState(MotorState arg0) {
		// TODO Auto-generated method stub

	}

	public void stop() {
		if (_speed != 0.0) {
			long stepsToStop = (long) ((_speed * _speed) / (2.0 * _acceleration)) + 1; // Equation
																						// 16
																						// (+integer
																						// rounding)
			if (_speed > 0)
				move(stepsToStop);
			else
				move(-stepsToStop);
		}
	}

	public void clearProperties() {
		// TODO Auto-generated method stub

	}

	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, String> getProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getProperty(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getProperty(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object getTag() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasProperty(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public void removeProperty(String arg0) {
		// TODO Auto-generated method stub

	}

	public void setName(String arg0) {
		// TODO Auto-generated method stub

	}

	public void setProperty(String arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	public void setTag(Object arg0) {
		// TODO Auto-generated method stub

	}

	public byte[] getStepSequence() {
		// TODO Auto-generated method stub
		return null;
	}

	public float getStepsPerRevolution() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void rotate(double arg0) {
		// TODO Auto-generated method stub

	}

	public void setStepInterval(long arg0) {
		// TODO Auto-generated method stub

	}

	public void setStepInterval(long arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	public void setStepSequence(byte[] arg0) {
		// TODO Auto-generated method stub

	}

	public void setStepsPerRevolution(int arg0) {
		// TODO Auto-generated method stub

	}

	public void step(long step) {
		switch (_interface) {
		case FUNCTION:
			step0(step);
			break;

		case DRIVER:
			step1(step);
			break;

		case FULL2WIRE:
			step2(step);
			break;

		case FULL3WIRE:
			step3(step);
			break;

		case FULL4WIRE:
			step4(step);
			break;

		case HALF3WIRE:
			step6(step);
			break;

		case HALF4WIRE:
			step8(step);
			break;
		}
	}

	////////////////////////////////////////////////////////////////////////////////
	// AccelStepper methods
	////////////////////////////////////////////////////////////////////////////////

	void moveTo(long absolute) {
		if (_targetPos != absolute) {
			_targetPos = absolute;
			computeNewSpeed();
			// compute new n?
		}
	}

	void move(long relative) {
		moveTo(_currentPos + relative);
	}

	// Implements steps according to the current step interval
	// You must call this at least once per step
	// returns true if a step occurred
	boolean runSpeed() {
		// Dont do anything unless we actually have a step interval
		if (_stepInterval == 0)
			return false;

		long time = java.lang.System.currentTimeMillis() * 1000;
		if (time - _lastStepTime >= _stepInterval) {
			if (_direction == Direction.DIRECTION_CW) {
				// Clockwise
				_currentPos += 1;
			} else {
				// Anticlockwise
				_currentPos -= 1;
			}
			step(_currentPos);

			_lastStepTime = time; // Caution: does not account for costs in
									// step()

			return true;
		} else {
			return false;
		}
	}

	long distanceToGo() {
		return _targetPos - _currentPos;
	}

	long targetPosition() {
		return _targetPos;
	}

	long currentPosition() {
		return _currentPos;
	}

	// Useful during initialisations or after initial positioning
	// Sets speed to 0
	void setCurrentPosition(long position) {
		_targetPos = _currentPos = position;
		_n = 0;
		_stepInterval = 0;
		_speed = 0f;
	}

	void computeNewSpeed() {
		long distanceTo = distanceToGo(); // +ve is clockwise from curent
											// location

		long stepsToStop = (long) ((_speed * _speed) / (2.0 * _acceleration)); // Equation
																				// 16

		if (distanceTo == 0 && stepsToStop <= 1) {
			// We are at the target and its time to stop
			_stepInterval = 0;
			_speed = 0f;
			_n = 0;
			return;
		}

		if (distanceTo > 0) {
			// We are anticlockwise from the target
			// Need to go clockwise from here, maybe decelerate now
			if (_n > 0) {
				// Currently accelerating, need to decel now? Or maybe going the
				// wrong way?
				if ((stepsToStop >= distanceTo) || _direction == Direction.DIRECTION_CCW)
					_n = -stepsToStop; // Start deceleration
			} else if (_n < 0) {
				// Currently decelerating, need to accel again?
				if ((stepsToStop < distanceTo) && _direction == Direction.DIRECTION_CW)
					_n = -_n; // Start accceleration
			}
		} else if (distanceTo < 0) {
			// We are clockwise from the target
			// Need to go anticlockwise from here, maybe decelerate
			if (_n > 0) {
				// Currently accelerating, need to decel now? Or maybe going the
				// wrong way?
				if ((stepsToStop >= -distanceTo) || _direction == Direction.DIRECTION_CW)
					_n = -stepsToStop; // Start deceleration
			} else if (_n < 0) {
				// Currently decelerating, need to accel again?
				if ((stepsToStop < -distanceTo) && _direction == Direction.DIRECTION_CCW)
					_n = -_n; // Start accceleration
			}
		}

		// Need to accelerate or decelerate
		if (_n == 0) {
			// First step from stopped
			_cn = _c0;
			_direction = (distanceTo > 0) ? Direction.DIRECTION_CW : Direction.DIRECTION_CCW;
		} else {
			// Subsequent step. Works for accel (n is +_ve) and decel (n is
			// -ve).
			_cn = _cn - ((2f * _cn) / ((4f * _n) + 1)); // Equation 13
			_cn = Math.max(_cn, _cmin);
		}
		_n++;
		_stepInterval = (long) _cn;
		_speed = 1000000f / _cn;
		if (_direction == Direction.DIRECTION_CCW)
			_speed = -_speed;

		System.out.println(_speed);
		System.out.println(_acceleration);
		System.out.println(_cn);
		System.out.println(_c0);
		System.out.println(_n);
		System.out.println(_stepInterval);
		System.out.println(distanceTo);
		System.out.println(stepsToStop);
		System.out.println("-----");
	}

	// Run the motor to implement speed and acceleration in order to proceed to
	// the target position
	// You must call this at least once per step, preferably in your main loop
	// If the motor is in the desired position, the cost is very small
	// returns true if the motor is still running to the target position.
	boolean run() {
		if (runSpeed())
			computeNewSpeed();
		return _speed != 0.0 || distanceToGo() != 0;
	}

	void setMaxSpeed(float speed) {
		if (speed < 0.0)
			speed = -speed;
		if (_maxSpeed != speed) {
			_maxSpeed = speed;
			_cmin = 1000000f / speed;
			// Recompute _n from current speed and adjust speed if accelerating
			// or cruising
			if (_n > 0) {
				_n = (long) ((_speed * _speed) / (2.0 * _acceleration)); // Equation
																			// 16
				computeNewSpeed();
			}
		}
	}

	float maxSpeed() {
		return _maxSpeed;
	}

	void setAcceleration(float acceleration) {
		if (acceleration == 0.0)
			return;
		if (acceleration < 0.0)
			acceleration = -acceleration;
		if (_acceleration != acceleration) {
			// Recompute _n per Equation 17
			_n = (long) (_n * (_acceleration / acceleration));
			// New c0 per Equation 7, with correction per Equation 15
			_c0 = 0.676f * (float) Math.sqrt(2.0 / acceleration) * 1000000f; // Equation
																				// 15
			_acceleration = acceleration;
			computeNewSpeed();
		}
	}

	private float constrain(float x, float min, float max) {
		if (x < min)
			return min;
		if (x > max)
			return max;
		return x;
	}

	void setSpeed(float speed) {
		if (speed == _speed)
			return;
		speed = constrain(speed, -_maxSpeed, _maxSpeed);
		if (speed == 0.0)
			_stepInterval = 0;
		else {
			_stepInterval = (long) Math.abs(1000000f / speed);
			_direction = (speed > 0.0) ? Direction.DIRECTION_CW : Direction.DIRECTION_CCW;
		}
		_speed = speed;
	}

	float speed() {
		return _speed;
	}

	// Subclasses can override

	// You might want to override this to implement eg serial output
	// bit 0 of the mask corresponds to _pin[0]
	// bit 1 of the mask corresponds to _pin[1]
	// ....
	void setOutputPins(int mask) {
		byte numpins = 2;
		if (_interface == MotorInterfaceType.FULL4WIRE || _interface == MotorInterfaceType.HALF4WIRE)
			numpins = 4;
		else if (_interface == MotorInterfaceType.FULL3WIRE || _interface == MotorInterfaceType.HALF3WIRE)
			numpins = 3;
		byte i;
		for (i = 0; i < numpins; i++)
			digitalWrite(_pin[i], (mask & (1 << i)) != 0 ? (HIGH ^ _pinInverted[i]) : (LOW ^ _pinInverted[i]));
	}

	// 0 pin step function (ie for functional usage)
	void step0(long step) {
		throw new NotImplementedException();
	}

	// 1 pin step function (ie for stepper drivers)
	// This is passed the current step number (0 to 7)
	// Subclasses can override
	void step1(long step) {
		// _pin[0] is step, _pin[1] is direction
		// Set direction first else get rogue pulses
		setOutputPins(_direction == Direction.DIRECTION_CW ? 0x02 : 0x00);
		// step HIGH
		setOutputPins(_direction == Direction.DIRECTION_CW ? 0x03 : 0x01); 
		// Caution 200ns setup time
		// Delay the minimum allowed pulse width
		try {
			Thread.sleep(_minPulseWidth / 1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setOutputPins(_direction == Direction.DIRECTION_CW ? 0x02 : 0x00); // step LOW
	}

	// 2 pin step function
	// This is passed the current step number (0 to 7)
	// Subclasses can override
	void step2(long step) {
		switch ((int) (step & 0x3)) {
		case 0: /* 01 */
			setOutputPins(0x02);
			break;

		case 1: /* 11 */
			setOutputPins(0x03);
			break;

		case 2: /* 10 */
			setOutputPins(0x01);
			break;

		case 3: /* 00 */
			setOutputPins(0x00);
			break;
		}
	}

	// 3 pin step function
	// This is passed the current step number (0 to 7)
	// Subclasses can override
	void step3(long step) {
		switch ((int) (step % 3)) {
		case 0: // 100
			setOutputPins(0x04);
			break;

		case 1: // 001
			setOutputPins(0x01);
			break;

		case 2: // 010
			setOutputPins(0x02);
			break;

		}
	}

	// 4 pin step function for half stepper
	// This is passed the current step number (0 to 7)
	// Subclasses can override
	void step4(long step) {
		switch ((int)(step & 0x3)) {
		case 0: // 1010
			setOutputPins(0x0a);
			break;

		case 1: // 0110
			setOutputPins(0x06);
			break;

		case 2: // 0101
			setOutputPins(0x05);
			break;

		case 3: // 1001
			setOutputPins(0x09);
			break;
		}
	}

	// 3 pin half step function
	// This is passed the current step number (0 to 7)
	// Subclasses can override
	void step6(long step) {
		switch ((int)(step % 6)) {
		case 0: // 100
			setOutputPins(0x04);
			break;

		case 1: // 101
			setOutputPins(0x05);
			break;

		case 2: // 001
			setOutputPins(0x01);
			break;

		case 3: // 011
			setOutputPins(0x03);
			break;

		case 4: // 010
			setOutputPins(0x02);
			break;

		case 5: // 110
			setOutputPins(0x06);
			break;

		}
	}

	// 4 pin half step function
	// This is passed the current step number (0 to 7)
	// Subclasses can override
	void step8(long step) {
		switch ((int) (step & 0x7)) {
		case 0: // 1000
			setOutputPins(0x01);
			break;

		case 1: // 1010
			setOutputPins(0x0a);
			break;

		case 2: // 0010
			setOutputPins(0x02);
			break;

		case 3: // 0110
			setOutputPins(0x06);
			break;

		case 4: // 0100
			setOutputPins(0x04);
			break;

		case 5: // 0101
			setOutputPins(0x05);
			break;

		case 6: // 1000
			setOutputPins(0x08);
			break;

		case 7: // 1001
			setOutputPins(0x09);
			break;
		}
	}

	// Prevents power consumption on the outputs
	void disableOutputs() {
		if (_interface == MotorInterfaceType.FUNCTION)
			return;

		setOutputPins(0); // Handles inversion automatically
		if (_enablePin != 0xff) {
			pinMode(_enablePin, OUTPUT);
			digitalWrite(_enablePin, LOW ^ (_enableInverted ? 1 : 0));
		}
	}

	void enableOutputs() {
		if (_interface == MotorInterfaceType.FUNCTION)
			return;

		pinMode(_pin[0], OUTPUT);
		pinMode(_pin[1], OUTPUT);
		if (_interface == MotorInterfaceType.FULL4WIRE || _interface == MotorInterfaceType.HALF4WIRE) {
			pinMode(_pin[2], OUTPUT);
			pinMode(_pin[3], OUTPUT);
		} else if (_interface == MotorInterfaceType.FULL3WIRE || _interface == MotorInterfaceType.HALF3WIRE) {
			pinMode(_pin[2], OUTPUT);
		}

		if (_enablePin != 0xff) {
			pinMode(_enablePin, OUTPUT);
			digitalWrite(_enablePin, HIGH ^ (_enableInverted ? 1 : 0));
		}
	}

	void setMinPulseWidth(int minWidth) {
		_minPulseWidth = minWidth;
	}

	void setEnablePin(byte enablePin) {
		_enablePin = enablePin;

		// This happens after construction, so init pin now.
		if (_enablePin != 0xff) {
			pinMode(_enablePin, OUTPUT);
			digitalWrite(_enablePin, HIGH ^ (_enableInverted ? 1 : 0));
		}
	}

	void setPinsInverted(boolean directionInvert, boolean stepInvert, boolean enableInvert) {
		_pinInverted[0] = stepInvert ? (byte) 1 : (byte) 0;
		_pinInverted[1] = directionInvert ? (byte) 1 : (byte) 0;
		_enableInverted = enableInvert;
	}

	void setPinsInverted(boolean pin1Invert, boolean pin2Invert, boolean pin3Invert, boolean pin4Invert,
			boolean enableInvert) {
		_pinInverted[0] = pin1Invert ? (byte) 1 : (byte) 0;
		_pinInverted[1] = pin2Invert ? (byte) 1 : (byte) 0;
		_pinInverted[2] = pin3Invert ? (byte) 1 : (byte) 0;
		_pinInverted[3] = pin4Invert ? (byte) 1 : (byte) 0;
		_enableInverted = enableInvert;
	}

	// Blocks until the target position is reached and stopped
	void runToPosition() {
		while (run())
			;
	}

	boolean runSpeedToPosition() {
		if (_targetPos == _currentPos)
			return false;
		if (_targetPos > _currentPos)
			_direction = Direction.DIRECTION_CW;
		else
			_direction = Direction.DIRECTION_CCW;
		return runSpeed();
	}

	// Blocks until the new target position is reached
	void runToNewPosition(long position) {
		moveTo(position);
		runToPosition();
	}

	boolean isRunning() {
		return !(_speed == 0.0 && _targetPos == _currentPos);
	}

	// stubs
	void digitalWrite(byte pin, int value) {
		
	}
	
	void pinMode(byte pin, int value) {
		
	}
}
