package simulator.pilot;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;

import mapping.MapGraph;
import mq.communicator.DummyHandler;
import peno.htttp.SpectatorHandler;
import simulator.viewport.SimulatorPanel;

public class DummyPilot implements PilotInterface {

    private int teamNumber;
    private MapGraph mapGraphConstructed;
    private Point2D.Double position;
    private double angle;
    private boolean active = false;
    private DummyHandler handler;
    private boolean gameModus;

    public DummyPilot(int teamNumber) {
        if (teamNumber < 0 || teamNumber > 3) {
            this.teamNumber = -1;
        } else {
            this.teamNumber = teamNumber;
        }

        handler = new DummyHandler(this);
        reset();
    }

    public void activate() {
        active = true;
        // TODO
    }

    @Override
    public double getAngle() {
        return angle;
    }

    @Override
    public SpectatorHandler getDefaultHandler() {
        return handler;
    }

    @Override
    public MapGraph getMapGraphConstructed() {
        return mapGraphConstructed;
    }

    @Override
    public Point getMatrixPosition() {
        return new Point((int) (getPosition().getX() / sizeTile()),
                (int) (getPosition().getY() / sizeTile()));
    }

    /**
     * Returns 0,1,2 or 3 indicating which treasure the pilot is looking for
     * Returns 4 or 5 when the treasure is found and the pilot knows what team
     * it is in Returns -1 if no valid team number is available
     */
    @Override
    public int getPlayerNumber() {
        return teamNumber;
    }

    @Override
    public Double getPosition() {
        return position;
    }

    @Override
    public int getTeamNumber() {
        return teamNumber;
    }

    public boolean isActive() {
        return active;
    }

    @Override
    public boolean isInGameModus() {
        return gameModus;
    }

    @Override
    public void makeReadyToPlay() {
        mapGraphConstructed = new MapGraph();
        mapGraphConstructed.addTile(getMatrixPosition());
    }

    @Override
    public void reset() {
        position = new Point2D.Double(sizeTile() / 2, sizeTile() / 2);
        angle = 270;
        mapGraphConstructed = new MapGraph();
        mapGraphConstructed.addTile(getMatrixPosition());
    }

    @Override
    public void setAngle(final double angle) {
        if (angle > 360) {
            this.angle = angle - 360;
        } else if (angle < 0) {
            this.angle = angle + 360;
        } else {
            this.angle = angle;
        }
    }

    @Override
    public void setGameModus(boolean onOff) {
        gameModus = onOff;

    }

    public void setMap(MapGraph newMap) {
        mapGraphConstructed = newMap;
    }

    @Override
    public void setPlayerNumber(int teamNumber) {
        this.teamNumber = teamNumber;
    }

    @Override
    public void setPosition(final double x, final double y) {
        position.setLocation(x, y);
    }

    @Override
    public void setTeamNumber(int teamNumber) {
        this.teamNumber = teamNumber;

    }

    @Override
    public void setupForGame(SimulatorPanel panel) {
        // TODO Auto-generated method stub

    }

    @Override
    public double sizeTile() {
        return 40;
    }
}