package statemachine;
import lejos.nxt.Motor;

public class Automatic extends State {
	
	public Automatic() {
		Motor.A.setSpeed(NORMAL_SPEED);
		Motor.B.setSpeed(NORMAL_SPEED);
		Motor.A.stop(true);
		Motor.B.stop();
	}
	
	public void moveForward(int angle) {
		Motor.A.rotate(angle, true);
		Motor.B.rotate(angle);
	}
	
	public void turnAngle(int angle) {
		Motor.A.rotate(-angle, true);
		Motor.B.rotate(angle);
	}
}