package simulator.pilot;

import java.awt.geom.Point2D;
import java.util.Set;

import mapping.Barcode;
import mapping.TreasureObject;
import mapping.MapGraph;
import mapping.Orientation;

public interface PilotInterface {

    // TOON Wat voor position krijgen we door via rabbitMQ?
    public abstract Point2D.Double getPosition();

    public abstract double getAngle();

    public abstract Set<Barcode> getBarcodes();
    
    public abstract Set<TreasureObject> getTreasureObjects();

    public abstract MapGraph getMapGraphConstructed();

    /**
     * True for abstractpilot, false for dummypilot
     */
    public abstract boolean isRobotControllable();

    public abstract double sizeTile();

    public abstract void reset();

    public abstract Orientation getOrientation();
}