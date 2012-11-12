package statemachine;

import communication.CommandUnit;

import lejos.nxt.*;

public class TurnLeft extends State {
	SongThread playSong = new SongThread();
	
	public TurnLeft() {
		
		playSong.start();
		Motor.A.setSpeed(CommandUnit.NORMAL_SPEED);
		Motor.B.setSpeed(CommandUnit.NORMAL_SPEED);
		Motor.A.forward();
		Motor.B.backward();
	}
	
	@Override
	public State ForwardPressed() {
		playSong.flag = false;
		return new DrivingForwardLeft();
	}
	
	@Override
	public State ForwardReleased() {
		playSong.flag = false;
		return new DrivingBackwardLeft();
	}
	
	@Override
	public State BackwardPressed() {
		playSong.flag = false;
		return new DrivingBackwardLeft();
	}
	
	@Override
	public State BackwardReleased() {
		playSong.flag = false;
		return new DrivingForwardLeft();
	}
	
	@Override
	public State LeftReleased() {
		playSong.flag = false;
		return new Waiting();
	}
	
	@Override
	public State RightPressed() {
		playSong.flag = false;
		return new Waiting();
	}
}