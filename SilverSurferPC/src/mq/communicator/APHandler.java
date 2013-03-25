package mq.communicator;

import java.util.List;

import peno.htttp.DisconnectReason;
import peno.htttp.PlayerHandler;
import peno.htttp.Tile;
import simulator.pilot.AbstractPilot;
import simulator.viewport.SimulatorPanel;

public class APHandler implements PlayerHandler {

    private AbstractPilot pilot;
    private SimulatorPanel panel;

    public APHandler(AbstractPilot pilot, SimulatorPanel panel) {
        this.pilot = pilot;
        this.panel = panel;
    }

    public AbstractPilot getPilot() {
        return pilot;
    }

    @Override
    public void gameStarted() {
        getPilot().startExploring();
        System.out.println("[HTTTP] Game started!");

    }

    @Override
    public void gameStopped() {
        // TODO Auto-generated method stub
        System.out.println("[HTTTP] Game stopped!");

    }

    @Override
    public void gamePaused() {
        // TODO Auto-generated method stub
        System.out.println("[HTTTP] Game paused");

    }

    @Override
    public void playerJoined(String playerID) {
        // TODO Auto-generated method stub
        System.out.println("[HTTTP] " + playerID + " has joined the game!");
    }

    @Override
    public void playerJoining(String playerID) {
    	
    }

    @Override
    public void playerDisconnected(String playerID, DisconnectReason reason) {
        // TODO Auto-generated method stub
    }

    @Override
    public void playerReady(String playerID, boolean isReady) {
    	
    }

    @Override
    public void playerFoundObject(String playerID, int playerNumber) {
        System.out.println("[HTTTP] Player " + playerNumber + " (" + playerID + ") has found his object!");
    }

	@Override
	public void gameWon(int teamNumber) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void gameRolled(int playerNumber, int objectNumber) {
		// TODO Auto-generated method stub
    	getPilot().setPlayerNumber(playerNumber-1);
        System.out.println("[HTTTP] Game rolled, your number is " + (playerNumber-1) + " and your objectnumber is " + objectNumber + ".");
        panel.setOnStartTile(pilot);
        panel.makeReadyToPlay();
        try {
        	pilot.getCenter().setReady(true);
        } catch(Exception e) {
        	
        }
		
	}

	@Override
	public void teamConnected(String partnerID) {
		// TODO Auto-generated method stub
		System.out.println("[HTTTP] Partner (" + partnerID + ") has connected.");
		pilot.setUpdatePosition(true);
		if(panel.getDummyPilot() != null)
			panel.getDummyPilot().activate();
	}

	@Override
	public void teamPosition(double x, double y, double angle) {
		if(panel.getDummyPilot() != null) //Test, is niet correct dus mag veranderd worden (zet dummy position en angle)
			panel.getDummyPilot().setMap(pilot.getMapGraphConstructed());
	}

	@Override
	public void teamTilesReceived(List<Tile> tiles) {
		// TODO Auto-generated method stub
		
	}
}
