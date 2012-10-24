package statemachine;
import communication.CommandUnit;

import lejos.nxt.*;

public class DrivingBackwardLeft extends State {

	public DrivingBackwardLeft() {
		Motor.A.setSpeed(CommandUnit.NORMAL_SPEED*2);
		Motor.A.backward();
		Motor.B.stop();
	}
	
	public State ForwardPressed() {
		return new TurnLeft();
	}
	
	public State BackwardReleased() {
		return new TurnLeft();
	}
	
	public State LeftReleased() {
		return new DrivingBackward();
	}
	
	public State RightPressed() {
		return new DrivingBackward();
	}
}
