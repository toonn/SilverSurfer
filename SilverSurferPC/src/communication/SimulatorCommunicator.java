package communication;

import audio.SongThread;
import commands.Command;
import simulator.SimulationPilot;

import java.io.IOException;

public class SimulatorCommunicator extends UnitCommunicator {

	private SimulationPilot sim;
	private int previousCommandForwardOrBackWard = 0;

	public SimulatorCommunicator(StatusInfoBuffer status) {
		super(status);
	}
	
	@Override 
	public void openUnitConnection(){
		sim = new SimulationPilot();
	}

	@Override
	public void runPolygon(int amtOfAngles, int lengthInCM) throws IOException {
		int angle = (int)((double) Math.round(360.0/amtOfAngles*100));
		if (amtOfAngles == 1)
			sendCommandToUnit(lengthInCM*100 + Command.AUTOMATIC_MOVE_FORWARD);
		else for(int i = 0; i<amtOfAngles; i++) {
			sendCommandToUnit(lengthInCM*100 + Command.AUTOMATIC_MOVE_FORWARD);
			sendCommandToUnit(angle*10 + Command.AUTOMATIC_TURN_ANGLE);
		}
	}
	
	@Override
	public void moveTurn(int amtOfAngles, int lengthInCM) throws IOException {
		sim.travel(lengthInCM);
		sim.rotate(amtOfAngles);
	}
	
	@Override
	public int getSpeed() {
		return sim.getSpeed();
	}
	
	/**
	 * Gets the amount of angles the arrow should turn in one event
	 * to be at par with the robot.
	 */
	public double getAngularSpeed() {
		switch(getSpeed()){
		case 1: return (double) 1.82;
		case 2: return (double) 2.74;
		case 3: return (double) 2.77;
		case 4: return (double) 1.82;
		}
		return (double) 2.74;

	}
	
	@Override
	public void setSpeed(int speed) {
		sim.setSpeed(speed);
	}
	
	@Override
	public void sendCommandToUnit(int command) throws IOException {

		if(command == Command.FORWARD_PRESSED)
			sim.travel(1);
		else if(command == Command.BACKWARD_PRESSED)	
			sim.travel(-1);
		else if((command == Command.LEFT_PRESSED && previousCommandForwardOrBackWard == 0) || (command == Command.RIGHT_PRESSED && previousCommandForwardOrBackWard == 2))
			sim.rotate((double) 360.0-getAngularSpeed());
		else if((command == Command.RIGHT_PRESSED && previousCommandForwardOrBackWard == 0) || (command == Command.LEFT_PRESSED && previousCommandForwardOrBackWard == 2))
			sim.rotate(getAngularSpeed());
		else if(command == Command.ALIGN_PERPENDICULAR)
			sim.allignOnWhiteLine();
		else if(command == Command.ALIGN_WALL)
			sim.allignOnWalls();
		else if(command == Command.LOOK_AROUND)
            sim.checkForObstructions();		
		else if(command == Command.PLAY_SONG){
    			SongThread song = new SongThread(); 
    			song.start();
		}
		else if(command%10 == Command.AUTOMATIC_MOVE_FORWARD)
			sim.travel((command-Command.AUTOMATIC_MOVE_FORWARD)/100);
		else if(command%10 == Command.AUTOMATIC_TURN_ANGLE)
			sim.rotate((double) (command-Command.AUTOMATIC_TURN_ANGLE)/1000);
		setPreviousCommand(command);
	}
	
	public void setPreviousCommand(int previousCommand) {
		if((previousCommand != 0) && (previousCommand != 2))
			return;
		else
			this.previousCommandForwardOrBackWard = previousCommand;
	}
	
	public SimulationPilot getSim() {
		return sim;
	}
	
	@Override
	public String getConsoleTag() {
		return "[SIMULATOR]";
	}
	
	public void clear() {
		sim.clear();
	}	
	
	
}