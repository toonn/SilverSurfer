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
	 * Tries to move North if possible, returns true if succeeds.
	 */
	public boolean moveNorth(){
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
	/**
	 * Tries to move East if possible, returns true if succeeds.
	 */
	public boolean moveEast(){
		if(!canMoveNorth()) return false;
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
	/**
	 * Tries to move South if possible, returns true if succeeds.
	 */
	public boolean moveSouth(){
		if(!canMoveNorth()) return false;
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
	/**
	 * Tries to move West if possible, returns true if succeeds.
	 */
	public boolean moveWest(){
		if(!canMoveNorth()) return false;
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
	
	/**
	 * Adds a given obstruction to edge north of the current tile.
	 */
	public void addObstructionNorth(Obstruction obst){
		getCurrentTile().getNorthEdge().setObstruction(obst);
	}
	/**
	 * Adds a given obstruction to edge east of the current tile.
	 */
	public void addObstructionEast(Obstruction obst){
		getCurrentTile().getEastEdge().setObstruction(obst);
	}
	/**
	 * Adds a given obstruction to edge south of the current tile.
	 */
	public void addObstructionSouth(Obstruction obst){
		getCurrentTile().getSouthEdge().setObstruction(obst);
	}
	/**
	 * Adds a given obstruction to edge west of the current tile.
	 */
	public void addObstructionWest(Obstruction obst){
		getCurrentTile().getWestEdge().setObstruction(obst);
	}
	
	/**
	 * Returns the obstruction north of the current tile.
	 */
	public Obstruction getObstructionNorth(){
		return getCurrentTile().getNorthEdge().getObstruction();
	}
	/**
	 * Returns the obstruction east of the current tile.
	 */
	public Obstruction getObstructionEast(){
		return getCurrentTile().getEastEdge().getObstruction();
	}
	/**
	 * Returns the obstruction south of the current tile.
	 */
	public Obstruction getObstructionSouth(){
		return getCurrentTile().getSouthEdge().getObstruction();
	}
	/**
	 * Returns the obstruction west of the current tile.
	 */
	public Obstruction getObstructionWest(){
		return getCurrentTile().getWestEdge().getObstruction();
	}
}
