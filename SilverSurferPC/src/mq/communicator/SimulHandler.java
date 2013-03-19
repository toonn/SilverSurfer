package mq.communicator;

import peno.htttp.DisconnectReason;
import peno.htttp.PlayerHandler;
import simulator.pilot.AbstractPilot;

public class SimulHandler implements PlayerHandler {

    private AbstractPilot pilot;

    public SimulHandler(AbstractPilot pilot) {
        this.pilot = pilot;
    }

    public AbstractPilot getPilot() {
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
        System.out.println("player joined " + playerID);

    }

    @Override
    public void playerJoining(String playerID) {
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

}
