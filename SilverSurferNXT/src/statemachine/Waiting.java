package statemachine;

import communication.CommandUnit;

import lejos.nxt.*;

public class Waiting extends State {
	
	public Waiting() {
		Motor.A.setSpeed(CommandUnit.NORMAL_SPEED);
		Motor.B.setSpeed(CommandUnit.NORMAL_SPEED);
		Motor.A.stop(true);
		Motor.B.stop();
	}
	
	@Override
	public State ForwardPressed() {
		return new DrivingForward();
	}
	
	@Override
	public State ForwardReleased() {
		return new DrivingBackward();
	}
	
	@Override
	public State BackwardPressed() {
		return new DrivingBackward();
	}
	
	@Override
	public State BackwardReleased() {
		return new DrivingForward();
	}
	
	@Override
	public State LeftPressed() {
		return new TurnLeft();
	}
	
	@Override
	public State LeftReleased() {
		return new TurnRight();
	}
	
	@Override
	public State RightPressed() {
		return new TurnRight();
	}
	
	@Override
	public State RightReleased() {
		return new TurnLeft();
	}
}