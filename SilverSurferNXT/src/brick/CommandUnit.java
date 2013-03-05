package brick;

import commands.Command;
import threads.*;

import java.io.*;

import lejos.nxt.*;
import lejos.nxt.addon.IRSeekerV2;
import lejos.nxt.comm.*;

public class CommandUnit {

	private static final double LENGTH_COEF = 20.8; //Amount of degrees needed for 1 cm forward.
	private static final double ANGLE_COEF = 710; //Amount of degrees needed for a 360 degree turn.
    private static int SPEED = 180;
    
    private double x = 20;
    private double y = 20;
    private double angle = 270; //Northside of the screen
    
    private NXTConnection pcConnection;
    private DataInputStream dis;
    private DataOutputStream dos;
    private UltrasonicSensor ultrasonicSensor;
    private LightSensor lightSensor;
    private IRSeekerV2 infraredSensor;
    private SensorThread ST;
	private BarcodeThread BT;
	
    private boolean quit = false; //Stop the program when this is true.
    private boolean readBarcodes = true; //Only read barcodes when this is true.
    private boolean permaBarcodeStop = false; //Do not read any barcode when this is true.

    public CommandUnit() {
        ultrasonicSensor = new UltrasonicSensor(SensorPort.S1);
        lightSensor = new LightSensor(SensorPort.S4, false);
        infraredSensor = new IRSeekerV2(SensorPort.S2, IRSeekerV2.Mode.AC);
        Motor.A.setSpeed(CommandUnit.SPEED);
        Motor.B.setSpeed(CommandUnit.SPEED);
        
        //testPickup();
        
        stopRobot();
        System.out.println("Waiting...");
        pcConnection = Bluetooth.waitForConnection();
        System.out.println("Connected.");

        dis = pcConnection.openDataInputStream();
        dos = pcConnection.openDataOutputStream();

        quit = false;

        lightSensor.setFloodlight(true);

        ST = new SensorThread("ST", this);
        ST.start();
    }
    
    private void testPickup() {
        lightSensor.setFloodlight(true);
        try {
        	Thread.sleep(1000);
        } catch(Exception e) {
        	
        }
        readBarcodes = false;
        moveForward((int)(32*LENGTH_COEF));
    	moveForward((int)(-10*LENGTH_COEF));
    	turnAngle((int)(ANGLE_COEF/2));
    	readBarcodes = true;
    }

    public static void main(String[] args) throws IOException {
        CommandUnit CU = new CommandUnit();

        while (!(CU.quit)) {
            try {
                LCD.clear();
                CU.sendStringToUnit("[B]");
                System.out.println("Waiting for input...");
                int input = CU.dis.readInt();
                switch (input) {
                case (Command.CLOSE_CONNECTION):
                    CU.ST.setQuit(true);
                    CU.lightSensor.setFloodlight(false);
                    CU.quit = true;
                    break;
                case (Command.SLOW_SPEED):
                    CU.setSpeed(1);
                    break;
                case (Command.NORMAL_SPEED):
                    CU.setSpeed(2);
                    break;
                case (Command.FAST_SPEED):
                    CU.setSpeed(3);
                    break;
                case (Command.VERY_FAST_SPEED):
                    CU.setSpeed(4);
                    break;
                case (Command.ALIGN_PERPENDICULAR):
                    CU.updatePosition(20);
                    CU.alignOnWhiteLine();
                    CU.stopRobot();
                    break;
                case (Command.ALIGN_WALL):
                    CU.alignOnWalls();
                    CU.stopRobot();
                    break;
                case (Command.CHECK_FOR_OBSTRUCTION):
                    CU.sendStringToUnit("[CFO] " + CU.ultrasonicSensor.getDistance());
                	break;
                case (Command.START_READING_BARCODES):
                    CU.readBarcodes = true;
                	break;
                case (Command.STOP_READING_BARCODES):
                    CU.readBarcodes = false;
                	break;
                case (Command.PERMA_STOP_READING_BARCODES):
                    CU.permaBarcodeStop = true;
                	break;
                default:
                    if (input % 100 == Command.AUTOMATIC_MOVE_FORWARD && input != Command.AUTOMATIC_MOVE_FORWARD) {
                        CU.updatePosition((input-Command.AUTOMATIC_MOVE_FORWARD)/100);
                        int result = CU.moveForward((int)Math.round(LENGTH_COEF*(input-Command.AUTOMATIC_MOVE_FORWARD)/100));
    	    			if(result != 0)
    	    				CU.sendStringToUnit("[BC] " + result);
                        CU.stopRobot();
                    } else if (((input % 100 == Command.AUTOMATIC_TURN_ANGLE) || (input % 100 == -(100-Command.AUTOMATIC_TURN_ANGLE))) && input != Command.AUTOMATIC_TURN_ANGLE) {
                        CU.updateAngle((input-Command.AUTOMATIC_TURN_ANGLE)/100);
                        CU.turnAngle((int)Math.round(ANGLE_COEF*(input-Command.AUTOMATIC_TURN_ANGLE)/100/360));
                        CU.stopRobot();
                    }
                	break;
                }
            } catch (Exception e) {
            	System.out.println("Error in CommandUnit.main(String[] args)!");
            }
        }

        CU.dis.close();
        CU.dos.close();
        CU.pcConnection.close();
    }

    private void sendStringToUnit(String info) {
        try {
            byte[] byteArray = info.getBytes();
            pcConnection.write(byteArray, byteArray.length);
        } catch (Exception e) {
        	System.out.println("Error in CommandUnit.sendStringToUnit(String info)!");
        }
    }
    
    private void stopRobot() {
        Motor.A.stop(true);
        Motor.B.stop();
    }

    private int getSpeed() {
        if (SPEED == 360)
            return 4;
        else if (SPEED == 270)
            return 3;
        else if (SPEED == 180)
            return 2;
        else
            return 1;
    }

    private void setSpeed(int speed) {
        if (speed == 1)
            SPEED = 90;
        else if (speed == 2)
            SPEED = 180;
        else if (speed == 3)
            SPEED = 270;
        else
            SPEED = 360;
		Motor.A.setSpeed(SPEED);
		Motor.B.setSpeed(SPEED);
    }
    
    private void updatePosition(double length) {
    	x = x + length*Math.cos(Math.toRadians(this.angle));
    	y = y - length*Math.sin(Math.toRadians(this.angle));
    	sendStringToUnit("[X] " + x);
    	sendStringToUnit("[Y] " + y);
    }
    
    private void updateAngle(double angle) {
    	this.angle = (this.angle + angle)%360;
    	sendStringToUnit("[ANG] " + this.angle);
    }

    public void updateStatus() {
        sendStringToUnit("[US] " + ultrasonicSensor.getDistance());
        sendStringToUnit("[LS] " + lightSensor.getLightValue());
        sendStringToUnit("[IS] " + infraredSensor.getSensorValue(3));
        sendStringToUnit("[LM] " + Motor.B.isMoving() + " " + Motor.B.getSpeed());
        sendStringToUnit("[RM] " + Motor.A.isMoving() + " " + Motor.A.getSpeed());
    }

    private void turnAngle(int angle) {
    	if (SPEED > 180) {
    		Motor.A.setSpeed(180);
    		Motor.B.setSpeed(180);
    	}
        Motor.A.rotate(-angle, true);
        Motor.B.rotate(angle);
		Motor.A.setSpeed(SPEED);
		Motor.B.setSpeed(SPEED);
    }
    
    private void moveForwardWithoutBarcode(int angle) {
        Motor.A.rotate(angle, true);
        Motor.B.rotate(angle);
    }
    
    private int moveForward(int angle) {
        try {
        	if(readBarcodes && !permaBarcodeStop) {
        		BT = new BarcodeThread("BT", lightSensor);
        		BT.start();
        	}
        	Motor.A.rotate(angle, true);
        	Motor.B.rotate(angle);
        	if(readBarcodes && !permaBarcodeStop) {
        		Thread.sleep(500);
        		boolean found = BT.getFound();
        		BT.setQuit(true);
        		if(found) {
        			Thread.sleep(500);
        			return readBarcode();
        		}
        	}
        } catch(Exception e) {
        	System.out.println("Error in CommandUnit.moveForward(int angle)!");
        }
        return 0;
    }
    
    private int readBarcode() {
    	int backupSpeed = getSpeed();
    	setSpeed(1);
    	String result = "";
    	if(lightSensor.getLightValue() < 40) {
        	moveForwardWithoutBarcode((int)Math.round(2*LENGTH_COEF));
        	for(int i = 0; i < 6; i++) {
        		if(lightSensor.getLightValue() < 40) 
        			result = result + "0";
        		else
        			result = result + "1";
            	moveForwardWithoutBarcode((int)Math.round(2*LENGTH_COEF));
        	}
        	moveForwardWithoutBarcode((int)Math.round(2*LENGTH_COEF));
    	}
    	alignOnWalls();
    	setSpeed(backupSpeed);
    	Byte byteResult = Byte.valueOf(result, 2);
    	return Integer.valueOf(byteResult.intValue());
    }

    private void alignOnWhiteLine() {
    	int treshold = 51;
    	int angle = 0;
        
		MoveThread MT = new MoveThread("MT", 0);
		MT.start();
		
		for(int i = 0; i < 7; i++) {
			try {
				Thread.sleep(100);
			} catch(Exception e) {
	        	System.out.println("Error in CommandUnit.alignOnWhiteLine()!");
			}
			while(lightSensor.getLightValue() > treshold || lightSensor.getLightValue() < 40);
		}
		while(lightSensor.getLightValue() < treshold);
		while(lightSensor.getLightValue() >= treshold);
		
		MT.setQuit(true);
		try {
			Thread.sleep(500);
		} catch(Exception e) {
        	System.out.println("Error in CommandUnit.alignOnWhiteLine()!");
		}
		
		moveForwardWithoutBarcode((int)Math.round(3*LENGTH_COEF));

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
    
    private void alignOnWalls() {
    	int firstUSRead;
    	int secondUSRead;
    	
    	turnAngle((int)ANGLE_COEF/4);
    	firstUSRead = ultrasonicSensor.getDistance();
    	if (firstUSRead < 28) {
    		moveForwardWithoutBarcode((int)Math.round((firstUSRead-23)*LENGTH_COEF));
        	turnAngle(-(int)ANGLE_COEF/4);
        	turnAngle(-(int)ANGLE_COEF/4);
    		secondUSRead = ultrasonicSensor.getDistance();
    		if(!(secondUSRead < 25 && secondUSRead > 21) && secondUSRead < 28)
        		moveForwardWithoutBarcode((int)Math.round((secondUSRead-23)*LENGTH_COEF));
    		turnAngle((int)ANGLE_COEF/4);
    	}
    	else {
        	turnAngle(-(int)ANGLE_COEF/4);
    		turnAngle(-(int)ANGLE_COEF/4);
    		secondUSRead = ultrasonicSensor.getDistance();
    		if (secondUSRead < 28) {
        		moveForwardWithoutBarcode((int)Math.round((secondUSRead-23)*LENGTH_COEF));
        		turnAngle((int)ANGLE_COEF/4);
    		}
    		else 
        		turnAngle((int)ANGLE_COEF/4);
    	}
    }
}