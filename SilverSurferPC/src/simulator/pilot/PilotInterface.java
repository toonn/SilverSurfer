package simulator.pilot;

import java.awt.geom.Point2D;

import mapping.MapGraph;
import mapping.Orientation;

public interface PilotInterface {

    // TOON Wat voor position krijgen we door via rabbitMQ?
    public abstract Point2D.Double getPosition();

    public abstract double getAngle();

    public abstract MapGraph getMapGraphConstructed();

    /**
     * True for abstractpilot, false for dummypilot
     */
    public abstract boolean isRobotControllable();

    public abstract double sizeTile();

    public abstract void reset();

    public abstract Orientation getOrientation();
    
    /**
     * Returns 0,1,2 or 3 indicating which treasure the pilot is looking for
     * Returns 4 or 5 when the treasure is found and the pilot knows what team it is in
     * Returns -1 if no valid team number is available
     */
    public abstract int getTeamNumber();
    
}