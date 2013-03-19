package brick;

import commands.Command;
import threads.*;

import java.io.*;

import lejos.nxt.*;
import lejos.nxt.addon.IRSeekerV2;
import lejos.nxt.comm.*;

public class CommandUnit {

	private static final double LENGTH_COEF = 20.8; //Amount of degrees needed for 1 cm forward.
	private static final double ANGLE_COEF = 1.65; //Amount of degrees needed for a 1 degree turn.
    private static int SPEED = 200;
    
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

    public static void main(String[] args) throws IOException {
        CommandUnit CU = new CommandUnit();
        
        while (!(CU.quit)) {
            try {
                CU.sendStringToUnit("[B]");
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
                    System.out.println("White line.");
                    CU.updatePosition(20);
                    CU.alignOnWhiteLine();
                    CU.stopRobot();
                    break;
                case (Command.ALIGN_WALL):
                    System.out.println("Walls.");
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
                    	double distance = (input-Command.AUTOMATIC_MOVE_FORWARD)/100;
                        System.out.println(distance + " cm.");
                        CU.updatePosition(distance);
                        int result = CU.moveForward((int)Math.floor(LENGTH_COEF*distance));
    	    			if(result != -1)
    	    				CU.sendStringToUnit("[BC] " + result);
                        CU.stopRobot();
                    } else if (((input % 100 == Command.AUTOMATIC_TURN_ANGLE) || (input % 100 == -(100-Command.AUTOMATIC_TURN_ANGLE))) && input != Command.AUTOMATIC_TURN_ANGLE) {
                    	double angle = (input-Command.AUTOMATIC_TURN_ANGLE)/100;
                        System.out.println(angle + " degrees.");
                        CU.updateAngle(angle);
                    	if(angle >= 0)
                    		CU.turnAngle((int)Math.floor(ANGLE_COEF*angle));
                    	else
                    		CU.turnAngle((int)Math.ceil(ANGLE_COEF*angle));
                        CU.stopRobot();
                    }
                	break;
                }
            } catch (Exception e) {
            	System.out.println("Error in CommandUnit.main(String[] args)!");
                CU.ST.setQuit(true);
                CU.lightSensor.setFloodlight(false);
                CU.quit = true;
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

    private void setSpeed(int speed) {
        if (speed == 1)
            SPEED = 100;
        else if (speed == 2)
            SPEED = 200;
        else if (speed == 3)
            SPEED = 300;
        else
            SPEED = 400;
		Motor.A.setSpeed(SPEED);
		Motor.B.setSpeed(SPEED);
        System.out.println("Speed: " + speed);
    }
    
    private void updatePosition(double length) {
    	x = x + length*Math.cos(Math.toRadians(this.angle));
    	y = y - length*Math.sin(Math.toRadians(this.angle));
    	sendStringToUnit("[X] " + x);
    	sendStringToUnit("[Y] " + y);
    }
    
    private void updateAngle(double angle) {
    	this.angle = (this.angle + angle)%360;
    	if(this.angle < 0)
    		this.angle = this.angle + 360;
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
    	Motor.A.setAcceleration(1000);
    	Motor.A.setAcceleration(1000);
    	int absAngle = Math.abs(angle);
    	if (SPEED > 200) {
    		Motor.A.setSpeed(200);
    		Motor.B.setSpeed(200);
    	}
    	if(angle >= 0) {
            Motor.A.rotate(-absAngle, true);
            Motor.B.rotate(absAngle);
    	}
    	else {
            Motor.A.rotate(absAngle, true);
            Motor.B.rotate(-absAngle);
    	}
		Motor.A.setSpeed(SPEED);
		Motor.B.setSpeed(SPEED);
    	Motor.A.setAcceleration(6000);
    	Motor.A.setAcceleration(6000);
    }
    
    private void moveForwardWithoutBarcode(int distance) {
        Motor.A.rotate(distance, true);
        Motor.B.rotate(distance);
    }
    
    private int moveForward(int distance) {
    	if(readBarcodes && !permaBarcodeStop) {
    		BT = new BarcodeThread("BT", distance, lightSensor, LENGTH_COEF);
    		BT.start();
    		while(BT.isAlive() && lightSensor.getLightValue() >= 40);
    		if(BT.isAlive()) {
    			BT.changeBool();
    			while(!BT.getBool());
    			alignOnWalls();
    			return BT.getResult();
    		}
    	}
    	else
    		moveForwardWithoutBarcode(distance);
    	return -1;
    }

    private void alignOnWhiteLine() {
    	int treshold = 51;
    	WhitelineThread WT = new WhitelineThread("WT", LENGTH_COEF, ANGLE_COEF);
		WT.start();		
		while(lightSensor.getLightValue() < treshold);
		while(lightSensor.getLightValue() >= treshold);
		WT.setFirstQuit(true);
		while(lightSensor.getLightValue() < treshold);
		WT.setSecondQuit(true);
		while(WT.isAlive());
    }
    
    private void alignOnWalls() {
    	turnAngle((int)(ANGLE_COEF*90));
    	int firstUSRead = ultrasonicSensor.getDistance();
    	int secondUSRead;
    	if (firstUSRead < 28) {
    		moveForwardWithoutBarcode((int)Math.round((firstUSRead-23)*LENGTH_COEF));
        	turnAngle(-(int)(ANGLE_COEF*180));
    		secondUSRead = ultrasonicSensor.getDistance();
    		if(!(secondUSRead < 25 && secondUSRead > 21) && secondUSRead < 28)
        		moveForwardWithoutBarcode((int)Math.round((secondUSRead-23)*LENGTH_COEF));
    		turnAngle((int)(ANGLE_COEF*90));
    	}
    	else {
        	turnAngle(-(int)(ANGLE_COEF*180));
    		secondUSRead = ultrasonicSensor.getDistance();
    		if (secondUSRead < 28) {
        		moveForwardWithoutBarcode((int)Math.round((secondUSRead-23)*LENGTH_COEF));
        		turnAngle((int)(ANGLE_COEF*90));
    		}
    		else 
        		turnAngle((int)(ANGLE_COEF*90));
    	}
    }
}