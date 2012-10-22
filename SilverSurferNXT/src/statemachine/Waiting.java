package statemachine;
import lejos.nxt.*;

public class Waiting extends State {
	
	public Waiting() {
		Motor.A.setSpeed(NORMAL_SPEED);
		Motor.B.setSpeed(NORMAL_SPEED);
		Motor.A.stop(true);
		Motor.B.stop();
	}
	
	public State ForwardPressed() {
		return new DrivingForward();
	}
	
	public State ForwardReleased() {
		return new DrivingBackward();
	}
	
	public State BackwardPressed() {
		return new DrivingBackward();
	}
	
	public State BackwardReleased() {
		return new DrivingForward();
	}
		
	public State LeftPressed() {
		return new TurnLeft();
	}
	
	public State LeftReleased() {
		return new TurnRight();
	}
	
	public State RightPressed() {
		return new TurnRight();
	}
	
	public State RightReleased() {
		return new TurnLeft();
	}
}