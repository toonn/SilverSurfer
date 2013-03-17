package mq.communicator;

import peno.htttp.Handler;
import simulator.pilot.AbstractPilot;
import simulator.pilot.PilotInterface;

public class DummyHandler implements Handler{

private PilotInterface pilot;
	
	public DummyHandler(PilotInterface pilot){
		this.pilot = pilot;
	}
	
	public PilotInterface getPilot() {
		return pilot;
	}

	@Override
	public void gameStarted() {
		// TODO Auto-generated method stub
		System.out.println("game started");
		
	}

	@Override
	public void gameStopped() {
		// TODO Auto-generated method stub
		System.out.println("game stopped");

	}

	@Override
	public void gamePaused() {
		// TODO Auto-generated method stub
		System.out.println("game paused");

	}

	@Override
	public void gameRolled(int playerNumber) {
		// TODO Auto-generated method stub
		System.out.println("game rolled " + playerNumber);

	}

	@Override
	public void playerJoined(String playerID) {
		// TODO Auto-generated method stub
		System.out.println("player joined "+playerID);

	}

	@Override
	public void playerLeft(String playerID) {
		// TODO Auto-generated method stub
		System.out.println("player left "+playerID);

	}

	@Override
	public void playerPosition(String playerID, double x, double y, double angle) {
		// TODO Auto-generated method stub
		System.out.println("player position "+playerID + x + y + angle);

	}

	@Override
	public void playerFoundObject(String playerID) {
		// TODO Auto-generated method stub
		System.out.println("player found object "+playerID);
		
	}
}
