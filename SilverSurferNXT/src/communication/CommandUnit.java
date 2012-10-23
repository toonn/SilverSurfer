package communication;
import java.io.*;

import statemachine.Automatic;
import statemachine.State;
import statemachine.Waiting;

import commands.Command;

import lejos.nxt.*;
import lejos.nxt.comm.*;

public class CommandUnit {

	public State currentState;
	
	private NXTConnection pcConnection;
	private DataInputStream dis;
	private DataOutputStream dos;
	
	public CommandUnit() {
		currentState = new Waiting();
		System.out.println("Waiting...");
    	pcConnection = Bluetooth.waitForConnection();
   		System.out.println("Connected.");
    
    	dis = pcConnection.openDataInputStream();
    	dos = pcConnection.openDataOutputStream();
	}
	
	public State getCurrentState() {
		return currentState;
	}
	
	public void setCurrentState(State newState) {
		currentState = newState;
	}
	
	public void sendStringToUnit(String info) {
		byte[] byteArray = info.getBytes();
		pcConnection.write(byteArray,byteArray.length);
	}
	
	public void sendIntToUnit(int info) {
		//TODO
	}
	public static void main(String[] args) throws IOException {
		
		CommandUnit CU = new CommandUnit();
   		
    	
    	while(true) {
    		try {
    			LCD.clear();
    			System.out.println("Waiting for input...");
    			int input = CU.dis.readInt();
    			switch(input) {
    			case (Command.FORWARD_PRESSED):
    				CU.setCurrentState(CU.getCurrentState().ForwardPressed());
    				CU.sendStringToUnit("ROBOT SAYS: Moving Forward");
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
    			default:
    				if(input%100==8){
    					Automatic auto = new Automatic();
    	    			CU.setCurrentState(auto);
    	    			auto.moveForward((int) (input-Command.AUTOMATIC_MOVE_FORWARD)/100);
    	    			CU.setCurrentState(new Waiting());
    				}
    				else if(input%100==9){
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