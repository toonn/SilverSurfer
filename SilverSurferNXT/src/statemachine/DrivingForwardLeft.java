package statemachine;
import communication.CommandUnit;

import lejos.nxt.Motor;

public class DrivingForwardLeft extends State {

	public DrivingForwardLeft() {
		Motor.A.setSpeed(CommandUnit.NORMAL_SPEED*2);
		Motor.A.forward();
		Motor.B.stop();
	}
	
	public State ForwardReleased() {
		return new TurnLeft();
	}
	
	public State BackwardPressed() {
		return new TurnLeft();
	}
	
	public State LeftReleased() {
		return new DrivingForward();
	}
	
	public State RightPressed() {
		return new DrivingForward();
	}
}