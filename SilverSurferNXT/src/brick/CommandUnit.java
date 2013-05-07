package brick;

import commands.Command;
import threads.*;

import java.io.*;

import lejos.nxt.*;
import lejos.nxt.addon.IRSeekerV2;
import lejos.nxt.comm.*;

public class CommandUnit {

	private static final int WALL_DISTANCE = 15;
	private static final int WALL_DISTANCE_LIMIT = 21;
	private static final double LIGHT_SENSOR_DISTANCE = 6.5;
	private static final int WHITE_LINE_TRESHOLD = 52;
	private static final int BLACK_LINE_TRESHOLD = 40;
	private static final double LENGTH_COEF = 20.8; //Amount of degrees needed for 1 cm forward.
	private static final double ANGLE_COEF_RIGHT = 2.330; //Amount of degrees needed for a 1 degree turn to the right.
	private static final double ANGLE_COEF_LEFT = 2.330; //Amount of degrees needed for a 1 degree turn to the left.
    private static final int DEFAULTSPEED = 200;
    private static int speed = 200;
    
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
    public boolean interrupt = false;

    public CommandUnit() {
        ultrasonicSensor = new UltrasonicSensor(SensorPort.S1);
        lightSensor = new LightSensor(SensorPort.S4, false);
        infraredSensor = new IRSeekerV2(SensorPort.S2, IRSeekerV2.Mode.AC);
        Motor.A.setSpeed(CommandUnit.speed);
        Motor.B.setSpeed(CommandUnit.speed);
        Motor.C.setSpeed(CommandUnit.speed/8);


        /*lightSensor.setFloodlight(true);
    	InterruptThread IT = new InterruptThread(this, ultrasonicSensor, 20, LENGTH_COEF);
    	IT.start();
        ExecuteWhiteLine EWT = new ExecuteWhiteLine(this);
        EWT.start();*/
    	/*Motor.A.rotate((int)(LENGTH_COEF*20), true);
    	Motor.B.rotate((int)(LENGTH_COEF*20), true);
    	while(!quit) {
    		System.out.println(interrupt);
    	}*/
        
        /*
        lightSensor.setFloodlight(true);
        int result = moveForward((int)Math.floor(LENGTH_COEF*40));
        if(result == 2 || result == 16)
        	pickupObject();
        else if(infraredSensor.getSensorValue(3) < 70 && (result == 13 || result == 44))
        	crossOpenSeesaw();
        else if(result == 13 || result == 44)
        	crossClosedSeesaw();
        else {
        	readBarcodes = false;
        	turnAngle((int)(ANGLE_COEF_RIGHT*180));
            moveForwardWithoutBarcode((int)Math.floor(LENGTH_COEF*5));
        	alignOnWhiteLine();
        	readBarcodes = true;
        }
        */
        
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
        int resultAlign;
        
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
                case (Command.ALIGN_WHITE_LINE):
                    System.out.println("White line.");
                	InterruptThread IT = new InterruptThread(CU, CU.ultrasonicSensor, 20, LENGTH_COEF);
                	IT.start();
                    CU.updatePosition(40);
                    /*int resultAlign = CU.alignOnWhiteLine();
	    			if(resultAlign != -1)
	    				CU.sendStringToUnit("[BC] " + resultAlign);
                    CU.stopRobot();*/
                    ExecuteWhiteLine EWT = new ExecuteWhiteLine(CU);
                    EWT.start();
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
                case (Command.CROSS_OPEN_SEESAW):
                    System.out.println("Crossing open seesaw.");
                	CU.updatePosition(160);
                	resultAlign = CU.crossOpenSeesaw();
	    			if(resultAlign != -1)
	    				CU.sendStringToUnit("[BC] " + resultAlign);
                    CU.stopRobot();
                	break;
                case (Command.CROSS_CLOSED_SEESAW):
                    System.out.println("Crossing closed seesaw.");
            		CU.updatePosition(160);
                	resultAlign = CU.crossClosedSeesaw();
	    			if(resultAlign != -1)
	    				CU.sendStringToUnit("[BC] " + resultAlign);
                    CU.stopRobot();
                	break;
                case (Command.PICKUP_OBJECT):
                    System.out.println("Picking up object.");
        			CU.updatePosition(40);
                	CU.updateAngle(180);
            		CU.pickupObject();
                    CU.stopRobot();
                	break;
                case (Command.UNDO_ACTION):
                	;
                case (Command.IGNORE_COLLISION):
                	;
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
                    	if(angle >= 0) //Turn to the right
                    		CU.turnAngle((int)Math.floor(ANGLE_COEF_RIGHT*angle));
                    	else //Turn to the left
                    		CU.turnAngle((int)Math.ceil(ANGLE_COEF_LEFT*angle));
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

    public void sendStringToUnit(String info) {
    	if(dis != null && dos != null) {
            try {
                byte[] byteArray = info.getBytes();
                pcConnection.write(byteArray, byteArray.length);
            } catch (Exception e) {
            	System.out.println("Error in CommandUnit.sendStringToUnit(String info)!");
            }
    	}
    }
    
    public void stopRobot() {
        Motor.A.stop(true);
        Motor.B.stop();
    }

    private void setSpeed(int speedLevel) {
        if (speedLevel == 1)
            speed = DEFAULTSPEED/2;
        else if (speedLevel == 2)
        	speed = DEFAULTSPEED;
        else if (speedLevel == 3)
        	speed = 3*DEFAULTSPEED/2;
        else
        	speed = 2*DEFAULTSPEED;
		Motor.A.setSpeed(speed);
		Motor.B.setSpeed(speed);
        System.out.println("Speed: " + speedLevel);
    }
    
    public void updatePosition(double length) {
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
    	if (speed > DEFAULTSPEED) {
    		Motor.A.setSpeed(DEFAULTSPEED);
    		Motor.B.setSpeed(DEFAULTSPEED);
    	}
    	if(angle >= 0) {
            Motor.A.rotate(-absAngle, true);
            Motor.B.rotate(absAngle);
    	}
    	else {
            Motor.A.rotate(absAngle, true);
            Motor.B.rotate(-absAngle);
    	}
		Motor.A.setSpeed(speed);
		Motor.B.setSpeed(speed);
    	Motor.A.setAcceleration(6000);
    	Motor.A.setAcceleration(6000);
    }
    
    private void moveForwardWithoutBarcode(int distance) {
        Motor.A.rotate(distance, true);
        Motor.B.rotate(distance);
    }
    
    private int moveForward(int distance) {
    	if(readBarcodes) {
        	int distance1, distance2;
        	if(distance > 5) {
        		distance1 = 5;
        		distance2 = distance - 5;
        	}
        	else {
        		distance1 = 0;
        		distance2 = distance;
        	}
    		Motor.A.rotate(distance1, true);
    		Motor.B.rotate(distance1);
    		BT = new BarcodeThread("BT", lightSensor, LENGTH_COEF, 0, BLACK_LINE_TRESHOLD);
    		BT.start();
    		Motor.A.rotate(distance2, true);
    		Motor.B.rotate(distance2, true);
    		while(Motor.A.isMoving() && !BT.getFound());
    		stopRobot();
    		boolean found = BT.getFound();
    		BT.changeBool();
    		if(found) {
    			BT = new BarcodeThread("BT", lightSensor, LENGTH_COEF, 1, BLACK_LINE_TRESHOLD);
    			BT.start();
    			try {
    				Thread.sleep(500);
        			while(Motor.A.isMoving());
    				Thread.sleep(500);
    			} catch(Exception e) {
    				
    			}
    			return BT.getResult();
    		}
    	}
    	else
    		moveForwardWithoutBarcode(distance);
    	return -1;
    }

    public int alignOnWhiteLine() {
    	int initialTachoCount = Motor.A.getTachoCount();
		int distanceTravelled = (int)Math.round((Motor.A.getTachoCount() - initialTachoCount)/LENGTH_COEF);
    	WhitelineThread WT = new WhitelineThread("WT", ultrasonicSensor, LENGTH_COEF, ANGLE_COEF_RIGHT, ANGLE_COEF_LEFT, WALL_DISTANCE, WALL_DISTANCE_LIMIT, LIGHT_SENSOR_DISTANCE);
		WT.start();
		while(lightSensor.getLightValue() < WHITE_LINE_TRESHOLD && !interrupt);
		if(interrupt) {
			stopRobot();
			WT.continueAfterFirstQuit = false;
			WT.setFirstQuit(true);
			distanceTravelled = (int)Math.round((Motor.A.getTachoCount() - initialTachoCount)/LENGTH_COEF);
			moveForwardWithoutBarcode((int)Math.round(-distanceTravelled*LENGTH_COEF));
			interrupt = false;
			sendStringToUnit("[UA]");
			return -1;
		}
		while(lightSensor.getLightValue() >= WHITE_LINE_TRESHOLD && !interrupt);
		if(interrupt) {
			stopRobot();
			WT.continueAfterFirstQuit = false;
			WT.setFirstQuit(true);
			distanceTravelled = (int)Math.round((Motor.A.getTachoCount() - initialTachoCount)/LENGTH_COEF);
			moveForwardWithoutBarcode((int)Math.round(-distanceTravelled*LENGTH_COEF));
			interrupt = false;
			sendStringToUnit("[UA]");
			return -1;
		}
		try {
			Thread.sleep(35);
			while(lightSensor.getLightValue() >= WHITE_LINE_TRESHOLD && !interrupt);
			if(interrupt) {
				stopRobot();
				WT.continueAfterFirstQuit = false;
				WT.setFirstQuit(true);
				distanceTravelled = (int)Math.round((Motor.A.getTachoCount() - initialTachoCount)/LENGTH_COEF);
				moveForwardWithoutBarcode((int)Math.round(-distanceTravelled*LENGTH_COEF));
				interrupt = false;
				sendStringToUnit("[UA]");
				return -1;
			}
			WT.setFirstQuit(true);
			Thread.sleep(500);
		} catch(Exception e) {

		}
		while(lightSensor.getLightValue() < WHITE_LINE_TRESHOLD);
		WT.setSecondQuit(true);
		while(!WT.doneWithAligning);
		if(interrupt) {
			stopRobot();
			distanceTravelled = (int)Math.round((Motor.A.getTachoCount() - initialTachoCount)/LENGTH_COEF);
			moveForwardWithoutBarcode((int)Math.round(-distanceTravelled*LENGTH_COEF));
			interrupt = false;
			sendStringToUnit("[UA]");
			return -1;
		}
		while(WT.isAlive());	
		return moveForward((int)Math.round(20*LENGTH_COEF));
    }
    
    private int crossOpenSeesaw() {   
    	boolean readBarcodesBackup = readBarcodes;
    	readBarcodes = false;
    	moveForward((int)Math.floor(LENGTH_COEF*130));
        readBarcodes = readBarcodesBackup;
        return alignOnWhiteLine();
    }
    
    private int crossClosedSeesaw() {
    	boolean readBarcodesBackup = readBarcodes;
    	readBarcodes = false;
        moveForwardWithoutBarcode((int)Math.floor(LENGTH_COEF*-40));
        alignOnWhiteLine();
        turnAngle((int)(ANGLE_COEF_LEFT*180));
    	Motor.A.setSpeed(CommandUnit.speed/2);
        Motor.B.setSpeed(CommandUnit.speed/2);
        Motor.C.rotate(-75);
        Motor.C.rotate(-35, true);
        moveForwardWithoutBarcode((int)Math.floor(LENGTH_COEF*20));
        Motor.C.rotate(110, true);
        moveForwardWithoutBarcode((int)Math.floor(LENGTH_COEF*20));
    	Motor.A.setSpeed(CommandUnit.speed);
        Motor.B.setSpeed(CommandUnit.speed);
        turnAngle((int)(ANGLE_COEF_LEFT*180));
        alignOnWhiteLine();
        readBarcodes = readBarcodesBackup;
        return crossOpenSeesaw();
    }
    
    private void pickupObject() {
    	boolean readBarcodesBackup = readBarcodes;
    	readBarcodes = false;
        turnAngle((int)(ANGLE_COEF_LEFT*180));
        Motor.A.setSpeed(CommandUnit.speed/2);
        Motor.B.setSpeed(CommandUnit.speed/2);
        Motor.C.rotate(-120);
        Motor.A.rotate((int)Math.floor(LENGTH_COEF*-20), true);
        Motor.B.rotate((int)Math.floor(LENGTH_COEF*-20));
        Motor.C.rotate(120, true); //TODO: geeft geen problemen?
        Motor.A.rotate((int)Math.floor(LENGTH_COEF*25), true);
        Motor.B.rotate((int)Math.floor(LENGTH_COEF*25));
        Motor.A.rotate((int)Math.floor(LENGTH_COEF*-40), true);
        Motor.B.rotate((int)Math.floor(LENGTH_COEF*-40));
    	Motor.A.setSpeed(CommandUnit.speed);
        Motor.B.setSpeed(CommandUnit.speed);
        readBarcodes = readBarcodesBackup;
    }
}