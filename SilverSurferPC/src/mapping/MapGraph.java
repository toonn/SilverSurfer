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
	public boolean canMoveNorth() {
		return getCurrentTile().getNorthEdge().isPassable();
	}
	/**
	 * Checks if the edge at this side is passable.
	 */
	public boolean canMoveEast() {
		return getCurrentTile().getEastEdge().isPassable();
	}
	/**
	 * Checks if the edge at this side is passable.
	 */
	public boolean canMoveSouth() {
		return getCurrentTile().getSouthEdge().isPassable();
	}
	/**
	 * Checks if the edge at this side is passable.
	 */
	public boolean canMoveWest() {
		return getCurrentTile().getWestEdge().isPassable();
	}
	
	/**
	 * Tries to move to the given orientation if possible, returns true if succeeds.
	 */
	public boolean moveToNextTile(Orientation orientation){
		if(orientation == Orientation.NORTH)
		{
			if(!canMoveNorth()) return false;
			else if (getCurrentTile().getNorthEdge().getOtherTile(getCurrentTile()) == null){
				if (getCurrentTile().getNorthEdge().isTile1(getCurrentTile())){
					getCurrentTile().getNorthEdge().setTile2(new Tile(getCurrentTile().getxCoordinate(),getCurrentTile().getyCoordinate()+1));
					getCurrentTile().getNorthEdge().getTile2().setSouthEdge(getCurrentTile().getNorthEdge());
					setCurrentTile(getCurrentTile().getNorthEdge().getOtherTile(getCurrentTile()));
					return true;
				}
				else if (getCurrentTile().getNorthEdge().isTile2(getCurrentTile())){
					getCurrentTile().getNorthEdge().setTile1(new Tile(getCurrentTile().getxCoordinate(),getCurrentTile().getyCoordinate()+1));
					getCurrentTile().getNorthEdge().getTile1().setSouthEdge(getCurrentTile().getNorthEdge());
					setCurrentTile(getCurrentTile().getNorthEdge().getOtherTile(getCurrentTile()));
					return true;
				}
			}
			else{
				setCurrentTile(getCurrentTile().getNorthEdge().getOtherTile(getCurrentTile()));
				return true;
			}
			return false;
		}
		if(orientation == Orientation.EAST)
		{
			if(!canMoveEast()) return false;
			else if (getCurrentTile().getEastEdge().getOtherTile(getCurrentTile()) == null){
				if (getCurrentTile().getEastEdge().isTile1(getCurrentTile())){
					getCurrentTile().getEastEdge().setTile2(new Tile(getCurrentTile().getxCoordinate()+1,getCurrentTile().getyCoordinate()));
					getCurrentTile().getEastEdge().getTile2().setWestEdge(getCurrentTile().getEastEdge());
					setCurrentTile(getCurrentTile().getEastEdge().getOtherTile(getCurrentTile()));
					return true;
				}
				else if (getCurrentTile().getEastEdge().isTile2(getCurrentTile())){
					getCurrentTile().getEastEdge().setTile1(new Tile(getCurrentTile().getxCoordinate()+1,getCurrentTile().getyCoordinate()));
					getCurrentTile().getEastEdge().getTile1().setWestEdge(getCurrentTile().getEastEdge());
					setCurrentTile(getCurrentTile().getEastEdge().getOtherTile(getCurrentTile()));
					return true;
				}
			}
			else{
				setCurrentTile(getCurrentTile().getEastEdge().getOtherTile(getCurrentTile()));
				return true;
			}
			return false;
		}
		if(orientation == Orientation.SOUTH)
		{
			if(!canMoveSouth()) return false;
			else if (getCurrentTile().getSouthEdge().getOtherTile(getCurrentTile()) == null){
				if (getCurrentTile().getSouthEdge().isTile1(getCurrentTile())){
					getCurrentTile().getSouthEdge().setTile2(new Tile(getCurrentTile().getxCoordinate(),getCurrentTile().getyCoordinate()-1));
					getCurrentTile().getSouthEdge().getTile2().setNorthEdge(getCurrentTile().getSouthEdge());
					setCurrentTile(getCurrentTile().getSouthEdge().getOtherTile(getCurrentTile()));
					return true;
				}
				else if (getCurrentTile().getSouthEdge().isTile2(getCurrentTile())){
					getCurrentTile().getSouthEdge().setTile1(new Tile(getCurrentTile().getxCoordinate(),getCurrentTile().getyCoordinate()-1));
					getCurrentTile().getSouthEdge().getTile1().setNorthEdge(getCurrentTile().getSouthEdge());
					setCurrentTile(getCurrentTile().getSouthEdge().getOtherTile(getCurrentTile()));
					return true;
				}
			}
			else{
				setCurrentTile(getCurrentTile().getSouthEdge().getOtherTile(getCurrentTile()));
				return true;
			}
			return false;
		}
		if(orientation == Orientation.WEST)
		{
			if(!canMoveWest()) return false;
			else if (getCurrentTile().getWestEdge().getOtherTile(getCurrentTile()) == null){
				if (getCurrentTile().getWestEdge().isTile1(getCurrentTile())){
					getCurrentTile().getWestEdge().setTile2(new Tile(getCurrentTile().getxCoordinate()-1,getCurrentTile().getyCoordinate()));
					getCurrentTile().getWestEdge().getTile2().setEastEdge(getCurrentTile().getWestEdge());
					setCurrentTile(getCurrentTile().getWestEdge().getOtherTile(getCurrentTile()));
					return true;
				}
				else if (getCurrentTile().getWestEdge().isTile2(getCurrentTile())){
					getCurrentTile().getWestEdge().setTile1(new Tile(getCurrentTile().getxCoordinate()-1,getCurrentTile().getyCoordinate()));
					getCurrentTile().getWestEdge().getTile1().setEastEdge(getCurrentTile().getWestEdge());
					setCurrentTile(getCurrentTile().getWestEdge().getOtherTile(getCurrentTile()));
					return true;
				}
			}
			else{
				setCurrentTile(getCurrentTile().getWestEdge().getOtherTile(getCurrentTile()));
				return true;
			}
			return false;
		}
		return false;
	}
	
	/**
	 * Adds a given obstruction to the edge on the given orientation of the current tile.
	 */
	public void addObstruction(Obstruction obst, Orientation orientation)
	{
		if(orientation == Orientation.NORTH)
		{
			getCurrentTile().getNorthEdge().setObstruction(obst);
		}
		if(orientation == Orientation.EAST)
		{
			getCurrentTile().getEastEdge().setObstruction(obst);
		}
		if(orientation == Orientation.SOUTH)
		{
			getCurrentTile().getSouthEdge().setObstruction(obst);
		}
		if(orientation == Orientation.WEST)
		{
			getCurrentTile().getWestEdge().setObstruction(obst);
		}
	}
	
	/**
	 * Returns the obstruction on the given orientation of the current tile.
	 */
	public Obstruction getObstruction(Orientation orientation)
	{
		if(orientation == Orientation.NORTH)
		{
			return getCurrentTile().getNorthEdge().getObstruction();
		}
		if(orientation == Orientation.EAST)
		{
			return getCurrentTile().getEastEdge().getObstruction();
		}
		if(orientation == Orientation.SOUTH)
		{
			return getCurrentTile().getSouthEdge().getObstruction();
		}
		//if(orientation == Orientation.WEST)
		else
		{
			return getCurrentTile().getWestEdge().getObstruction();
		}
	}
}
