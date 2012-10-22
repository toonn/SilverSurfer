package statemachine;
import lejos.nxt.*;

public class DrivingBackwardLeft extends State {

	public DrivingBackwardLeft() {
		Motor.A.setSpeed(HIGH_SPEED);
		Motor.A.backward();
		Motor.B.stop();
	}
	
	public State ForwardPressed() {
		return new TurnLeft();
	}
	
	public State BackwardReleased() {
		return new TurnLeft();
	}
	
	public State LeftReleased() {
		return new DrivingBackward();
	}
	
	public State RightPressed() {
		return new DrivingBackward();
	}
}
