package statemachine;

import communication.CommandUnit;

import lejos.nxt.*;

public class DrivingForwardLeft extends State {

	public DrivingForwardLeft() {
		Motor.A.setSpeed(CommandUnit.NORMAL_SPEED*2);
		Motor.A.forward();
		Motor.B.stop();
	}
	
	@Override
	public State ForwardReleased() {
		return new TurnLeft();
	}
	
	@Override
	public State BackwardPressed() {
		return new TurnLeft();
	}
	
	@Override
	public State LeftReleased() {
		return new DrivingForward();
	}
	
	@Override
	public State RightPressed() {
		return new DrivingForward();
	}
}