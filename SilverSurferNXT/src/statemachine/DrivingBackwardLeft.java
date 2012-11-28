/*package statemachine;

import communication.CommandUnit;

import lejos.nxt.*;

public class DrivingBackwardLeft extends State {

	public DrivingBackwardLeft() {
		Motor.A.setSpeed(CommandUnit.NORMAL_SPEED*2);
		Motor.A.backward();
		Motor.B.stop();
	}
	
	@Override
	public State ForwardPressed() {
		return new TurnLeft();
	}
	
	@Override
	public State BackwardReleased() {
		return new TurnLeft();
	}
	
	@Override
	public State LeftReleased() {
		return new DrivingBackward();
	}
	
	@Override
	public State RightPressed() {
		return new DrivingBackward();
	}
}*/