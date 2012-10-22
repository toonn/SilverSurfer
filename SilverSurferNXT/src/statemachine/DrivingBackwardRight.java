package statemachine;
import lejos.nxt.*;

public class DrivingBackwardRight extends State {

	public DrivingBackwardRight() {
		Motor.B.setSpeed(HIGH_SPEED);
		Motor.A.stop();
		Motor.B.backward();
	}
	
	public State ForwardPressed() {
		return new TurnRight();
	}
	
	public State BackwardReleased() {
		return new TurnRight();
	}
	
	public State LeftPressed() {
		return new DrivingBackward();
	}
	
	public State RightReleased() {
		return new DrivingBackward();
	}
}
