package statemachine;
import communication.CommandUnit;

import lejos.nxt.*;

public class TurnRight extends State {

	public TurnRight() {
		Motor.A.setSpeed(CommandUnit.NORMAL_SPEED);
		Motor.B.setSpeed(CommandUnit.NORMAL_SPEED);
		Motor.A.backward();
		Motor.B.forward();
	}
	
	public State ForwardPressed() {
		return new DrivingForwardRight();
	}
	
	public State ForwardReleased() {
		return new DrivingBackwardRight();
	}
	
	public State BackwardPressed() {
		return new DrivingBackwardRight();
	}
	
	public State BackwardReleased() {
		return new DrivingForwardRight();
	}
		
	public State LeftPressed() {
		return new Waiting();
	}
	
	public State RightReleased() {
		return new Waiting();
	}
}