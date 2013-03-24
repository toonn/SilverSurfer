package mapping;

public class Seesaw extends TileContent {
	
	private Orientation orientation;
	private boolean positionIsKnown;

	public Seesaw(final Tile tile, final Orientation orientation) {
		super(tile, 0);
		this.orientation = orientation;
	}
	
	public Seesaw(final Tile tile, final Orientation orientation, int value) {
		super(tile, value);
		this.orientation = orientation;
	}

	/**
	 * Flips the Seesaw. If the current Seesaw-edge is up, it is set down and vice versa.
	 */
	public void flipSeesaw() {
		for(Edge edge: tile.getEdges())
			if(edge.getObstruction() == Obstruction.SEESAW_DOWN)
				edge.setObstruction(Obstruction.SEESAW_UP);
			else if(edge.getObstruction() == Obstruction.SEESAW_UP)
				edge.setObstruction(Obstruction.SEESAW_DOWN);
	}
	
	public Orientation getOrientation() {
		return orientation;
	}
	
	public boolean isUp() {
		for(Edge edge: tile.getEdges()) 
			if(edge.getObstruction() == Obstruction.SEESAW_UP)
				return true;
		return false;
	}
    
    public void setValue(final int value) {
        this.value = value;
    }
    
    public boolean getPositionIsKnown() {
		return positionIsKnown;
	}
	
	public void setPositionIsKnown(boolean positionIsknown) {
		this.positionIsKnown = positionIsknown;
	}
}