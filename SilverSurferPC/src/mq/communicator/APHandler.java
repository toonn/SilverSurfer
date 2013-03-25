package mq.communicator;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.Timer;

import peno.htttp.DisconnectReason;
import peno.htttp.PlayerHandler;
import peno.htttp.SpectatorHandler;
import peno.htttp.Tile;
import simulator.pilot.AbstractPilot;
import simulator.viewport.SimulatorPanel;

public class APHandler implements PlayerHandler {

    private AbstractPilot pilot;
    private SimulatorPanel panel;
    private int updateStatusFPS = 1;
    private ActionListener updateStatus = new ActionListener() {

        @Override
        public void actionPerformed(final ActionEvent arg0) {
        	try {
        		pilot.getCenter().getClient().updatePosition(pilot.getPosition().x, pilot.getPosition().y, pilot.getAngle());
        	} catch(Exception e) {
        		
        	}
        }
    };

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
    public void playerJoined(String playerID) {
        // TODO Auto-generated method stub
        System.out.println(playerID + " has joined the game!");

    }

    @Override
    public void playerJoining(String playerID) {
    	
    }

    @Override
    public void playerDisconnected(String playerID, DisconnectReason reason) {
        // TODO Auto-generated method stub
    	System.out.println("test "+ playerID + " " + reason + " playerDisconnected");
    }

    @Override
    public void playerReady(String playerID, boolean isReady) {
    	
    }

    @Override
    public void playerFoundObject(String playerID, int playerNumber) {
        System.out.println(playerID + " " + playerNumber + " has found object!!!");
    }

	@Override
	public void gameWon(int teamNumber) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void gameRolled(int playerNumber, int objectNumber) {
		// TODO Auto-generated method stub
    	getPilot().setPlayerNumber(playerNumber-1);
        System.out.println("Game rolled, your number is " + playerNumber + " and your objectnumber is " + objectNumber + ".");
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
		System.out.println("Partner with nr " + partnerID + " has connected.");
        new Timer(1000 / updateStatusFPS, updateStatus).start();
	}

	@Override
	public void teamPosition(double x, double y, double angle) {
		// TODO Auto-generated method stub
		System.out.println("I have received info: position " + x + ", " + y + " and angle " + angle);
	}

	@Override
	public void teamTilesReceived(List<Tile> tiles) {
		// TODO Auto-generated method stub
		
	}
}
