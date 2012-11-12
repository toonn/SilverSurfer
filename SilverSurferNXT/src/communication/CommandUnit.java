package communication;

import statemachine.*;
import commands.Command;

import java.io.*;

import lejos.nxt.*;
import lejos.nxt.addon.IRSeekerV2.Mode;
import lejos.nxt.comm.*;

public class CommandUnit {
	
	public State currentState;
	public static int NORMAL_SPEED = 180;
	private NXTConnection pcConnection;
	private DataInputStream dis;
	private DataOutputStream dos;
	private boolean quit = false;
	private UltrasonicSensor ultrasonicSensor;
	private LightSensor lightSensor;
	private TouchSensor touchSensor1;
	private TouchSensor touchSensor2;
	private boolean busy = false;
	private boolean resultAlignOnWalls = false;
	private String resultLookAround;
	private SensorThread ST;
	
	public CommandUnit() {		
		ultrasonicSensor = new UltrasonicSensor(SensorPort.S1);
		lightSensor = new LightSensor(SensorPort.S2, false);
		touchSensor1 = new TouchSensor(SensorPort.S3);
		touchSensor2 = new TouchSensor(SensorPort.S4);
		
		currentState = new Waiting();
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
	
	public State getCurrentState() {
		return currentState;
	}
	
	public void setCurrentState(State newState) {
		currentState = newState;
	}
	
	public void sendStringToUnit(String info) {
			try {
				byte[] byteArray = info.getBytes();
				pcConnection.write(byteArray,byteArray.length);
			} catch(Exception e) {
				
			}
	}
	
	public int getSpeed(int speed) {
			if(NORMAL_SPEED == 360)
				return 4;
			else if(NORMAL_SPEED == 270)
				return 3;
			else if(NORMAL_SPEED == 180)
				return 2;
			else
				return 1;
	}
	
	public void setSpeed(int speed) {
		if(speed == 1)
			NORMAL_SPEED = 90;
		else if(speed == 2)
			NORMAL_SPEED = 180;
		else if(speed == 3)
			NORMAL_SPEED = 270;
		else
			NORMAL_SPEED = 360;
	}
	
	public void updateStatus() {
		sendStringToUnit("[US] " + ultrasonicSensor.getDistance());
		sendStringToUnit("[LS] " + lightSensor.getLightValue());
		sendStringToUnit("[TS1] " + touchSensor1.isPressed());
		sendStringToUnit("[TS2] " + touchSensor2.isPressed());
		sendStringToUnit("[LM] " + Motor.B.isMoving() + " " + Motor.B.getSpeed());
		sendStringToUnit("[RM] " + Motor.A.isMoving() + " " + Motor.A.getSpeed());
		sendStringToUnit("[B] " + busy);
	}
	
	public static void main(String[] args) throws IOException {
		CommandUnit CU = new CommandUnit();
		
    	while(!(CU.quit)) {
    		try {
    			LCD.clear();
    			CU.busy = false;
    			CU.updateStatus();
    			System.out.println("Waiting for input...");
    			int input = CU.dis.readInt();
    			CU.busy = true;
    			CU.updateStatus();
    			switch(input) {
    			case (Command.FORWARD_PRESSED):
    				CU.setCurrentState(CU.getCurrentState().ForwardPressed());
    				break;
    			case (Command.FORWARD_RELEASED):
    				CU.setCurrentState(CU.getCurrentState().ForwardReleased());
    				break;
    			case (Command.BACKWARD_PRESSED):
    				CU.setCurrentState(CU.getCurrentState().BackwardPressed());
    				break;
    			case (Command.BACKWARD_RELEASED):
    				CU.setCurrentState(CU.getCurrentState().BackwardReleased());
    				break;
    			case (Command.LEFT_PRESSED):
    				CU.setCurrentState(CU.getCurrentState().LeftPressed());
    				break;
    			case (Command.LEFT_RELEASED):
    				CU.setCurrentState(CU.getCurrentState().LeftReleased());
    				break;
    			case (Command.RIGHT_PRESSED):
    				CU.setCurrentState(CU.getCurrentState().RightPressed());
    				break;
    			case (Command.RIGHT_RELEASED):
    				CU.setCurrentState(CU.getCurrentState().RightReleased());
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
    			    Automatic alignPerp = new Automatic();
    			    CU.setCurrentState(alignPerp);
    			    alignPerp.alignOnWhiteLine(CU.lightSensor, CU, CU.lightSensor.getLightValue() + 4);
    			    CU.sendStringToUnit("[RAL] Done");
    			    CU.setCurrentState(new Waiting());
    			    break;
    			case (Command.ALIGN_WALL):
    			    Automatic alignWall = new Automatic();
    			    CU.setCurrentState(alignWall);
    			    CU.resultAlignOnWalls = alignWall.alignOnWall(CU.ultrasonicSensor);
    			    CU.sendStringToUnit("[RAW] " + CU.resultAlignOnWalls);
    			    CU.setCurrentState(new Waiting());
    			    break;
    			case (Command.CLOSE_CONNECTION):
    				CU.ST.setQuit(true);
    	    		CU.lightSensor.setFloodlight(false);
    	    		CU.quit = true;
    				break;
    			case (Command.LOOK_AROUND):
    			    Automatic lookAround = new Automatic();
    			    CU.setCurrentState(lookAround);
    			    CU.resultLookAround = lookAround.lookAround(CU.ultrasonicSensor);
    			    CU.sendStringToUnit("[RLA] " + CU.resultLookAround);
    			    CU.setCurrentState(new Waiting());
    				break;
    			default:
    				if(input%10==8) {
    					Automatic auto = new Automatic();
    	    			CU.setCurrentState(auto);
    	    			auto.moveForward((int) (input-Command.AUTOMATIC_MOVE_FORWARD)/100);
    	    			CU.setCurrentState(new Waiting());
    				}
    				else if(input%10==9) {
    					Automatic auto = new Automatic();
    	    			CU.setCurrentState(auto);
    	    			auto.turnAngle((input-Command.AUTOMATIC_TURN_ANGLE)/100);
    	    			CU.setCurrentState(new Waiting());
    				}
    				else if(input%100==-91) {
    					Automatic auto = new Automatic();
    	    			CU.setCurrentState(auto);
    	    			auto.turnAngle((input-Command.AUTOMATIC_TURN_ANGLE)/100);
    	    			CU.setCurrentState(new Waiting());
    				}
        			break;
    			}
    		}
    		catch(EOFException e) {
    			System.out.println("End of file!");
    			break;
    		}
    	}
    	
    	CU.dis.close();
    	CU.dos.close();
    	CU.pcConnection.close();
	}
}