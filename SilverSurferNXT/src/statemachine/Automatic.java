package statemachine;

import communication.*;

import lejos.nxt.*;

public class Automatic extends State {
	
	BarcodeThread BT;
    
	public Automatic() {
        Motor.A.setSpeed(CommandUnit.NORMAL_SPEED);
        Motor.B.setSpeed(CommandUnit.NORMAL_SPEED);
        Motor.A.stop(true);
        Motor.B.stop();
    }

    public int moveForward(int angle, LightSensor lightSensor, UltrasonicSensor ultrasonicSensor) {
    	BT = new BarcodeThread("BT");
    	BT.setLightSensor(lightSensor);
    	BT.start();
        Motor.A.rotate(angle, true);
        Motor.B.rotate(angle);
        try {
			Thread.sleep(500);
        } catch(Exception e) {
        	
        }
        boolean found = BT.getFound();
        BT.setQuit(true);
        try {
			Thread.sleep(500);
        } catch(Exception e) {
        	
        }
        if(found) {
        	boolean check = searchWalls(ultrasonicSensor);
        	if(check)
        		return readBarcode(lightSensor);
        }
        return 0;
    }
    
    public void moveForwardWithoutBarcode(int angle) {
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
    		moveForwardWithoutBarcode((int)Math.round((firstUSRead-23)*20.8));
        	turnAngle(-179);
        	turnAngle(-179);
    		secondUSRead = ultrasonicSensor.getDistance();
    		if(!(secondUSRead < 25 && secondUSRead > 21) && secondUSRead < 32)
        		moveForwardWithoutBarcode((int)Math.round((secondUSRead-23)*20.8));
    		turnAngle(179);
    		return true;
    	}
    	else {
        	turnAngle(-179);
    		turnAngle(-179);
    		secondUSRead = ultrasonicSensor.getDistance();
    		if (secondUSRead < 32) {
        		moveForwardWithoutBarcode((int)Math.round((secondUSRead-23)*20.8));
        		turnAngle(179);
    			return true;
    		}
    		else 
        		turnAngle(179);
    			return false;
    	}
    }
    
    public String lookAround(UltrasonicSensor ultrasonicSensor) {
    	String front = "no wall";
    	String back = "no wall";
    	String left = "no wall";
    	String right = "no wall";
    	
    	if(ultrasonicSensor.getDistance() < 32)
    		front = "wall";
    	turnAngle(179);
    	if(ultrasonicSensor.getDistance() < 32)
    		right = "wall";
    	turnAngle(179);
    	if(ultrasonicSensor.getDistance() < 32)
    		back = "wall";
    	turnAngle(179);
    	if(ultrasonicSensor.getDistance() < 32)
    		left = "wall";
    	turnAngle(-179);
    	turnAngle(-179);
    	turnAngle(-179);
    	return "Front: " + front + ", back: " + back + ", left: " + left + ", right: " + right;
    } 
    
    public boolean searchWalls(UltrasonicSensor ultrasonicSensor) {
    	turnAngle(179);
    	boolean rightWall = (ultrasonicSensor.getDistance() < 32);
    	turnAngle(-179);
    	turnAngle(-179);
    	boolean leftWall = (ultrasonicSensor.getDistance() < 32);
    	turnAngle(179);
    	return rightWall && leftWall;
    }
    
    public int readBarcode(LightSensor lightSensor) {
    	String result = "";
    	if(lightSensor.getLightValue() < 40) {
        	moveForwardWithoutBarcode((int)Math.round(2 * 20.8));
        	for(int i = 0; i<6; i++) {
        		if(lightSensor.getLightValue() < 40) 
        			result = result + "0";
        		else
        			result = result + "1";
            	moveForwardWithoutBarcode((int)Math.round(2 * 20.8));
            	System.out.println(result);
        	}
        	moveForwardWithoutBarcode((int)Math.round(2 * 20.8));
    	}
    	Byte byteResult = Byte.valueOf(result, 2);
    	return Integer.valueOf(byteResult.intValue());
    }
}