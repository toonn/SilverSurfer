package mapping;

import java.awt.Point;
import java.awt.geom.Point2D;

public class Edge {

    private Tile tile1; // North or east side of this tile.
    private Tile tile2; // South or west side of this tile.
    private Obstruction obstruction = null;
    private Orientation orientation; // North/south: horizontal, west/east: vertical

    protected Tile getTile1() {
        return tile1;
    }

    protected Tile getTile2() {
        return tile2;
    }

    public Obstruction getObstruction() {
        return obstruction;
    }

    public void setObstruction(final Obstruction obstruction) {
        this.obstruction = obstruction;
    }

    protected void setOrientation(Orientation orientation) {
        this.orientation = orientation;
    }

    protected void setTileAtRightField(final Orientation orientation,
            final Tile tile) {
        if (tile != null && !(tile.getEdgeAt(orientation) == this)) {
            return;
        }
        if (orientation == Orientation.NORTH || orientation == Orientation.EAST) {
            tile1 = tile;
        } else {
            tile2 = tile;
        }
    }

    public Tile getNeighbour(final Tile tile) {
        if (tile1 != null && tile1.equals(tile)) {
            return tile2;
        } else {
            return tile1;
        }
    }

    public boolean isPassable() {
        return (getObstruction() == null || getObstruction().isPassable());
    }

    public Point2D.Double[] getEndPoints() {
        Orientation orientation = null;
        Point point = null;
        if (tile1 == null) {
            for (Orientation orien : Orientation.values()) {
                if (equals(tile2.getEdgeAt(orien))) {
                    orientation = orien;
                }
            }
            point = new Point(tile2.getPosition());
            if (orientation == Orientation.EAST) {
                point.setLocation(point.x + 1, point.y);
            } else if (orientation == Orientation.SOUTH) {
                point.setLocation(point.x, point.y + 1);
            }
        } else {
            for (Orientation orien : Orientation.values()) {
                if (equals(tile1.getEdgeAt(orien))) {
                    orientation = orien;
                }
            }
            point = new Point(tile1.getPosition());
            if (orientation == Orientation.EAST) {
                point.setLocation(point.x + 1, point.y);
            } else if (orientation == Orientation.SOUTH) {
                point.setLocation(point.x, point.y + 1);
            }
        }

        Point2D.Double[] points = new Point2D.Double[2];

        Point2D.Double point1 = new Point2D.Double(point.x, point.y);
        points[0] = point1;

        Point2D.Double point2 = null;
        if (orientation == Orientation.NORTH
                || orientation == Orientation.SOUTH) {
            point2 = new Point2D.Double(point.x + 1, point.y);
        } else if (orientation == Orientation.EAST
                || orientation == Orientation.WEST) {
            point2 = new Point2D.Double(point.x, point.y + 1);
        }
        points[1] = point2;

        return points;
    }

    @Override
    public String toString() {
        if (orientation == Orientation.NORTH
                || orientation == Orientation.SOUTH) {
            if (getObstruction() != null) {
                return "\u2588\u2588\u2588\u2588";
            } else {
                return "\u2588--\u2588";
            }
        } else if (getObstruction() != null) {
            return "\u2588";
        } else {
            return "\u00A6";
        }
    }
}