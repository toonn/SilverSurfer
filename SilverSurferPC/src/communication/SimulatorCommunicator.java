package communication;

import java.io.IOException;
import commands.Command;
import simulator.SimulationPilot;


public class SimulatorCommunicator extends UnitCommunicator{

	private SimulationPilot sim;
	private int lastForwardBackwardCommand = 1;
	private int lastRightLeftCommand = 5;
	//private int lastReceivedCommand;
	
	@Override 
	public void openUnitConnection(){
		sim = new SimulationPilot();
	}

	@Override
	public void runPolygon(int amtOfAngles, int lengthInCM) throws IOException {
		int angle = (int)((float) Math.round(360.0/amtOfAngles*100));
		for(int i = 0; i<amtOfAngles; i++) {
			sendCommandToUnit(lengthInCM*100 + Command.AUTOMATIC_MOVE_FORWARD);
			sendCommandToUnit(angle*10 + Command.AUTOMATIC_TURN_ANGLE);
		}
	}
	
	@Override
	public int getSpeed() {
		return sim.getSpeed();
	}
	
	@Override
	public void setSpeed(int speed) {
		sim.setSpeed(speed);
	}
	
	@Override
	public void sendCommandToUnit(int command) throws IOException {
		if(command == 0||command == 1||command == 2||command == 3){
			lastForwardBackwardCommand=command;
		}
		else if(command == 4||command == 5||command == 6||command == 7){
			lastRightLeftCommand=command;
		}
		
        switch(lastForwardBackwardCommand) { 			
        	case 0: 			
        		if(lastRightLeftCommand==4){ 			
        			sim.travel(1); 			
        			sim.rotate(359); 			
        		} 			
        		else if(lastRightLeftCommand==5){ 			
        			sim.travel(1); 			
        		} 			
        		else if(lastRightLeftCommand==6){ 			
        			sim.travel(1); 			
        			sim.rotate(1); 			
        		} 			
        		else if(lastRightLeftCommand==7){ 			
        			sim.travel(1); 			
        		} 			
        		break; 			
        	case 1: 			
        		if(lastRightLeftCommand==4){ 			
        			sim.rotate(359);} 			
        		else if(lastRightLeftCommand==5){ 			
        			// do nothing 			
        		} 			
        		else if(lastRightLeftCommand==6){ 			
        			sim.rotate(1); 			
        		} 			
        		else if(lastRightLeftCommand==7){ 			
        			//do nothing 			
        		} 			
        		break; 			
        	case 2: 			
        		if(lastRightLeftCommand==4){ 			
        			sim.rotate(180); 			
        			sim.travel(1); 			
        			sim.rotate(180); 			
        			sim.rotate(1);
        		} 			
        		else if(lastRightLeftCommand==5){ 			
        			sim.rotate(180); 			
        			sim.travel(1); 			
        			sim.rotate(180); 			
        		} 			
        		else if(lastRightLeftCommand==6){ 			
        			sim.rotate(180); 			
        			sim.travel(1); 			
        			sim.rotate(180); 			
        			sim.rotate(359); 			
        		} 			
        		else if(lastRightLeftCommand==7){ 			
        			sim.rotate(180); 			
        			sim.travel(1); 			
        			sim.rotate(180); 			
        		} 			
        		break; 			
        	case 3: 			
        		if(lastRightLeftCommand==4){ 			
        			sim.rotate(359);} 			
        		else if(lastRightLeftCommand==5){ 			
        			// do nothing 			
        		} 			
        		else if(lastRightLeftCommand==6){ 			
        			sim.rotate(1); 			
        		} 			
        		else if(lastRightLeftCommand==7){ 			
        			//do nothing 			
        		} 			
        		break;
        }
		/*switch(command){
		
		case 0:
			sim.travel(1);
			break;
		case 1: 
			setLastReceivedCommand(command);
			break;
		case 2: 
			sim.rotate(180);
			sim.travel(1);
			sim.rotate(180);
			break;
		case 3:
			setLastReceivedCommand(command);
			break;
		case 4:
			sim.rotate(355);
			break;
		case 5:
			setLastReceivedCommand(command);
			break;
		case 6:
			sim.rotate(5);
			break;
		case 7:
			setLastReceivedCommand(command);
			break;
		default:
			if(command%10==8){
				sim.travel((command-Command.AUTOMATIC_MOVE_FORWARD)/100);
			}
			if(command%10==9){
				sim.rotate((float) (command-Command.AUTOMATIC_TURN_ANGLE)/1000);
			}
			return;
		}*/
	}

	//public boolean isLastReceivedCommandUneven() {
	//	if(lastReceivedCommand%2==1) {
	//		return true;
	//	}
	//	return false;
	//}
	
	public SimulationPilot getSim() {
		return sim;
	}

	//public void setLastReceivedCommand(int lastReceivedCommand) {
	//	this.lastReceivedCommand = lastReceivedCommand;
	//}
	
	@Override
	public String getConsoleTag() {
		return "[SIMULATOR]";
	}
}