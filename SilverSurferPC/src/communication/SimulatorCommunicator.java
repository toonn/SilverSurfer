package communication;

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
		int angle = (int)((float) Math.round(360.0/amtOfAngles*100));
		if (amtOfAngles == 1)
			sendCommandToUnit(lengthInCM*100 + Command.AUTOMATIC_MOVE_FORWARD);
		else for(int i = 0; i<amtOfAngles; i++) {
			sendCommandToUnit(lengthInCM*100 + Command.AUTOMATIC_MOVE_FORWARD);
			sendCommandToUnit(angle*10 + Command.AUTOMATIC_TURN_ANGLE);
		}
	}
	
	@Override
	public void runForward(int amtOfAngles, int lengthInCM) throws IOException {
		sim.travel(40);

	}
	
	@Override
	public void runTurning(int amtOfAngles, int lengthInCM) throws IOException {
		sim.rotate(90);
	}
	
	@Override
	public int getSpeed() {
		return sim.getSpeed();
	}
	
	/**
	 * Gets the amount of angles the arrow should turn in one event
	 * to be at par with the robot.
	 */
	public float getAngularSpeed() {
		switch(getSpeed()){
		case 1: return (float) 1.80;
		case 2: return (float) 2.74;
		case 3: return (float) 2.80;
		case 4: return (float) 3.00;
		}
		return (float) 2.75;

	}
	
	@Override
	public void setSpeed(int speed) {
		sim.setSpeed(speed);
	}
	
	@Override
	public void sendCommandToUnit(int command) throws IOException {

		if(command == 0)
			sim.travel(1);
		else if(command == 2)	
			sim.travel(-1);
		else if((command == 4 && previousCommandForwardOrBackWard == 0) || (command == 6 && previousCommandForwardOrBackWard == 2))
			sim.rotate((float) 360.0-getAngularSpeed());
		else if((command == 6 && previousCommandForwardOrBackWard == 0) || (command == 4 && previousCommandForwardOrBackWard == 2))
			sim.rotate(getAngularSpeed());
		else if(command%10 == 8)
			sim.travel((command-Command.AUTOMATIC_MOVE_FORWARD)/100);
		else if(command%10 == 9)
			sim.rotate((float) (command-Command.AUTOMATIC_TURN_ANGLE)/1000);
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