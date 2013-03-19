package simulator.pilot;

import java.awt.Point;
import java.awt.geom.Point2D;

import peno.htttp.GameHandler;

import mapping.MapGraph;

public interface PilotInterface {

    public abstract double sizeTile();

    public abstract int getTeamNumber();

    public abstract void setTeamNumber(int teamNumber);

    public abstract MapGraph getMapGraphConstructed();

    public abstract Point2D.Double getPosition();

    public abstract Point getMatrixPosition();

    public abstract void setPosition(double x, double y);

    public abstract double getAngle();

    public abstract void setAngle(double angle);

    public abstract void reset();

    public abstract GameHandler getDefaultHandler();
}