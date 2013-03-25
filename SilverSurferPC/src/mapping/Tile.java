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
    
    public int getAmtOfWalls(){
    	int amt = 0;
    	for (Edge edge : getEdges())
			if(edge.getObstruction() == Obstruction.WALL)
				amt++;
    	return amt;
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
        	if(getEdgeAt(orientation).getObstruction().isPassable())
            	neighbours.add(getEdgeAt(orientation).getNeighbour(this));
        return neighbours;
    }

    public ArrayList<Tile> getReachableNeighboursIgnoringSeesaw() {
        final ArrayList<Tile> neighbours = new ArrayList<Tile>();
        for (final Orientation orientation : Orientation.values())
        	if(getEdgeAt(orientation).getObstruction() != Obstruction.WALL)
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
    
    public String getToken(){
    	String tokenHead = "";
    	String orientationPart = "";

    	if (this.getAmtOfWalls() == 4)
    		tokenHead = "Closed";
    	else if (this.getAmtOfWalls() == 3){
    		tokenHead = "DeadEnd";
    		for (Orientation o : Orientation.values())
				if (getEdgeAt(o).getObstruction().equals(Obstruction.WHITE_LINE))
					orientationPart += "."+ Orientation.toToken(o.getOppositeOrientation());
    	}
    	else if (this.getAmtOfWalls() == 2){
    		if (getEdgeAt(Orientation.NORTH).equals(Obstruction.WALL) && getEdgeAt(Orientation.EAST).equals(Obstruction.WALL)){
    			tokenHead = "Corner";
    			orientationPart = ".N";
    		}
    		else if (getEdgeAt(Orientation.EAST).equals(Obstruction.WALL) && getEdgeAt(Orientation.SOUTH).equals(Obstruction.WALL)){
    			tokenHead = "Corner";
    			orientationPart = ".E";
    		}
    		else if (getEdgeAt(Orientation.SOUTH).equals(Obstruction.WALL) && getEdgeAt(Orientation.WEST).equals(Obstruction.WALL)){
    			tokenHead = "Corner";
    			orientationPart = ".S";
    		}
    		else if (getEdgeAt(Orientation.WEST).equals(Obstruction.WALL) && getEdgeAt(Orientation.NORTH).equals(Obstruction.WALL)){
    			tokenHead = "Corner";
    			orientationPart = ".W";
    		}
    		else {
    			tokenHead = "Straight";
    			if (getEdgeAt(Orientation.WEST).equals(Obstruction.WALL) && getEdgeAt(Orientation.EAST).equals(Obstruction.WALL))
        			orientationPart = ".E";
    			else
    				orientationPart = ".N";

    		}
    	}
    	else if (this.getAmtOfWalls() == 1){
    		tokenHead = "T";
    		for (Orientation o : Orientation.values())
				if (getEdgeAt(o).getObstruction() == Obstruction.WALL)
					orientationPart += "."+ Orientation.toToken(o);
    	}
    	else if (this.getAmtOfWalls() == 0)
    		tokenHead = "Cross";
    	
    	String barcodePart = "";
    	if (this.getContent() instanceof Barcode)
    		barcodePart = "." + ((Barcode) this.getContent()).getValue();
    	
    	String startBasePart = "";
    	if (this.getContent() instanceof StartBase)
    		startBasePart = ".S" + ((StartBase) this.getContent()).getValue() + Orientation.toToken( ((StartBase) this.getContent()).getOrientation());
    	
    	String seesawPart = "";
    	if (this.getContent() instanceof Seesaw){
    		tokenHead = "Seesaw";
    		orientationPart = "." + Orientation.toToken(((Seesaw)this.getContent()).getOrientation());
    	}
    	
    	String treasurePart = "";
    	if (this.getContent() instanceof TreasureObject)
    		treasurePart = ".V";
    	
    	return tokenHead+orientationPart+barcodePart+startBasePart+seesawPart+treasurePart;
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