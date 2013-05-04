package simulator.pilot;

import java.awt.Point;
import java.awt.geom.Point2D;

import mapping.MapGraph;

public interface PilotInterface {

    public abstract double getAngle();

    public abstract MapGraph getMapGraphConstructed();

    public abstract Point getMatrixPosition();

    public abstract Point2D.Double getPosition();

    public abstract int getTeamNumber();

    public abstract void makeReadyToPlay();

    public abstract void reset();

    public abstract void setAngle(double angle);

    public abstract void setPlayerNumber(int number);

    public abstract void setPosition(double x, double y);

    public abstract void setTeamNumber(int teamNumber);

    public abstract double sizeTile();
}