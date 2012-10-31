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
	
	@Override
	public State ForwardReleased() {
		return new Waiting();
	}
	
	@Override
	public State BackwardPressed() {
		return new Waiting();
	}
	
	@Override
	public State LeftPressed() {
		return new DrivingForwardLeft();
	}
	
	@Override
	public State LeftReleased() {
		return new DrivingForwardRight();
	}
	
	@Override
	public State RightPressed() {
		return new DrivingForwardRight();
	}
	
	@Override
	public State RightReleased() {
		return new DrivingForwardLeft();
	}
}