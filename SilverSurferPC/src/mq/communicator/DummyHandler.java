package mq.communicator;

import peno.htttp.DisconnectReason;
import peno.htttp.SpectatorHandler;
import simulator.pilot.PilotInterface;

public class DummyHandler implements SpectatorHandler {

    private PilotInterface pilot;

    public DummyHandler(PilotInterface pilot) {
        this.pilot = pilot;
    }

    @Override
    public void gamePaused() {
        // TODO Auto-generated method stub
        System.out.println("game paused");

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
    public void gameWon(int teamNumber) {
        // TODO Auto-generated method stub

    }

    public PilotInterface getPilot() {
        return pilot;
    }

    @Override
    public void lockedSeesaw(String playerID, int playerNumber, int barcode) {
        // TODO Auto-generated method stub

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
    public void playerUpdate(String playerID, int playerNumber, double x,
            double y, double angle, boolean foundObject) {
        // TODO Auto-generated method stub

    }

    @Override
    public void unlockedSeesaw(String playerID, int playerNumber, int barcode) {
        // TODO Auto-generated method stub

    }
}
