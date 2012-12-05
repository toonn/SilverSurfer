package mapping;

import java.io.File;
import java.util.*;

public class MapGraph {

	private int[] startingTileCoordinates = new int[2];
	private int[] currentTileCoordinates = new int[2];
	private Set<Tile> tiles = new HashSet<Tile>();

	
	/**
	 * Creates a new Map
	 */
	public MapGraph(){
		setStartingTileCoordinates(0,0);
		setCurrentTileCoordinates(0,0);
	}
	
	/**
	 * Creates a new Map with the tile with defined coordinates as coordinates as starting Tile.
	 * @param start
	 */
	public MapGraph(int x, int y){
		setStartingTileCoordinates(x, y);
		setCurrentTileCoordinates(x, y);
		
	}
	
	public Set<Tile> getTiles(){
		return tiles;
	}
	
	/**
	 * Returns the Tile on which this map was started.
	 */
	public Tile getStartingTile() {
		for (Tile tile : tiles) {
			if (tile.getxCoordinate() == getStartingTileCoordinates()[0]
					&& tile.getyCoordinate() == getStartingTileCoordinates()[1]) {
				return tile;
			}
		}
		// gebeurt nooit
		throw new IllegalStateException("bij getStartingTile in MapGraph");
	}
	

	public int[] getStartingTileCoordinates() {
		return startingTileCoordinates;
	}
	
	public void setStartingTileCoordinates(int x, int y) {
		startingTileCoordinates[0] = x;
		startingTileCoordinates[1] = y;
	}


	/**
	 * Returns the tile the simulator or robot is currently on.
	 */
	public Tile getCurrentTile() {
		for (Tile tile : tiles) {
			if (tile.getxCoordinate() == getCurrentTileCoordinates()[0]
					&& tile.getyCoordinate() == getCurrentTileCoordinates()[1]) {
				return tile;
			}
		}
		return null;
	}
	
	public int[] getCurrentTileCoordinates() {
		return currentTileCoordinates;
	}

	public void setCurrentTileCoordinates(int x, int y) {
		currentTileCoordinates[0] = x;
		currentTileCoordinates[1] = y;
	}
	
	
	
	public Tile getTileWithCoordinates(int xCoordinate, int yCoordinate) {
		for (Tile tile : tiles) {
			if (tile.getxCoordinate() == xCoordinate
					&& tile.getyCoordinate() == yCoordinate) {
				return tile;
			}
		}
		// als er op deze coordinaten nog geen tile staat
		return null;
	}

	public void addContentToCurrentTile(TileContent code){
		getCurrentTile().setContent(code);
	}
	public TileContent getContentCurrentTile(){
		return getCurrentTile().getContent();
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
	
	public void setTileXY(int x, int y, Tile tile){
		tile.setxCoordinate(x);
		tile.setyCoordinate(y);
		
		for (Tile mapTile: tiles){
			int[] ar = null;
			Orientation orientation;
				if(mapTile!=null && tile.areNeighbours(mapTile)){
					ar = new int[2];
					ar[0] = mapTile.getxCoordinate()-tile.getxCoordinate();
					ar[1] = mapTile.getyCoordinate()-tile.getyCoordinate();
					
					orientation = Orientation.getOrientationOfArray(ar);
					
					tile.replaceEdge(orientation,mapTile.getEdge(orientation.getOppositeOrientation()));
				}
			
	}
		if(getTileWithCoordinates(x, y)!=(null)){
			removeTile(x,y);
		}
		else {
			tiles.add(tile);
		
		}
	}

	public void removeTile(int x, int y) {
		getTileWithCoordinates(x,y).terminate();
		tiles.remove(getTileWithCoordinates(x,y));
	}
	
	@Override
	public String toString() {
		String s = "MapGraph content:";
		for (Tile t : tiles) {
			s+= "\n+" + t.toString();
		}
		return s;
	}
	
}