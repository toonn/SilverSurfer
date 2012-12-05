package communication;

import commands.Command;

import java.io.*;

import lejos.nxt.*;
import lejos.nxt.comm.*;

public class CommandUnit {

	private static final double LENGTH_COEF = 20.8; //Amount of degrees needed for 1 cm forward.
	private static final double ANGLE_COEF = 710; //Amount of degrees needed for a 360 degree turn.
    private static int NORMAL_SPEED = 180;
    private NXTConnection pcConnection;
    private DataInputStream dis;
    private DataOutputStream dos;
    private SensorThread ST;
	private BarcodeThread BT;
    private boolean quit = false;
    private UltrasonicSensor ultrasonicSensor;
    private LightSensor lightSensor;
    private TouchSensor touchSensor1;
    private TouchSensor touchSensor2;
    private boolean busy = false;
    private boolean readBarcodes = true;
    private double x = 220;
    private double y = 220;
    private double angle = 270;

    public CommandUnit() {
        ultrasonicSensor = new UltrasonicSensor(SensorPort.S1);
        lightSensor = new LightSensor(SensorPort.S2, false);
        touchSensor1 = new TouchSensor(SensorPort.S3);
        touchSensor2 = new TouchSensor(SensorPort.S4);
        Motor.A.setSpeed(CommandUnit.NORMAL_SPEED);
        Motor.B.setSpeed(CommandUnit.NORMAL_SPEED);

        waiting();
        System.out.println("Waiting...");
        pcConnection = Bluetooth.waitForConnection();
        System.out.println("Connected.");

        dis = pcConnection.openDataInputStream();
        dos = pcConnection.openDataOutputStream();

        quit = false;

        lightSensor.setFloodlight(true);

        ST = new SensorThread("ST");
        ST.setCommandUnit(this);
        ST.start();
    }

    public static void main(String[] args) throws IOException {
        CommandUnit CU = new CommandUnit();

        while (!(CU.quit)) {
            try {
                LCD.clear();
                CU.busy = false;
                CU.sendStringToUnit("[B] " + CU.busy);
                System.out.println("Waiting for input...");
                int input = CU.dis.readInt();
                CU.busy = true;
                CU.sendStringToUnit("[B] " + CU.busy);
                switch (input) {
                case (Command.CLOSE_CONNECTION):
                    CU.ST.setQuit(true);
                    CU.lightSensor.setFloodlight(false);
                    CU.quit = true;
                    break;
                case (Command.SLOW_SPEED):
                    CU.setSpeed(1);
                	CU.sendStringToUnit("[DON]");
                    break;
                case (Command.NORMAL_SPEED):
                    CU.setSpeed(2);
                	CU.sendStringToUnit("[DON]");
                    break;
                case (Command.FAST_SPEED):
                    CU.setSpeed(3);
            		CU.sendStringToUnit("[DON]");
                    break;
                case (Command.VERY_FAST_SPEED):
                    CU.setSpeed(4);
            		CU.sendStringToUnit("[DON]");
                    break;
                case (Command.ALIGN_PERPENDICULAR):
                    CU.updateCoordinates(15, 0);
                    CU.alignOnWhiteLine(CU.lightSensor.getLightValue() + 4);
                    CU.waiting();
                	CU.sendStringToUnit("[DON]");
                    break;
                case (Command.ALIGN_WALL):
                    CU.alignOnWalls();
                    CU.waiting();
                	CU.sendStringToUnit("[DON]");
                    break;
                case (Command.LOOK_AROUND):
                    CU.lookAround();
                    CU.waiting();
                	CU.sendStringToUnit("[DON]");
                    break;
                case (Command.PLAY_SONG):
    				SongThread ST = new SongThread();
    				ST.start();
                	CU.sendStringToUnit("[DON]");
    				break;
                case (Command.CHECK_OBSTRUCTIONS_AND_SET_TILE):
                    CU.sendStringToUnit("[CH] " + CU.ultrasonicSensor.getDistance());
            		CU.sendStringToUnit("[DON]");
                	break;
                case (Command.READ_BARCODES):
                    CU.readBarcodes = false;
            		CU.sendStringToUnit("[DON]");
                	break;
                default:
                    if (input % 100 == Command.AUTOMATIC_MOVE_FORWARD && input != Command.AUTOMATIC_MOVE_FORWARD) {
                        CU.updateCoordinates((input-Command.AUTOMATIC_MOVE_FORWARD)/100, 0);
                        int result = CU.moveForward((int)Math.round(LENGTH_COEF*(input-Command.AUTOMATIC_MOVE_FORWARD)/100));
    	    			if(result != 0)
    	    				CU.sendStringToUnit("[BC] " + result);
                        CU.waiting();
                    } else if (((input % 100 == Command.AUTOMATIC_TURN_ANGLE) || (input % 100 == -(100-Command.AUTOMATIC_TURN_ANGLE))) && input != Command.AUTOMATIC_TURN_ANGLE) {
                        CU.updateCoordinates(0, (input-Command.AUTOMATIC_TURN_ANGLE)/100);
                        CU.turnAngle((int)Math.round(ANGLE_COEF*(input-Command.AUTOMATIC_TURN_ANGLE)/100/360));
                        CU.waiting();
                    }
                	CU.sendStringToUnit("[DON]");
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
    
    private void waiting() {
        Motor.A.stop(true);
        Motor.B.stop();
    }

    public int getSpeed() {
        if (NORMAL_SPEED == 360)
            return 4;
        else if (NORMAL_SPEED == 270)
            return 3;
        else if (NORMAL_SPEED == 180)
            return 2;
        else
            return 1;
    }

    public void setSpeed(int speed) {
        if (speed == 1)
            NORMAL_SPEED = 90;
        else if (speed == 2)
            NORMAL_SPEED = 180;
        else if (speed == 3)
            NORMAL_SPEED = 270;
        else
            NORMAL_SPEED = 360;
		Motor.A.setSpeed(NORMAL_SPEED);
		Motor.B.setSpeed(NORMAL_SPEED);
    }

    public void sendStringToUnit(String info) {
        try {
            byte[] byteArray = info.getBytes();
            pcConnection.write(byteArray, byteArray.length);
        } catch (Exception e) {

        }
    }
    
    public void updateCoordinates(double length, double angle) {
    	if(angle == 0) {
    		x = x + length*Math.cos(Math.toRadians(this.angle));
    		y = y - length*Math.sin(Math.toRadians(this.angle));
            sendStringToUnit("[X] " + x);
            sendStringToUnit("[Y] " + y);
    	}    		
    	else {
    		this.angle = (this.angle + angle)%360;
            sendStringToUnit("[ANG] " + this.angle);
    	}
    }

    public void updateStatus() {
        sendStringToUnit("[US] " + ultrasonicSensor.getDistance());
        sendStringToUnit("[LS] " + lightSensor.getLightValue());
        sendStringToUnit("[TS1] " + touchSensor1.isPressed());
        sendStringToUnit("[TS2] " + touchSensor2.isPressed());
        sendStringToUnit("[LM] " + Motor.B.isMoving() + " " + Motor.B.getSpeed());
        sendStringToUnit("[RM] " + Motor.A.isMoving() + " " + Motor.A.getSpeed());
    }
    
    private int moveForward(int angle) {
        try {
        	if(readBarcodes) {
        		BT = new BarcodeThread("BT");
        		BT.setLightSensor(lightSensor);
        		BT.start();
        	}
        	Motor.A.rotate(angle, true);
        	Motor.B.rotate(angle);
        	if(readBarcodes) {
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
        	for(int i = 0; i<6; i++) {
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
    
    private void moveForwardWithoutBarcode(int angle) {
        Motor.A.rotate(angle, true);
        Motor.B.rotate(angle);
    }

    private void turnAngle(int angle) {
        Motor.A.rotate(-angle, true);
        Motor.B.rotate(angle);
    }

    private void alignOnWhiteLine(int treshold) {
    	if(treshold < 40)
    		treshold = 52;
    	int angle = 0;
        
		WhiteLineThread WLT = new WhiteLineThread("WLT");
		WLT.start();

		while(lightSensor.getLightValue() < treshold);
		while(lightSensor.getLightValue() >= treshold);
		
		WLT.setQuit(true);
		try {
			Thread.sleep(500);
		} catch(Exception e) {
			
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
    
    private void lookAround() {
    	int[] lookAroundResult = new int[4];
    	lookAroundResult[0] = ultrasonicSensor.getDistance();
    	turnAngle((int)ANGLE_COEF/4);
    	lookAroundResult[1] = ultrasonicSensor.getDistance();
    	turnAngle((int)ANGLE_COEF/4);
    	lookAroundResult[2] = ultrasonicSensor.getDistance();
    	turnAngle((int)ANGLE_COEF/4);
    	lookAroundResult[3] = ultrasonicSensor.getDistance();
    	turnAngle(-(int)ANGLE_COEF/4);
    	turnAngle(-(int)ANGLE_COEF/4);
    	turnAngle(-(int)ANGLE_COEF/4);
        sendStringToUnit("[LA0] " + lookAroundResult[0]);
        sendStringToUnit("[LA1] " + lookAroundResult[1]);
        sendStringToUnit("[LA2] " + lookAroundResult[2]);
        sendStringToUnit("[LA3] " + lookAroundResult[3]);
    }
}