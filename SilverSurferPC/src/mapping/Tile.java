package mapping;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class Tile {

    private final Point position;
    private TileContent content;
    private boolean isMarkedExploreMaze = false;
    private boolean isMarkedShortestPath = false;
    private int manhattanValue;
    private int cost = -1;
    private final HashMap<Orientation, Edge> edges = new HashMap<Orientation, Edge>();

    public Tile(final Point position) {
        this.position = position;
        for (final Orientation orientation : Orientation.values()) {
            final Edge edge = new Edge();
            edges.put(orientation, edge);
            edge.setTileAtRightField(orientation, this);
            edge.setOrientation(orientation);
        }
    }

    public boolean areReachableNeighbours(final Tile tile) {
        if (tile == null)
            return false;
        if (Math.abs(position.getX() - tile.getPosition().getX()) == 1 && (position.getY() - tile.getPosition().getY()) == 0 && getReachableNeighbours().contains(tile))
            return true;
        if ((position.getX() - tile.getPosition().getX()) == 0 && Math.abs(position.getY() - tile.getPosition().getY()) == 1 && getReachableNeighbours().contains(tile))
            return true;
        return false;
    }

    public int getAmtOfWalls() {
        int amt = 0;
        for (Edge edge : getEdges())
            if (edge.getObstruction() == Obstruction.WALL)
                amt++;
        return amt;
    }

    public TileContent getContent() {
        return content;
    }

    public int getCost() {
        return cost;
    }

    public Edge getEdgeAt(final Orientation orientation) {
        return edges.get(orientation);
    }

    public Collection<Edge> getEdges() {
        return edges.values();
    }

    public int getManhattanValue() {
        return manhattanValue;
    }

    public ArrayList<Tile> getNeighbours() {
        final ArrayList<Tile> neighbours = new ArrayList<Tile>();
        for (final Orientation orientation : Orientation.values())
            neighbours.add(getEdgeAt(orientation).getNeighbour(this));
        return neighbours;
    }

    public ArrayList<Tile> getReachableNeighbours() {
        final ArrayList<Tile> neighbours = new ArrayList<Tile>();
        for (final Orientation orientation : Orientation.values())
            if (getEdgeAt(orientation).getObstruction()!= null && getEdgeAt(orientation).getObstruction().isPassable())
                neighbours.add(getEdgeAt(orientation).getNeighbour(this));
        return neighbours;
    }

    public ArrayList<Tile> getReachableNeighboursIgnoringSeesaw() {
        final ArrayList<Tile> neighbours = new ArrayList<Tile>();
        for (final Orientation orientation : Orientation.values())
            if (getEdgeAt(orientation).getObstruction() != Obstruction.WALL)
                neighbours.add(getEdgeAt(orientation).getNeighbour(this));
        return neighbours;
    }
    
    public Tile getNeighbour(Orientation orientation) {
    	return edges.get(orientation).getNeighbour(this);
    }
    
    public Tile getNorthNeighbour() {
    	return edges.get(Orientation.NORTH).getNeighbour(this);
    }
    
    public Tile getEastNeighbour() {
    	return edges.get(Orientation.EAST).getNeighbour(this);
    }
    
    public Tile getSouthNeighbour() {
    	return edges.get(Orientation.SOUTH).getNeighbour(this);
    }
    
    public Tile getWestNeighbour() {
    	return edges.get(Orientation.WEST).getNeighbour(this);
    }

    public Point getPosition() {
        return position;
    }

    public String getToken() {
        String tokenHead = "";
        String orientationPart = "";

        if (getAmtOfWalls() == 4)
            tokenHead = "Closed";
        else if (getAmtOfWalls() == 3) {
            tokenHead = "DeadEnd";
            for (Orientation o : Orientation.values())
                if (getEdgeAt(o).getObstruction() == Obstruction.WHITE_LINE)
                    orientationPart += "." + Orientation.toToken(o.getOppositeOrientation());
        }
        else if (getAmtOfWalls() == 2) {
            if (getEdgeAt(Orientation.NORTH).getObstruction() == Obstruction.WALL && getEdgeAt(Orientation.WEST).getObstruction() == Obstruction.WALL) {
                tokenHead = "Corner";
                orientationPart = ".N";
            }
            else if (getEdgeAt(Orientation.EAST).getObstruction() == Obstruction.WALL && getEdgeAt(Orientation.NORTH).getObstruction() == Obstruction.WALL) {
                tokenHead = "Corner";
                orientationPart = ".E";
            }
            else if (getEdgeAt(Orientation.SOUTH).getObstruction() == Obstruction.WALL && getEdgeAt(Orientation.EAST).getObstruction() == Obstruction.WALL) {
                tokenHead = "Corner";
                orientationPart = ".S";
            }
            else if (getEdgeAt(Orientation.WEST).getObstruction() == Obstruction.WALL && getEdgeAt(Orientation.SOUTH).getObstruction() == Obstruction.WALL) {
                tokenHead = "Corner";
                orientationPart = ".W";
            }
            else {
                tokenHead = "Straight";
                if (getEdgeAt(Orientation.WEST).getObstruction() == Obstruction.WALL && getEdgeAt(Orientation.EAST).getObstruction() == Obstruction.WALL)
                    orientationPart = ".N";
                else
                    orientationPart = ".E";

            }
        }
        else if (getAmtOfWalls() == 1) {
            tokenHead = "T";
            for (Orientation o : Orientation.values())
                if (getEdgeAt(o).getObstruction() == Obstruction.WALL)
                    orientationPart += "." + Orientation.toToken(o);
        }
        else if (getAmtOfWalls() == 0)
            tokenHead = "Cross";

        String barcodePart = "";
        if (getContent() instanceof Barcode)
            barcodePart = "." + ((Barcode) getContent()).getValue();

        String startBasePart = "";
        if (getContent() instanceof StartBase)
            startBasePart = ".S" + ((StartBase) getContent()).getValue() + Orientation.toToken(((StartBase) getContent()).getOrientation());

        String seesawPart = "";
        if (getContent() instanceof Seesaw) {
            tokenHead = "Seesaw";
            orientationPart = "." + Orientation.toToken(((Seesaw) getContent()).getOrientation());
        }

        String treasurePart = "";
        if (getContent() instanceof TreasureObject)
            treasurePart = ".V";

        return tokenHead + orientationPart + barcodePart + startBasePart + seesawPart + treasurePart;
    }

    public boolean isMarkedExploreMaze() {
        return isMarkedExploreMaze;
    }

    public boolean isMarkedShortestPath() {
        return isMarkedShortestPath;
    }

    protected void replaceEdge(final Orientation orientation, final Edge edge) {
        if (edges.containsKey(orientation)) {
            getEdgeAt(orientation).setTileAtRightField(orientation.getOppositeOrientation(), null);
            edges.remove(orientation);
        }
        edges.put(orientation, edge);
        edge.setTileAtRightField(orientation, this);
        edge.setOrientation(orientation);
    }

    public void resetCost() {
        cost = -1;
    }

    public void setContent(final TileContent content) {
        this.content = content;
    }

    /**
     * Alleen als nieuwe kost kleiner is dan huidige kost.
     */
    public void setCost(final int cost) {
        if (this.cost == -1 || this.cost > cost)
            this.cost = cost;
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

    @Override
    public String toString() {
        String content = "";
        if (getContent() instanceof Barcode) {
            content = "" + getContent().getValue();
        } else if (getContent() instanceof TreasureObject) {
            content = "o" + getContent().getValue();
        }
        return getEdgeAt(Orientation.NORTH) + "\n"
                + getEdgeAt(Orientation.WEST) + String.format("%2s", (content))
                + getEdgeAt(Orientation.EAST) + "\n"
                + getEdgeAt(Orientation.SOUTH);
    }
}