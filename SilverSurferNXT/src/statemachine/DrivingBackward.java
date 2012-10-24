package statemachine;
import communication.CommandUnit;

import lejos.nxt.*;

public class DrivingBackward extends State {

	public DrivingBackward() {
		Motor.A.setSpeed(CommandUnit.NORMAL_SPEED);
		Motor.B.setSpeed(CommandUnit.NORMAL_SPEED);
		Motor.A.backward();
		Motor.B.backward();
	}
	
	public State ForwardPressed() {
		return new Waiting();
	}
	
	public State BackwardReleased() {
		return new Waiting();
	}
		
	public State LeftPressed() {
		return new DrivingBackwardLeft();
	}
	
	public State LeftReleased() {
		return new DrivingBackwardRight();
	}
	
	public State RightPressed() {
		return new DrivingBackwardRight();
	}
	
	public State RightReleased() {
		return new DrivingBackwardLeft();
	}
}