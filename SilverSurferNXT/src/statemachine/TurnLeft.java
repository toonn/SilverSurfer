package statemachine;
import communication.CommandUnit;

import lejos.nxt.Motor;

public class TurnLeft extends State {
	
	public TurnLeft() {
		Motor.A.setSpeed(CommandUnit.NORMAL_SPEED);
		Motor.B.setSpeed(CommandUnit.NORMAL_SPEED);
		Motor.A.forward();
		Motor.B.backward();
	}

	public State ForwardPressed() {
		return new DrivingForwardLeft();
	}
	
	public State ForwardReleased() {
		return new DrivingBackwardLeft();
	}
	
	public State BackwardPressed() {
		return new DrivingBackwardLeft();
	}
	
	public State BackwardReleased() {
		return new DrivingForwardLeft();
	}
	
	public State LeftReleased() {
		return new Waiting();
	}
	
	public State RightPressed() {
		return new Waiting();
	}
}