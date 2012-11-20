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
		// gebeurt nooit
		throw new IllegalStateException("bij getStartingTile in MapGraph");
	}
	public int[] getCurrentTileCoordinates() {
		return currentTileCoordinates;
	}

	public void setCurrentTileCoordinates(int x, int y) {
		currentTileCoordinates[0] = x;
		currentTileCoordinates[1] = y;
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
	public boolean canMoveTo(Orientation orientation)
	{	
		return getCurrentTile().getEdge(orientation).isPassable();
	}
	
//	public void moveToNextTile(Orientation orientation){
//			if(this.canMoveTo(orientation)){
//				setCurrentTileCoordinates(getCurrentTileCoordinates()[0] + Orientation.getArrayToFindNeighbourRelative(orientation)[0], 
//						getCurrentTileCoordinates()[1] + Orientation.getArrayToFindNeighbourRelative(orientation)[1]);}
//		} 

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
	
//	
//	public static void main(String[] args) {
//		MapGraph map = new MapGraph();
//		Tile tile1 = new Tile();
//		Tile tile2 = new Tile();
//		Tile tile3 = new Tile();
//		tile1.getEdge(Orientation.NORTH).setObstruction(Obstruction.WALL);
//		map.setTileXY(0, 2, tile1);
//		map.setTileXY(0, 1, tile2);
//		map.setTileXY(1, 2, tile3);
//		tile3.getEdge(Orientation.WEST).setObstruction(Obstruction.WALL);
//		
//		System.out.println(tile1.getReachableNeighbours().get(0)==null);
//		System.out.println(tile1.getReachableNeighbours().get(1)== null);
//		System.out.println(tile1.getReachableNeighbours().get(2) == (null));
//		System.out.println(tile1.getReachableNeighbours().get(3) == (null));
//		
//		System.out.println(tile1.getEdge(Orientation.EAST).isPassable());
//		System.out.println(tile1.getEdge(Orientation.EAST).equals(tile3.getEdge(Orientation.WEST)));
//		System.out.println(tile1.getEdge(Orientation.NORTH).equals(tile2.getEdge(Orientation.SOUTH)));
//		
//	}
	
	public static void main(String[] args) {
		Tile tile = new Tile() ;
		System.out.println(tile.getxCoordinate());
		System.out.println(tile.getyCoordinate());
		MapReader map = new MapReader();
		MapGraph graph = MapReader.createMapFromFile(new File("resources/maze_maps/example_map.txt"), 0, 0);
		for(int j = 0; j <6 ; j++){
			for(int i = 0; i<4;i++){
				System.out.println(j + "en" + i);
				for(Orientation orientation: Orientation.values())
				System.out.println(!graph.getTileWithCoordinates(j, i).getEdge(orientation).isPassable());
			}
		}
		
		
	}
	
}