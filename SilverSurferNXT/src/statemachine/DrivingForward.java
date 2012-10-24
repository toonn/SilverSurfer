package statemachine;
import communication.CommandUnit;

import lejos.nxt.*;

public class DrivingForward extends State {

	public DrivingForward() {
		Motor.A.setSpeed(CommandUnit.NORMAL_SPEED);
		Motor.B.setSpeed(CommandUnit.NORMAL_SPEED);
		Motor.A.forward();
		Motor.B.forward();
	}
	
	public State ForwardReleased() {
		return new Waiting();
	}
	
	public State BackwardPressed() {
		return new Waiting();
	}
		
	public State LeftPressed() {
		return new DrivingForwardLeft();
	}
	
	public State LeftReleased() {
		return new DrivingForwardRight();
	}
	
	public State RightPressed() {
		return new DrivingForwardRight();
	}
	
	public State RightReleased() {
		return new DrivingForwardLeft();
	}
}