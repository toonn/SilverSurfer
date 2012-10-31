package statemachine;

import communication.CommandUnit;

import lejos.nxt.*;

public class DrivingForwardRight extends State {

	public DrivingForwardRight() {
		Motor.B.setSpeed(CommandUnit.NORMAL_SPEED*2);
		Motor.A.stop();
		Motor.B.forward();
	}
	
	@Override
	public State ForwardReleased() {
		return new TurnRight();
	}
	
	@Override
	public State BackwardPressed() {
		return new TurnRight();
	}
	
	@Override
	public State LeftPressed() {
		return new DrivingForward();
	}
	
	@Override
	public State RightReleased() {
		return new DrivingForward();
	}
}