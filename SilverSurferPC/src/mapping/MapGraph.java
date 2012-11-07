package mapping; 

import exception.SimulatorCrashedException;

/**
 * startingTileCoordinates en currentTileCoordinates krijgen een x en een y zoals men een 
 * matrix leest dus [00 01][10 11] (Het relatieve coordinatenstelsel
 * terwijl de tile-Objecten zich in het werkelijk coordinatensysteem bevinden , 
 * de coordinaten van deze tiles komen overeen met [00 10][01 11]
 * 
 * Elke methode in deze klasse is aangepast zodat je het relatieve coordinatensysteem kan gebruiken
 * 
 *
 */

public class MapGraph {

	private int[] startingTileCoordinates = new int[2];
	private int[] currentTileCoordinates = new int[2];
	private Tile[][] tiles = new Tile[0][0];

	/**
	 * Creates a new Map with the defined length as its length,
	 * and the defined width as its width.
	 * The starting Tile coordinates are initiades 00
	 */
	public MapGraph(int lengthx, int lengthy){
		tiles = new Tile[lengthx][lengthy];
		setStartingTileCoordinates(0,0);
		setCurrentTileCoordinates(0,0);
	}
	
	/**
	 * Creates a new Map with the defined length as its length,
	 * and the defined width as its width.
	 * the startingtileCoordinates with defined coordinates as coordinates as starting Tile.
	 * @param start
	 */
	public MapGraph(int x, int y, int lengthx, int lengthy){
		tiles = new Tile[lengthx][lengthy];
		setStartingTileCoordinates(x, y);
		setCurrentTileCoordinates(x, y);
		
	}
	/**
	 * Returns the Tile on which this map was started.
	 */
	public Tile getStartingTile() {
		return tiles[getStartingTileCoordinates()[0]][getStartingTileCoordinates()[1]];
	}
	

	public int[] getStartingTileCoordinates() {
		return startingTileCoordinates;
	}
	
	public void setStartingTileCoordinates(int x, int y) {
		startingTileCoordinates[0] = x;
		startingTileCoordinates[1] = y;
		System.out.println(this);
		System.out.println(startingTileCoordinates[0]);
		System.out.println(startingTileCoordinates[1]);
	}


	/**
	 * Returns the tile the simulator or robot is currently on.
	 */
	public Tile getCurrentTile() {
		return tiles[getCurrentTileCoordinates()[0]][getCurrentTileCoordinates()[1]];
	}
	public int[] getCurrentTileCoordinates() {
		return currentTileCoordinates;
	}

	public void setCurrentTileCoordinates(int x, int y) {
		currentTileCoordinates[0] = x;
		currentTileCoordinates[1] = y;
		System.out.println("current");
		System.out.println(this);
		System.out.println(currentTileCoordinates[0]);
		System.out.println(currentTileCoordinates[1]);
	}

	public void addContentToCurrentTile(TileContent code){
		getCurrentTile().setContent(code);
	}
	public TileContent getContentCurrentTile(){
		return getCurrentTile().getContent();
	}

	/**
	 * Checks if the edge at this side is passable.
	 */
	public boolean canMoveTo(Orientation orientation) throws SimulatorCrashedException
	{	
		if(getCurrentTile() == null){
			throw new SimulatorCrashedException();
		}
		else if(getCurrentTile().getEdge(orientation)==null)
			throw new IllegalArgumentException("edge is null in canMoveTo");
		
		return getCurrentTile().getEdge(orientation).isPassable();
	}
	/**
	 * Tries to move to the given orientation if possible, returns true if succeeds.
	 */
	public void moveToNextTile(Orientation orientation){
		try {
			if(this.canMoveTo(orientation)){
				setCurrentTileCoordinates(getCurrentTileCoordinates()[0] + Orientation.getArrayToFindNeighbourRelative(orientation)[0], 
						getCurrentTileCoordinates()[1] + Orientation.getArrayToFindNeighbourRelative(orientation)[1]);}
		} catch (SimulatorCrashedException e) {
			
		}
			
	} 

	/**
	 * Adds a given obstruction to the edge on the given orientation of the current tile.
	 */
	public void addObstruction(Obstruction obst, Orientation orientation)
	{
		if(getCurrentTile() == null){
			throw new IllegalArgumentException("currenttile is null in addobstruction");
		}
		else if(getCurrentTile().getEdge(orientation)==null)
			throw new IllegalArgumentException("edge is null in addobstruction");
			
		this.getCurrentTile().getEdge(orientation).setObstruction(obst);
	}

	/**
	 * Returns the obstruction on the given orientation of the current tile.
	 * ofwel wall ofwel null
	 */
	public Obstruction getObstruction(Orientation orientation)
	{
		return getCurrentTile().getEdge(orientation).getObstruction();
	}
	
	
	public Tile[][] getTiles() {
		return tiles;
	}
	
	public Tile getTileXY(int x, int y){
		return tiles[x][y];
	}
	
	public void setTileXY(int x, int y, Tile tile){
		tile.setxCoordinate(y);
		tile.setyCoordinate(x);
		
		for (int i = 0; i < tiles.length; i++){
			int[] ar = null;
	//		Edge edge;
			Orientation orientation;
			for (int j = 0; j < tiles[i].length; j++){
				if(tiles[i][j]!=null && tile.areNeighbours(tiles[i][j])){
					ar = new int[2];
					ar[0] = tile.getxCoordinate()-tiles[i][j].getxCoordinate();
					ar[1] = tile.getyCoordinate()-tiles[i][j].getyCoordinate();
					
					orientation = Orientation.getOrientationOfArray(ar);
					
				tile.replaceEdge(Orientation.getOppositeOrientation(orientation), tiles[i][j].getEdge(orientation));
				}
			}
	}
		if(tiles[x][y]!=(null)){
			removeTile(x,y);
		}
		else {
			tiles[x][y] = tile;
		
		}
	}

	public void removeTile(int x, int y) {
		getTileXY(x,y).terminate();
		tiles[x][y] = null;
		
	}
}
