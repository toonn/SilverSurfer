package mapping;

public class Edge {

	private Tile tile1;
	private Tile tile2;
	private Obstruction obstruction;
	
	
	/**
	 * Creates an edge between two tiles with the defined obstruction in between them.
	 */
	public Edge(Tile t1, Tile t2, Obstruction obst){
		setTile1(t1);
		setTile2(t2);
		setObstruction(obst);
	}
	
	/**
	 * Creates an edge between two tiles with nothing in between them.
	 */
	public Edge(Tile t1, Tile t2){
		this(t1,t2,null);
	}
	/**
	 * Gets the first Tile of this edge.
	 */
	public Tile getTile1() {
		return tile1;
	}
	public void setTile1(Tile tile1) {
		this.tile1 = tile1;
	}
	
	
	/**
	 * Gets the second Tile of this edge.
	 */
	public Tile getTile2() {
		return tile2;
	}
	public void setTile2(Tile tile2) {
		this.tile2 = tile2;
	}
	
	
	/**
	 * Gets the obstruction that is between the two Tiles of this edge.
	 * might be 'null'.
	 */
	public Obstruction getObstruction() {
		return obstruction;
	}
	public void setObstruction(Obstruction obstruction) {
		this.obstruction = obstruction;
	}
	
	/**
	 * Checks if this edge should be able to be ridden over.
	 * @return true if there is no wall or nothingness on the edge.
	 */
	public boolean isPassable() {
		return getObstruction() != Obstruction.WALL && getObstruction() != Obstruction.NOTHINGNESS;
	}
	
	/**
	 * Returns true if Tile t == getTile1()
	 */
	public boolean isTile1(Tile t){
		return (t == getTile1());
	}
	/**
	 * Returns true if Tile t == getTile2()
	 */
	public boolean isTile2(Tile t){
		return (t == getTile2());
	}
	/**
	 * Returns the other tile on this edge, the one that isn't 't'.
	 */
	public Tile getOtherTile(Tile t){
		if (t == getTile1())
			return getTile2();
		else if (t == getTile2())
			return getTile1();
		else return null;

	}

}
