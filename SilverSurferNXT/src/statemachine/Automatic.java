package statemachine;

import communication.*;

import lejos.nxt.*;

public class Automatic extends State {

    public Automatic() {
        Motor.A.setSpeed(CommandUnit.NORMAL_SPEED);
        Motor.B.setSpeed(CommandUnit.NORMAL_SPEED);
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

    public void alignOnWhiteLine(LightSensor lightSensor, CommandUnit CU, int treshold) {
    	int angle = 0;
        
		WhiteLineThread WLTForward = new WhiteLineThread("WLTForward");
		WLTForward.setCommandUnit(CU);
		WLTForward.setStartState(1);
		WLTForward.start();

		while(lightSensor.getLightValue() < treshold);
		while(lightSensor.getLightValue() >= treshold);

		WLTForward.setQuit(true);

		while(lightSensor.getLightValue() < treshold) {
			turnAngle(-3);
		}
		while(lightSensor.getLightValue() >= treshold) {
			turnAngle(3);
			angle = angle + 3;
		}
		while(lightSensor.getLightValue() < treshold) {
			turnAngle(3);
			angle = angle + 3;
		}
		turnAngle(-(angle/2));
    }
}