package mapping;

public class MapGraph {

	private Tile startingTile;
	private Tile currentTile;

	/**
	 * Creates a new Map with the defined tile as starting Tile.
	 * @param start
	 */
	public MapGraph(Tile start){
		setStartingTile(start);
		setCurrentTile(start);
	}
	/**
	 * Returns the Tile on which this map was started.
	 * (should have coordinates (0,0)).
	 */
	public Tile getStartingTile() {
		return startingTile;
	}
	public void setStartingTile(Tile startingTile) {
		this.startingTile = startingTile;
	}


	/**
	 * Returns the tile the simulator or robot is currently on.
	 */
	public Tile getCurrentTile() {
		return currentTile;
	}
	public void setCurrentTile(Tile currentTile) {
		this.currentTile = currentTile;
	}

	public void addContentToCurrentTile(TileContent code){
		currentTile.setContent(code);
	}
	public TileContent getContentCurrentTile(){
		return getCurrentTile().getContent();
	}

	/**
	 * Checks if the edge at this side is passable.
	 */
	public boolean canMoveTo(Orientation orientation)
	{
		return getCurrentTile().getEdge(orientation).isPassable();
	}

	/**
	 * Tries to move to the given orientation if possible, returns true if succeeds.
	 */
	public boolean moveToNextTile(Orientation orientation){
		if(this.canMoveTo(orientation))
		{
			// the other side of the edge is not yet explored
			if(this.getCurrentTile().getEdge(orientation).getOtherTile(this.getCurrentTile()) == null)
			{
				Tile newTile = new Tile(this.getCurrentTile().getxCoordinate() + orientation.getRelativeXCoordinate(),
										this.getCurrentTile().getyCoordinate() + orientation.getRelativeYCoordinate());
				newTile.setEdge(this.getCurrentTile().getEdge(orientation), orientation.getOppositeOrientation());

				// the current tile is tile 1
				if(this.getCurrentTile().getEdge(orientation).isTile1(this.getCurrentTile()))
				{
					this.getCurrentTile().getEdge(orientation).setTile2(newTile);

				}
				// the current tile is tile 2
				else
				{
					this.getCurrentTile().getEdge(orientation).setTile1(newTile);
				}
			}
			// adjust the current tile to the new tile
			this.setCurrentTile(this.getCurrentTile().getEdge(orientation).getOtherTile(this.getCurrentTile()));
			return true;
		}
		else
		{
			System.out.println("you bumped into a wall");
			return false;
		}
	}

	/**
	 * Adds a given obstruction to the edge on the given orientation of the current tile.
	 */
	public void addObstruction(Obstruction obst, Orientation orientation)
	{
		if(this.getCurrentTile().getEdge(orientation) == null)
		{
			this.getCurrentTile().setEdge(new Edge(this.getCurrentTile(), null, obst), orientation);
		}
		else
		{
			this.getCurrentTile().getEdge(orientation).setObstruction(obst);
		}
	}

	/**
	 * Returns the obstruction on the given orientation of the current tile.
	 */
	public Obstruction getObstruction(Orientation orientation)
	{
		return getCurrentTile().getEdge(orientation).getObstruction();
	}
}
