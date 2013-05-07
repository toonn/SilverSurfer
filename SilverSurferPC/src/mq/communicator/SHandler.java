package mq.communicator;

import java.awt.Point;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import mapping.MapGraph;
import mapping.Tile;
import peno.htttp.DisconnectReason;
import peno.htttp.PlayerDetails;
import peno.htttp.SpectatorHandler;
import simulator.viewport.SimulatorPanel;

public class SHandler implements SpectatorHandler {
    private MapGraph mapGraphLoaded;
    private Map<Integer, Integer> lockedSeesaws = new HashMap<Integer, Integer>();

    public SHandler(MapGraph mapGraphLoaded) {
        this.mapGraphLoaded = mapGraphLoaded;
    }

    public void setMap(MapGraph map) {
        mapGraphLoaded = map;
    }

    @Override
    public void gameStarted() {
        // TODO Auto-generated method stub

    }

    @Override
    public void gameStopped() {
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
        if (angle == -90)
            angle = 270;
        Point startMatrixPosition = getStartMatrixPosition(playerNumber - 1);
        SimulatorPanel.updateRobotPositions(playerNumber - 1,
                (int) (x + startMatrixPosition.getX()),
                (int) (-y + startMatrixPosition.getY()), angle);
    }

    private Point getStartMatrixPosition(int playerNumber) {
        Point startMatrixPosition = new Point();
        for (Tile tile : mapGraphLoaded.getStartTiles())
            if (tile.getContent().getValue() == playerNumber)
                startMatrixPosition.setLocation(tile.getPosition());
        return startMatrixPosition;
    }

    @Override
    public void lockedSeesaw(String playerID, int playerNumber, int barcode) {
        int seesawValue = 0;
        if (barcode == 11 || barcode == 13)
            seesawValue = 0;
        else if (barcode == 15 || barcode == 17)
            seesawValue = 1;
        else if (barcode == 19 || barcode == 21)
            seesawValue = 2;

        // -1? Vertaling naar onze playerNumbers
        lockedSeesaws.put(seesawValue, playerNumber - 1);
    }

    @Override
    public void unlockedSeesaw(String playerID, int playerNumber, int barcode) {
        int seesawValue = 0;
        if (barcode == 11 || barcode == 13)
            seesawValue = 0;
        else if (barcode == 15 || barcode == 17)
            seesawValue = 1;
        else if (barcode == 19 || barcode == 21)
            seesawValue = 2;

        // -1? Vertaling naar onze playerNumbers
        lockedSeesaws.remove(seesawValue);
    }

    public int whoHasLock(int seesawValue) {
        return lockedSeesaws.get(seesawValue);
    }

    public boolean isLocked(int seesawValue) {
        return lockedSeesaws.containsKey(seesawValue);
    }
}