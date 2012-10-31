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
	
	@Override
	public State ForwardPressed() {
		return new Waiting();
	}
	
	@Override
	public State BackwardReleased() {
		return new Waiting();
	}
		
	@Override
	public State LeftPressed() {
		return new DrivingBackwardLeft();
	}
	
	@Override
	public State LeftReleased() {
		return new DrivingBackwardRight();
	}
	
	@Override
	public State RightPressed() {
		return new DrivingBackwardRight();
	}
	
	@Override
	public State RightReleased() {
		return new DrivingBackwardLeft();
	}
}