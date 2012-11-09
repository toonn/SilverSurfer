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
		//moveForward(541); //Lengthcoef (20.8) * lengte tot midden volgende vak (26 cm) afgerond
    }
    
    public boolean alignOnWall(UltrasonicSensor ultrasonicSensor) {
    	int firstUSRead;
    	int secondUSRead;
    	
    	turnAngle(179); //90 graden
    	firstUSRead = ultrasonicSensor.getDistance();
    	if (firstUSRead < 32) {
    		moveForward((int)Math.round((firstUSRead-23)*20.8));
        	turnAngle(-179);
        	turnAngle(-179);
    		secondUSRead = ultrasonicSensor.getDistance();
    		if(!(secondUSRead < 25 && secondUSRead > 21))
        		moveForward((int)Math.round((secondUSRead-23)*20.8));
    		turnAngle(179);
    		return true;
    	}
    	else {
        	turnAngle(-179);
    		turnAngle(-179);
    		secondUSRead = ultrasonicSensor.getDistance();
    		if (secondUSRead < 32) {
        		moveForward((int)Math.round((secondUSRead-23)*20.8));
        		turnAngle(179);
    			return true;
    		}
    		else 
        		turnAngle(179);
    			return false;
    	}
    }
}