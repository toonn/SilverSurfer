/*package statemachine;

import communication.CommandUnit;

import lejos.nxt.*;

public class TurnRight extends State {

	public TurnRight() {
		Motor.A.setSpeed(CommandUnit.NORMAL_SPEED);
		Motor.B.setSpeed(CommandUnit.NORMAL_SPEED);
		Motor.A.backward();
		Motor.B.forward();
	}
	
	@Override
	public State ForwardPressed() {
		return new DrivingForwardRight();
	}
	
	@Override
	public State ForwardReleased() {
		return new DrivingBackwardRight();
	}
	
	@Override
	public State BackwardPressed() {
		return new DrivingBackwardRight();
	}
	
	@Override
	public State BackwardReleased() {
		return new DrivingForwardRight();
	}
	
	@Override
	public State LeftPressed() {
		return new Waiting();
	}
	
	@Override
	public State RightReleased() {
		return new Waiting();
	}
}*/