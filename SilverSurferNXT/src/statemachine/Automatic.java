package statemachine;

import communication.*;

import lejos.nxt.*;

public class Automatic extends State {
	
	BarcodeThread BT;
	CommandUnit CU;
    
	public Automatic() {
        Motor.A.setSpeed(CommandUnit.NORMAL_SPEED);
        Motor.B.setSpeed(CommandUnit.NORMAL_SPEED);
        Motor.A.stop(true);
        Motor.B.stop();
    }

    public int moveForward(int angle, LightSensor lightSensor, UltrasonicSensor ultrasonicSensor) {
        try {
        	BT = new BarcodeThread("BT");
        	BT.setLightSensor(lightSensor);
        	BT.start();
        	Motor.A.rotate(angle, true);
        	Motor.B.rotate(angle);
        	Thread.sleep(500);
        	boolean found = BT.getFound();
        	BT.setQuit(true);
        	if(found) {
        		Thread.sleep(500);
        		return readBarcode(lightSensor);
        	}
        } catch(Exception e) {
        	System.out.println("Error in Automatic.moveForward()!");
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
        
		WhiteLineThread WLT = new WhiteLineThread("WLT");
		WLT.setCommandUnit(CU);
		WLT.start();

		while(lightSensor.getLightValue() < treshold);
		while(lightSensor.getLightValue() >= treshold);
		
		try {
			Thread.sleep(300);
		} catch(Exception e) {
			
		}
		
		WLT.setQuit(true);

		while(lightSensor.getLightValue() < treshold)
			turnAngle(-3);
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
    
    public boolean alignOnWall(UltrasonicSensor ultrasonicSensor) {
    	int firstUSRead;
    	int secondUSRead;
    	
    	turnAngle(179); //90 graden
    	firstUSRead = ultrasonicSensor.getDistance();
    	if (firstUSRead < 30) {
    		moveForwardWithoutBarcode((int)Math.round((firstUSRead-23)*20.8));
        	turnAngle(-179);
        	turnAngle(-179);
    		secondUSRead = ultrasonicSensor.getDistance();
    		if(!(secondUSRead < 25 && secondUSRead > 21) && secondUSRead < 30)
        		moveForwardWithoutBarcode((int)Math.round((secondUSRead-23)*20.8));
    		turnAngle(179);
    		return true;
    	}
    	else {
        	turnAngle(-179);
    		turnAngle(-179);
    		secondUSRead = ultrasonicSensor.getDistance();
    		if (secondUSRead < 30) {
        		moveForwardWithoutBarcode((int)Math.round((secondUSRead-23)*20.8));
        		turnAngle(179);
    			return true;
    		}
    		else 
        		turnAngle(179);
    			return false;
    	}
    }
    
    public int[] lookAround(UltrasonicSensor ultrasonicSensor) {
    	int[] lookAroundResult = new int[4];
    	lookAroundResult[0] = ultrasonicSensor.getDistance();
    	turnAngle(179);
    	lookAroundResult[1] = ultrasonicSensor.getDistance();
    	turnAngle(179);
    	lookAroundResult[2] = ultrasonicSensor.getDistance();
    	turnAngle(179);
    	lookAroundResult[3] = ultrasonicSensor.getDistance();
    	turnAngle(-179);
    	turnAngle(-179);
    	turnAngle(-179);
    	return lookAroundResult;
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
        	}
        	moveForwardWithoutBarcode((int)Math.round(2 * 20.8));
    	}
    	Byte byteResult = Byte.valueOf(result, 2);
    	return Integer.valueOf(byteResult.intValue());
    }
}