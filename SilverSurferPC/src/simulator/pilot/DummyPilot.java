package simulator.pilot;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;

import mapping.MapGraph;
import mq.communicator.APHandler;
import simulator.viewport.SimulatorPanel;

public class DummyPilot implements PilotInterface {

    private int teamNumber = -1;
    private MapGraph mapGraphConstructed;
    private Point2D.Double position;
    private double angle;
    private boolean active = false;
    private APHandler handler;
    private boolean gameModus;

    public DummyPilot() {
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
    public APHandler getDefaultHandler() {
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
    	active = false;
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