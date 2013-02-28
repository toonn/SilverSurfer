package mapping;

import java.awt.Point;
import java.awt.geom.Point2D;

/**
 * A class of edges involving two tiles, a wall and a number to store in which
 * directions the two tiles are attached to the edge.
 * 
 * @invar Each edge must have 2 proper tiles attached to it. | hasProperTiles()
 * @invar The number to store in which directions the two squares are attached
 *        to the border must be -1 as long as no square is attached. From the
 *        moment the first square is attached, the number change to a value 0, 1
 *        or 2 depending on the direction. From that moment on, it will remain
 *        until the border is terminated.
 * 
 * @author Gerlinde Van Roey
 * 
 */
public class Edge {

    /**
     * Variable referencing the tile to which this edge is attached. The edge is
     * placed at the north or the east side of this tile.
     */
    private Tile tile1;

    /**
     * Variable referencing the tile to which this edge is attached. The edge is
     * placed at the south or the west of this tile
     */
    private Tile tile2;
    private Obstruction obstruction;

    /**
     * Variable registering the number of the directionpair of the square(s)
     * stored in this border.
     */
    private int numberPairDirections;

    /**
     * Variable registering whether this border is terminated.
     */
    private boolean isTerminated;

    /**
     * Initialize this new edge with a value of -1 for the number of the
     * PairDirection.
     */
    public Edge() {
        setNumberPairDirections(-1);
    }

    /**
     * Return the square that shares this border with the given square. These
     * squares act like neighbours. A null reference is returned if the given
     * square has no neighbour.
     * 
     * @param square
     *            The square to get the neighbour of.
     * @return The neighboursquare of the given square if the given square is
     *         attached to this border and if this border is attached to two
     *         squares. If not, return null. | if( howManySquaresAttached() == 2
     *         && ( getSquare1().equals(square) ) | then result == getSquare2()
     *         | else if ( howManySquaresAttached() == 2 && (
     *         getSquare1().equals(square) ) | then result == getSquare1() |
     *         else ( result == null )
     * 
     */
    public Tile getNeighbour(final Tile tile) {
        if (howManyTilesAttached() == 2) {
            if (getTile1().equals(tile)) {
                return getTile2();
            } else if (getTile2().equals(tile)) {
                return getTile1();
            }
        }
        return null;
    }

    /**
     * Return the directionpair-number associated with the direction(s) of the
     * square(s) that is/are stored in this border. The several values can be 0
     * or 1, depending on the rest you obtain by dividing the number attributed
     * to the direction by three.
     */
    public int getNumberPairDirections() {
        return numberPairDirections;
    }

    /**
     * Gets the obstruction that is between the two Tiles of this edge. might be
     * 'null'.
     */
    public Obstruction getObstruction() {
        return obstruction;
    }

    /**
     * Return a square to which this border is attached. A null reference is
     * returned if no square is attached.
     */
    public Tile getTile1() {
        return tile1;
    }

    /**
     * Return a square to which this border is attached. A null reference is
     * returned if no square is attached.
     */
    public Tile getTile2() {
        return tile2;
    }

    public Point2D.Double[] getEndPoints() {
        Point point1 = getTile1().getPosition();
        Point point2 = getTile2().getPosition();
        Point2D.Double[] points = new Point2D.Double[2];

        points[0] = new Point2D.Double(Math.max(point1.x, point2.x), Math.max(
                point1.y, point2.y));

        int xInc = 0;
        int yInc = 0;
        if (point1.x == point2.x) {
            xInc = 1;
        } else if (point1.y == point2.y) {
            yInc = 1;
        }
        points[1] = new Point2D.Double(points[0].x + xInc, points[0].y + yInc);

        return points;
    }

    public Orientation getOrientation() {
        return tile1.getCommonOrientation(tile2);
    }

    /**
     * Return how many squares are attached to the border.
     * 
     * @return The number of squares attached to this border.
     */
    public int howManyTilesAttached() {
        int numberOfSquares = 0;
        if (getTile1() != null) {
            numberOfSquares++;
        }
        if (getTile2() != null) {
            numberOfSquares++;
        }
        return numberOfSquares;
    }

    /**
     * Checks if this edge should be able to be ridden over.
     * 
     * @return true if there is no wall or nothingness on the edge.
     */
    public boolean isPassable() {
        return (getObstruction() == null);
    }

    /**
     * Check whether this border is terminated.
     */
    public boolean isTerminated() {
        return isTerminated;
    }

    /**
     * Set the directionpair-number associated with the direction(s) of the
     * square(s) that is/are stored in this border.
     * 
     * @param numberPairDirections
     *            The number of the directionpair of the square(s) stored in
     *            this border.
     */
    public void setNumberPairDirections(final int numberPairDirections) {
        this.numberPairDirections = numberPairDirections;
    }

    public void setObstruction(final Obstruction obstruction) {
        this.obstruction = obstruction;
    }

    /**
     * Set the square stored at square1 attached to this border to the given
     * square.
     * 
     * @param square1
     *            The new square to be attached to this border.
     * @post The new square is equal to the given square. | new.getSquare1() ==
     *       square1
     */
    private void setTile1(final Tile tile1) {
        this.tile1 = tile1;
    }

    /**
     * Set the square stored at square2 attached to this border to the given
     * square.
     * 
     * @param square2
     *            The new square to be attached to this border.
     * @post The new square is equal to the given square. | new.getSquare2() ==
     *       square2
     */
    private void setTile2(final Tile tile2) {
        this.tile2 = tile2;
    }

    /**
     * Set the square attached to this border to the given square and store it
     * at the right field. If the border is located in the north, east or
     * ceiling of the given square, that square is stored in square1. If the
     * border is located in the south, west or floor of the given square, that
     * square is stored in square2.
     * 
     * @param direction
     *            The direction in which the border is located to the square.
     * @param square
     *            The new square for this border.
     * @post If the given square is effective, and it does not yet reference
     *       this border as one of its borders, return. Else, the new square is
     *       equal to the given square and is stored at the right field. |
     *       if(ExtMath.isNumberDirectionEven(direction)) | then
     *       (new.getSquare1() == square) | if(!
     *       ExtMath.isNumberDirectionEven(direction)) | then (new.getSquare2()
     *       == square)
     */
    public void setTileAtRightField(final Orientation orientation,
            final Tile tile) {
        if (tile != null && !(tile.getEdge(orientation) == this)) {
            return;
        }
        if (ExtMath.isNumberDirectionEven(orientation)) {
            setTile1(tile);
        } else {
            setTile2(tile);
        }
    }

    /**
     * Terminate this border.
     * 
     * @post This border is terminated if no squares are associated with it
     *       anymore. | if(this.howManySquaresAttached() == 0) | then
     *       (new.isTerminated())
     */
    public void terminate() {
        if (howManyTilesAttached() == 0) {
            isTerminated = true;
        }
    }

    @Override
    public String toString() {
        if (getOrientation() == Orientation.NORTH
                || getOrientation() == Orientation.SOUTH) {
            if (getObstruction() != null) {
                return "----";
            } else {
                return "~~~~";
            }
        } else if (getObstruction() != null) {
            return "|";
        } else {
            return "/";
            // String t1 = "T1: null";
            // String t2 = "T2: null";
            // String obstr = "Free Edge";
            // if (getObstruction() != null) {
            // obstr = getObstruction().toString();
            // }
            // if (getTile1() != null) {
            // t1 = "T1:(" + getTile1().getPosition().getX()
            // + getTile1().getPosition().getY() + ")";
            // }
            // if (getTile2() != null) {
            // t2 = "T2:(" + getTile2().getPosition().getX()
            // + getTile2().getPosition().getY() + ")";
            // }
            // return t1 + " " + t2 + " Obstr:" + obstr;
        }
    }
}
