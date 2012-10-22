package statemachine;
import lejos.nxt.Motor;

public class DrivingForwardLeft extends State {

	public DrivingForwardLeft() {
		Motor.A.setSpeed(HIGH_SPEED);
		Motor.A.forward();
		Motor.B.stop();
	}
	
	public State ForwardReleased() {
		return new TurnLeft();
	}
	
	public State BackwardPressed() {
		return new TurnLeft();
	}
	
	public State LeftReleased() {
		return new DrivingForward();
	}
	
	public State RightPressed() {
		return new DrivingForward();
	}
}