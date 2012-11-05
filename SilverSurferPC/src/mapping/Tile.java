package mapping;

public class Tile {

	private int xCoordinate;
	private int yCoordinate;
	
	private Edge northEdge;
	private Edge eastEdge;
	private Edge southEdge;
	private Edge westEdge;
	
	private TileContent content;
	
	public Tile(int x, int y){
		setxCoordinate(x);
		setyCoordinate(y);
	}
	/**
	 * Returns the x-coordinate of this tile, relative to the starting-tile.
	 * So; starting Tile.coordinates == (0,0),
	 * the tile just north of the starting tile is: (0,1)
	 * and the tile west of that one is: (-1,1).
	 * @return
	 */
	public int getxCoordinate() {
		return xCoordinate;
	}
	public void setxCoordinate(int xCoordinate) {
		this.xCoordinate = xCoordinate;
	}
	
	/**
	 * Returns the y-coordinate of this tile, relative to the starting-tile.
	 * So; starting Tile.coordinates == (0,0),
	 * the tile just north of the starting tile is: (0,1)
	 * and the tile west of that one is: (-1,1).
	 * @return
	 */
	public int getyCoordinate() {
		return yCoordinate;
	}
	public void setyCoordinate(int yCoordinate) {
		this.yCoordinate = yCoordinate;
	}
	
	
	/**
	 * Returns the edge that's north of this Tile.
	 */
	public Edge getNorthEdge() {
		return northEdge;
	}
	public void setNorthEdge(Edge northEdge) {
		this.northEdge = northEdge;
	}
	
	
	/**
	 * Returns the edge that's east of this Tile.
	 */
	public Edge getEastEdge() {
		return eastEdge;
	}
	public void setEastEdge(Edge eastEdge) {
		this.eastEdge = eastEdge;
	}
	
	
	/**
	 * Returns the edge that's south of this Tile.
	 */
	public Edge getSouthEdge() {
		return southEdge;
	}
	public void setSouthEdge(Edge southEdge) {
		this.southEdge = southEdge;
	}
	
	
	/**
	 * Returns the edge that's west of this Tile.
	 */
	public Edge getWestEdge() {
		return westEdge;
	}
	public void setWestEdge(Edge westEdge) {
		this.westEdge = westEdge;
	}
	
	public Edge getEdge(Orientation orientation)
	{
		if(orientation == Orientation.NORTH)
		{
			return northEdge;
		}
		if(orientation == Orientation.EAST)
		{
			return eastEdge;
		}
		if(orientation == Orientation.SOUTH)
		{
			return southEdge;
		}
		if(orientation == Orientation.WEST)
		{
			return westEdge;
		}
		return null;
	}
	
	public void setEdge(Edge edge, Orientation orientation)
	{
		if(orientation == Orientation.NORTH)
		{
			northEdge = edge;
		}
		if(orientation == Orientation.EAST)
		{
			eastEdge = edge;
		}
		if(orientation == Orientation.SOUTH)
		{
			southEdge = edge;
		}
		if(orientation == Orientation.WEST)
		{
			westEdge = edge;
		}
	}
	
	/**
	 * Returns the content of this tile. Might be a barcode or null.
	 * @return
	 */
	public TileContent getContent() {
		return content;
	}
	public void setContent(TileContent cont) {
		this.content = cont;
	}

	@Override
	public String toString() {
		return "Content: " + getContent() + " Edges -- North: " + getNorthEdge() + " East: " + getEastEdge() + " South: "+getSouthEdge()+ " West: "+getWestEdge();
	}
}
