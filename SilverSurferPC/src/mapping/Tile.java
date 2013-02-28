package mapping;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class Tile {

    private final Point location;
    private Barcode content;
    private boolean isMarkedExploreMaze = false;
    private boolean isMarkedShortestPath = false;
    private int manhattanValue;
    private int cost = -1;

    /**
     * Variable registering whether this square is terminated.
     */
    private boolean isTerminated;

    /**
     * Map collecting references to edges associated with the 4 directions of
     * this tile.
     * 
     * @invar Each square contains 4 edges. | for each direction in
     *        edges.keySet(): | getEdge(direction) != null
     */
    private final HashMap<Orientation, Edge> edges = new HashMap<Orientation, Edge>();

    public Tile(final Point point) {
        location = point;
        populateEdges();
    }

    /**
     * Check whether the given square and this square are neighbours. Two
     * squares are neighbours if they share a same border.
     * 
     * @param square
     *            the square to check.
     * @return True if and only if this square has a neighboursquare in a
     *         certain direction and if that neighboursquare is equal to the
     *         given square. | for each direction in borders.keySet() : |
     *         if(hasNeighbour(direction) | then (result ==
     *         getBorder(direction).getNeighbour(this).equals(square) )
     */
    public boolean areNeighbours(final Tile tile) {
        if (tile == null) {
            return false;
        }
        if (Math.abs(location.getX() - tile.getPosition().getX()) == 1
                && (location.getY() - tile.getPosition().getY()) == 0) {
            return true;
        }
        if (Math.abs(location.getX() - tile.getPosition().getX()) == 1
                && (location.getY() - tile.getPosition().getY()) == 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Check whether this square can have the given border as one of its borders
     * in the given direction.
     * 
     * @param border
     *            The border to check.
     * @param direction
     *            The direction to check.
     * @return True if this square does already contains the given border at the
     *         given direction. | result ==
     *         this.getBorder(direction).equals(border) False if this square can
     *         not have the given border as a border at the given direction. |
     *         if (! canHaveAsBorder(border)) | then result == false Otherwise,
     *         false if this square can not have the given border at the given
     *         direction. | if (! canHaveAsReplacingBorder(direction, border) )
     *         | then result == false Otherwise, false if the given dungeon
     *         contains a wall with a door and the given direction is equal to
     *         the floor. | result == ! (border.hasWall()&&
     *         border.getWall().hasDoor() && direction == Direction.FLOOR)
     *         Otherwise, true if and only if the given border is not yet
     *         attached to any square. | result == ( border.getSquare1() == null
     *         && border.getSquare2() == null ) Otherwise, true if and only if
     *         there is already one square attached to the given border, that
     *         square must be located at the opposite side of the border. |
     *         result == | if((ExtMath.isNumberDirectionEven(direction) &&
     *         (border.getSquare1() == null) ) | || (!
     *         ExtMath.isNumberDirectionEven(direction) && (border.getSquare2()
     *         == null)) ) | then result == (
     *         Direction.getNumberDirection(direction)%3 ==
     *         border.getNumberPairDirections() ) Otherwise, false because the
     *         last possibility is that the border is already attached to two
     *         squares. | result == false
     * 
     */
    // dat de border kan replaced worden wanneer deze er al staat is van belang
    // bij de
    // canNavigateTo-methode, bij !canShareBorders check je of de
    // "overkoepelende" border
    // bij beide kan gezet worden, wat dus in het geval dat de border er al
    // staat, mogelijk is.
    public boolean canHaveAsBorderAtDirection(final Edge edge,
            final Orientation orientation) {

        if (getEdge(orientation).equals(edge)) {
            return true;
        }

        if (!canHaveAsEdge(edge)) {
            return false;
        }

        if (edge.howManyTilesAttached() == 0) {
            return true;
        } else if (edge.howManyTilesAttached() == 1) {
            if ((ExtMath.isNumberDirectionEven(orientation)
                    && (edge.getTile1() == null) && areNeighbours(edge
                        .getTile2()))
                    || (!ExtMath.isNumberDirectionEven(orientation)
                            && (edge.getTile2() == null) && areNeighbours(edge
                                .getTile1()))) {
                return (edge.getNumberPairDirections() == orientation
                        .getNumberOrientation() % 3);
            }
        }
        return false;
    }

    /**
     * Check whether this square can have the given border as its border.
     * 
     * @param border
     *            the border to check.
     * @return If this square is terminated, true if and only if the given
     *         border is not effective. | if (isTerminated()) | then result ==
     *         (border == null) Otherwise, true if and only if the given border
     *         is effective and not yet terminated. | else result == | ( (border
     *         != null) | && (! border.isTerminated()) )
     */
    public boolean canHaveAsEdge(final Edge edge) {
        if (isTerminated()) {
            return edge == null;
        } else {
            return (edge != null) && (!edge.isTerminated());
        }
    }

    /**
     * returns all 4 neighbours of this tile, if has 4, otherwise null.
     */
    public ArrayList<Tile> getAllNeighbours() {
        final ArrayList<Tile> neighbours = new ArrayList<Tile>(4);
        for (final Orientation orientation : Orientation.values()) {
            neighbours.add(getEdge(orientation).getNeighbour(this));
        }

        return neighbours;
    }

    // Terminate

    public int getAmountOfWalls() {
        int i = 0;
        if (!getEdge(Orientation.EAST).isPassable()) {
            i++;
        }
        if (!getEdge(Orientation.WEST).isPassable()) {
            i++;
        }
        if (!getEdge(Orientation.NORTH).isPassable()) {
            i++;
        }
        if (!getEdge(Orientation.SOUTH).isPassable()) {
            i++;
        }
        return i;
    }

    public Orientation getCommonOrientation(Tile tile) {
        if (!areNeighbours(tile)) {
            return null;
        }
        for (Orientation orientation : Orientation.values()) {
            if (getEdge(orientation) == tile.getEdge(orientation)) {
                return orientation;
            }
        }
        return null;
    }

    /**
     * Returns the content of this tile. Might be a barcode or null.
     * 
     * @return
     */
    public Barcode getContent() {
        return content;
    }

    // Edges

    public int getCost() {
        return cost;
    }

    /**
     * Return the border in the given direction of this square.
     * 
     * @param direction
     *            The direction of the border to be returned.
     */
    public Edge getEdge(final Orientation orientation) {
        return edges.get(orientation);
    }

    public Collection<Edge> getEdges() {
        return edges.values();
    }

    /**
     * 
     * @return de heuristiek
     */
    public int getManhattanValue() {
        return manhattanValue;
    }

    // /**
    // * return -1000 if this tile is not yet set on a mapgraph
    // */
    // public int getxCoordinate() {
    // return xCoordinate;
    // }
    //
    // /**
    // * return -1000 if this tile is not yet set on a mapgraph
    // */
    // public int getyCoordinate() {
    // return yCoordinate;
    // }

    /** return null if this tile is not yet set on a mapgraph */
    public Point getPosition() {
        return location;
    }

    /**
     * geeft een arraylist weer met neighbourtiles enkel die waar geen muur
     * tussen staat! de orientation horende bij de index is als volgt: 0: north
     * 1: east 2: south 3: west als er zich geen tile bevindt in die
     * orientation, wordt er een null in de arraylist opgeslagen.
     */
    public ArrayList<Tile> getReachableNeighbours() {
        final ArrayList<Tile> neighbours = new ArrayList<Tile>(4);
        for (final Orientation orientation : Orientation.values()) {
            if (getEdge(orientation).getObstruction() == null) {
                neighbours.add(getEdge(orientation).getNeighbour(this));
            } else {
                neighbours.add(null);
            }
        }

        return neighbours;
    }

    /**
     * Geeft true als hij al gebruikt is in het ExploreMaze-algoritme.
     */
    public boolean isMarkedExploreMaze() {
        return isMarkedExploreMaze;
    }

    /**
     * Geeft true als hij al gebruikt is in het shortestPath-algoritme.
     */
    public boolean isMarkedShortestPath() {
        return isMarkedShortestPath;
    }

    public boolean isStraightTile() {
        if (!getEdge(Orientation.EAST).isPassable()
                && !getEdge(Orientation.WEST).isPassable()
                && getEdge(Orientation.NORTH).isPassable()
                && getEdge(Orientation.SOUTH).isPassable()) {
            return true;
        }
        if (!getEdge(Orientation.NORTH).isPassable()
                && !getEdge(Orientation.SOUTH).isPassable()
                && getEdge(Orientation.WEST).isPassable()
                && getEdge(Orientation.EAST).isPassable()) {
            return true;
        }
        return false;
    }

    /**
     * Check whether this square is terminated.
     */
    public boolean isTerminated() {
        return isTerminated;
    }

    // public boolean isReachableNeighbour(Tile tile){
    // return getReachableNeighbours().contains(tile);
    // }

    /**
     * Set the borders in each direction for this square. Each border is
     * attached to this square in the given direction. This is a method only
     * used in the constructor.
     * 
     * @post This square is attached to the new border for the given direction.
     *       | for each direction in Direction.values() |
     *       this.getBorderAt(direction) == new Border(this, null)
     */
    protected void populateEdges() {
        for (final Orientation orientation : Orientation.values()) {
            final Edge edge = new Edge();
            setEdge(orientation, edge);
            edge.setTileAtRightField(orientation, this);
            edge.setNumberPairDirections(orientation.getNumberOrientation() % 3);
        }
    }

    // velden aangemaakt voor gebruik van algoritmes

    /**
     * Replace the border in the given direction with the given border.
     * 
     * @param direction
     *            The direction of the square in which we want to replace the
     *            border.
     * @param border
     *            the border to which this square must be attached.
     * @post This square is attached to the given border. |
     *       new.getBorder(direction) == border
     * @post The given border has this square as one of its squares. | ( ((new
     *       border).getSquare1() == this) || | ((new border).getSquare2() ==
     *       this) )
     * @post If this square was attached to some other border in the given
     *       direction, that border no longer references this square as one of
     *       the squares attached to it. | if ( (getBorder(direction) != null) |
     *       && (getBorder(direction) != border) ) | then (! ( (new
     *       getBorder()).getSquare1 == this || (new getBorder()).getSquare2 ==
     *       this )
     * @post if this square is terminated or does not can have this border at
     *       the given direction or if be placed, it exceeds the maximum numbers
     *       of doors or has less then one wall, the state of this square will
     *       remain the same.
     */
    // Border ligt steeds ten noord of west van tile1
    public void replaceEdge(final Orientation orientation, final Edge edge) {
        if (getEdge(orientation).equals(edge)) {
            return;
        }
        if (isTerminated()) {
            return;
        }
        if (!canHaveAsBorderAtDirection(edge, orientation)) {
            return;
        }

        if (getEdge(orientation) != null) {
            getEdge(orientation).setTileAtRightField(orientation, null);
            getEdge(orientation).terminate();
        }
        setEdge(orientation, edge);
        edge.setTileAtRightField(orientation, this);
        edge.setNumberPairDirections(orientation.getNumberOrientation() % 3);
    }

    public void setContent(final Barcode cont) {
        content = cont;
    }

    /**
     * Hierin wordt de kost gezet, deze is afhankelijk van het shortestPath
     * algoritme. De kost wordt enkel gezet, als deze kleiner is dan de kost die
     * er nu al staat of als ze nog op -1 staat (initiele waarde).
     */
    public void setCost(final int cost) {
        if (this.cost == -1 || this.cost > cost) {
            this.cost = cost;
        }
    }

    /**
     * Deze zet de kost terug op zijn initiele waarde. De tile bevat normaal
     * steeds deze waarde behalve als ze betrokken is bij een shortestPath
     * algoritme. Dit houdt ook in dat ze maar in 1 shortestPath-algoritme
     * tegelijkertijd kan gebruikt worden. Op het einde van het shortestPath
     * algoritme wordt deze methode steeds opgeroepen op elke tile die erin
     * betrokken was.
     */
    public void setCostBackToInitiatedValue() {
        cost = -1;
    }

    /**
     * Set the border to which this square is attached to the given border in
     * the given direction.
     * 
     * @param direction
     *            The direction to set the border.
     * @param border
     *            The border to which this square must be attached.
     * @post This square is attached to the given border in the given direction.
     *       | new.getBorder(direction) == border
     */
    protected void setEdge(final Orientation orientation, final Edge edge) {
        if (!canHaveAsEdge(edge)) {
            return;
        }
        edges.put(orientation, edge);
    }

    public void setManhattanValue(final int manhattanValue) {
        this.manhattanValue = manhattanValue;
    }

    public void setMarkingExploreMaze(final boolean marking) {
        isMarkedExploreMaze = marking;

    }

    public void setMarkingShortestPath(final boolean marking) {
        isMarkedShortestPath = marking;

    }

    /**
     * Terminate this square.
     * 
     * @post this square is terminated. | new.isTerminated()
     * @post This square no longer references any borders as the borders to
     *       which it is attached. |for each direction in borders.keySet(): |
     *       new.getBorder(direction) == null
     * @post if this square was not already terminated, the borders to which
     *       this square was attached, no longer have this square attached to
     *       them. If a border doesn't have any square attached to it anymore,
     *       that border is terminated. |for each direction in borders.keySet():
     *       | if (! isTerminated()) | then (new
     *       getBorder(direction)).getSquare1() != this | && (new
     *       getBorder(direction)).getSquare2() != this |for each direction in
     *       borders.keySet(): | if( (new getBorder(direction)).getSquare1() ==
     *       null && | (new getBorder(direction)).getSquare2() == null ) | then(
     *       (new getBorder(direction)).isTerminated() )
     */
    public void terminate() {
        if (!isTerminated()) {
            isTerminated = true;
            for (final Orientation orientation : Orientation.values()) {
                final Edge formerEdge = getEdge(orientation);
                setEdge(orientation, null);
                formerEdge.setTileAtRightField(orientation, null);
                formerEdge.terminate();
            }
        }
    }

    @Override
    public String toString() {
        return getEdge(Orientation.NORTH) + "\n" + getEdge(Orientation.WEST)
                + String.format("%2s", getContent().getValue())
                + getEdge(Orientation.EAST) + "\n" + getEdge(Orientation.SOUTH);
    }
}