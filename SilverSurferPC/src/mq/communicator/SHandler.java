package mq.communicator;

import peno.htttp.DisconnectReason;
import peno.htttp.PlayerDetails;
import peno.htttp.SpectatorHandler;
import simulator.viewport.SimulatorPanel;

public class SHandler implements SpectatorHandler {

	@Override
	public void gameStarted() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void gameStopped() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void gamePaused() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void gameWon(int teamNumber) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void playerJoining(String playerID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void playerJoined(String playerID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void playerDisconnected(String playerID, DisconnectReason reason) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void playerReady(String playerID, boolean isReady) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void playerFoundObject(String playerID, int playerNumber) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void playerRolled(PlayerDetails playerDetails, int playerNumber) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void playerUpdate(PlayerDetails playerDetails, int playerNumber,
			long x, long y, double angle, boolean foundObject) {
    	y = SimulatorPanel.getMapGraphLoadedSize().y - y; //Omdat y van onder naar boven wordt getelt
		SimulatorPanel.updateRobotPositions(playerNumber-1, (int)x, (int)y);
	}

	@Override
	public void lockedSeesaw(String playerID, int playerNumber, int barcode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unlockedSeesaw(String playerID, int playerNumber, int barcode) {
		// TODO Auto-generated method stub
		
	}

}