/*package statemachine;

import communication.CommandUnit;

import lejos.nxt.*;

public class DrivingBackwardRight extends State {

	public DrivingBackwardRight() {
		Motor.B.setSpeed(CommandUnit.NORMAL_SPEED*2);
		Motor.A.stop();
		Motor.B.backward();
	}
	
	@Override
	public State ForwardPressed() {
		return new TurnRight();
	}
	
	@Override
	public State BackwardReleased() {
		return new TurnRight();
	}
	
	@Override
	public State LeftPressed() {
		return new DrivingBackward();
	}
	
	@Override
	public State RightReleased() {
		return new DrivingBackward();
	}
}*/