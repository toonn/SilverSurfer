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
    
    public Point getPosition() {
        return position;
    }

    public TileContent getContent() {
        return content;
    }

    public void setContent(final TileContent content) {
        this.content = content;
    }

    public boolean isMarkedExploreMaze() {
        return isMarkedExploreMaze;
    }

    public void setMarkingExploreMaze(final boolean marking) {
        isMarkedExploreMaze = marking;
    }

    public boolean isMarkedShortestPath() {
        return isMarkedShortestPath;
    }

    public void setMarkingShortestPath(final boolean marking) {
        isMarkedShortestPath = marking;
    }

    public int getManhattanValue() {
        return manhattanValue;
    }

    public void setManhattanValue(final int manhattanValue) {
        this.manhattanValue = manhattanValue;
    }

    public int getCost() {
        return cost;
    }
    
    /**
     * Alleen als nieuwe kost kleiner is dan huidige kost.
     */
    public void setCost(final int cost) {
        if (this.cost == -1 || this.cost > cost)
            this.cost = cost;
    }
    
    public void resetCost() {
        cost = -1;
    }

    public Collection<Edge> getEdges() {
        return edges.values();
    }
    
    public Edge getEdgeAt(final Orientation orientation) {
        return edges.get(orientation);
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
        	if(getEdgeAt(orientation).isPassable())
            	neighbours.add(getEdgeAt(orientation).getNeighbour(this));
        return neighbours;
    }
    
    public boolean areNeighbours(final Tile tile) {
        if(tile == null)
            return false;
        if(Math.abs(position.getX() - tile.getPosition().getX()) == 1 && (position.getY() - tile.getPosition().getY()) == 0)
            return true;
        if((position.getX() - tile.getPosition().getX()) == 0 && Math.abs(position.getY() - tile.getPosition().getY()) == 1) 
            return true;
        return false;
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
    
    @Override
    public Tile clone() {
    	Point point = new Point();
    	point.setLocation(this.getPosition());
    	Tile tile = new Tile(point);
    	
    	tile.setContent(this.getContent());
    	
    	for(Orientation orientation: Orientation.values())
    		tile.getEdgeAt(orientation).setObstruction(this.getEdgeAt(orientation).getObstruction());
    	
    	return tile;
    }

    @Override
    public String toString() {
        String content = "";
        if (getContent() instanceof Barcode)
            content = "" + getContent().getValue();
        else if (getContent() instanceof TreasureObject)
            content = "o" + getContent().getValue();
        return getEdgeAt(Orientation.NORTH) + "\n" + getEdgeAt(Orientation.WEST)
                + String.format("%2s", (content)) + getEdgeAt(Orientation.EAST)
                + "\n" + getEdgeAt(Orientation.SOUTH);
    }
}