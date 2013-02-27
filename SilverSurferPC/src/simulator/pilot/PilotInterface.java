package simulator.pilot;

import java.awt.geom.Point2D;
import java.util.Set;

import mapping.Barcode;
import mapping.MapGraph;
import mapping.Orientation;

public interface PilotInterface {

    public abstract double getAngle();

    public abstract Set<Barcode> getBarcodes();

    public abstract Orientation getOrientation();

    // TOON Wat voor position krijgen we door via rabbitMQ?
    public abstract Point2D.Double getAbsolutePosition();

    public abstract MapGraph getMapGraphConstructed();

    /**
     * Moet deze Pilot de 'robot' aansturen? (Is het een echte of
     * zelfgesimuleerde robot <-> is het een robot die je doorkrijgt via
     * rabbitMQ)
     */
    public abstract boolean isRobotControllable();

    /**
     * Resets the currentPositionAbsolute's and the startPositionAbsolute's to
     * 220. Resets alpha to 270, speed to 10;
     */
    // TOON Vergeet de mapgraph?
    public abstract void reset();

    public abstract double sizeTile();

}