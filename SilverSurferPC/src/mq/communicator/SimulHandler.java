package mq.communicator;

import java.util.List;

import peno.htttp.DisconnectReason;
import peno.htttp.PlayerHandler;
import peno.htttp.Tile;
import simulator.pilot.AbstractPilot;

public class SimulHandler implements PlayerHandler {

    private AbstractPilot pilot;

    public SimulHandler(AbstractPilot pilot) {
        this.pilot = pilot;
    }

    @Override
    public void gamePaused() {
        // TODO Auto-generated method stub
        System.out.println("game paused");
        pilot.stopExploring();

    }

    @Override
    public void gameRolled(int playerNumber, int objectNumber) {
        // TODO Auto-generated method stub
        System.out.println("game rolled " + playerNumber);

    }

    @Override
    public void gameStarted() {
        // TODO Auto-generated method stub
        System.out.println("game started");
        pilot.startExploring();

    }

    @Override
    public void gameStopped() {
        // TODO Auto-generated method stub
        System.out.println("game stopped");
        pilot.stopExploring();

    }

    @Override
    public void gameWon(int teamNumber) {
        // TODO Auto-generated method stub

    }

    public AbstractPilot getPilot() {
        return pilot;
    }

    @Override
    public void playerDisconnected(String playerID, DisconnectReason reason) {
        // TODO Auto-generated method stub

    }

    @Override
    public void playerFoundObject(String playerID, int playerNumber) {
        // TODO Auto-generated method stub

    }

    @Override
    public void playerJoined(String playerID) {
        // TODO Auto-generated method stub
        System.out.println("player joined " + playerID);

    }

    @Override
    public void playerJoining(String playerID) {
        // TODO Auto-generated method stub

    }

    @Override
    public void playerReady(String playerID, boolean isReady) {
        // TODO Auto-generated method stub

    }

    @Override
    public void teamConnected(String partnerID) {
        // TODO Auto-generated method stub

    }

    @Override
    public void teamPosition(double x, double y, double angle) {
        // TODO Auto-generated method stub

    }

    @Override
    public void teamTilesReceived(List<Tile> tiles) {
        // TODO Auto-generated method stub

    }

}
