package statemachine;
import lejos.nxt.*;

public class DrivingForwardRight extends State {

	public DrivingForwardRight() {
		Motor.B.setSpeed(HIGH_SPEED);
		Motor.A.stop();
		Motor.B.forward();
	}
	
	public State ForwardReleased() {
		return new TurnRight();
	}
	
	public State BackwardPressed() {
		return new TurnRight();
	}
	
	public State LeftPressed() {
		return new DrivingForward();
	}
	
	public State RightReleased() {
		return new DrivingForward();
	}
}