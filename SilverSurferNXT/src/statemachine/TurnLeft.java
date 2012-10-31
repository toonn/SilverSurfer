package statemachine;

import communication.CommandUnit;

import lejos.nxt.Motor;

public class TurnLeft extends State {
	
	public TurnLeft() {
		Motor.A.setSpeed(CommandUnit.NORMAL_SPEED);
		Motor.B.setSpeed(CommandUnit.NORMAL_SPEED);
		Motor.A.forward();
		Motor.B.backward();
	}
	
	@Override
	public State ForwardPressed() {
		return new DrivingForwardLeft();
	}
	
	@Override
	public State ForwardReleased() {
		return new DrivingBackwardLeft();
	}
	
	@Override
	public State BackwardPressed() {
		return new DrivingBackwardLeft();
	}
	
	@Override
	public State BackwardReleased() {
		return new DrivingForwardLeft();
	}
	
	@Override
	public State LeftReleased() {
		return new Waiting();
	}
	
	@Override
	public State RightPressed() {
		return new Waiting();
	}
}