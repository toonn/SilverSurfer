package mapping;


/**
 * A class of edges involving two tiles, a wall and a number to store in which directions
 * the two tiles are attached to the edge.
 * 
 * @invar	Each edge must have 2 proper tiles attached to it.
 * 			| hasProperTiles()
 * @invar	The number to store in which directions the two squares are attached to the border
 * 			must be -1 as long as no square is attached. From the moment the first square is attached,
 * 			the number change to a value 0, 1 or 2 depending on the direction. 
 * 			From that moment on, it will remain until the border is terminated.
 * 
 * @author Gerlinde Van Roey
 *
 */
public class Edge {

	/**
	 * Variable referencing the tile to which this edge is attached.
	 * The edge is placed at the north or the east side of this tile.
	 */
	private Tile tile1;
	
	/**
	 * Variable referencing the tile to which this edge is attached.
	 * The edge is placed at the south or the west of this tile
	 */
	private Tile tile2;
	private Obstruction obstruction;
	
	
	
	/**
	 * Initialize this new edge with a value of -1 for the number of the PairDirection.
	 */
	public Edge(){
		setNumberPairDirections(-1);
	}
	

	/**
	 * Return a square to which this border is attached.
	 * 		A null reference is returned if no square is attached.
	 */
	public Tile getTile1() {
		return tile1;
	}
	
	/**
	 * Return a square to which this border is attached.
	 * 		A null reference is returned if no square is attached.
	 */
	public Tile getTile2() {
		return tile2;
	}
	
	
	/**
	 * Set the square stored at square1 attached to this border to the given square.
	 *  
	 * @param 	square1
	 * 			The new square to be attached to this border.
	 * @post	The new square is equal to the given square.	
	 * 			| new.getSquare1() == square1
	 */
	private void setTile1(Tile tile1) {
		this.tile1 = tile1;
	}
	
	/**
	 * Set the square stored at square2 attached to this border to the given square.
	 *  
	 * @param 	square2
	 * 			The new square to be attached to this border.
	 * @post	The new square is equal to the given square.	
	 * 			| new.getSquare2() == square2
	 */
	private void setTile2(Tile tile2) {
		this.tile2 = tile2;
	}
	
	/**
	 * Return the directionpair-number associated with the direction(s) of the square(s) 
	 * that is/are stored in this border.
	 * The several values can be 0 or 1, depending on the rest you obtain by dividing 
	 * the number attributed to the direction by three. 
	 */
	public int getNumberPairDirections(){
		return this.numberPairDirections;
	}
	
	/**
	 * Set the directionpair-number associated with the direction(s) of the square(s)
	 * that is/are stored in this border.
	 * 
	 * @param 	numberPairDirections
	 * 			The number of the directionpair of the square(s) stored in this border.
	 */
	public void setNumberPairDirections(int numberPairDirections){
		this.numberPairDirections = numberPairDirections;
	}
	
	/**
	 * Variable registering the number of the directionpair of the square(s) stored in this border.
	 */
	private int numberPairDirections;
	
	
	/**
	 * Terminate this border.
	 * 
	 * @post	This border is terminated if no squares are associated with it anymore.
	 * 			| if(this.howManySquaresAttached() == 0)
	 * 			|  then (new.isTerminated())
	 */
	public void terminate(){
		if(this.howManyTilesAttached() == 0)
		{this.isTerminated = true;}
	}
	/**
	 * Check whether this border is terminated.
	 */
	public boolean isTerminated(){
		return this.isTerminated;
	}
	/**
	 * Variable registering whether this border is terminated.
	 */
	private boolean isTerminated;
	
	
	
	/**
	 * Return how many squares are attached to the border.
	 * 
	 * @return	The number of squares attached to this border.
	 */
	public int howManyTilesAttached(){
		int numberOfSquares = 0;
		if(getTile1() != null){
			numberOfSquares++;
		}
		if(getTile2() != null){
			numberOfSquares++;
		}
		return numberOfSquares;
	}
	
	/**
	 * Return the square that shares this border with the given square.
	 * These squares act like neighbours.
	 * 		A null reference is returned if the given square has no neighbour.
	 * 
	 * @param	square
	 * 			The square to get the neighbour of.
	 * @return	The neighboursquare of the given square if the given square is attached to this border
	 * 			and if this border is attached to two squares.
	 * 			If not, return null.
	 * 			| if( howManySquaresAttached() == 2 && ( getSquare1().equals(square) )
	 * 			|  then result == getSquare2()
	 * 			| else if ( howManySquaresAttached() == 2 && ( getSquare1().equals(square) )
	 * 			|  then result == getSquare1()
	 * 			| else ( result == null )		
	 * 			
	 */
	public Tile getNeighbour(Tile tile){
		if(howManyTilesAttached() == 2)
		{
		if(getTile1().equals(tile)){return getTile2();}
		else if(getTile2().equals(tile)){return getTile1();} }
		return null;
	}
	
	/**
	 * Check whether the square stored in square1 references this border as one of its borders.
	 * 
	 * @return	True if and only if the square stored in square1 references this border as 
	 * 			one of its borders.
	 */
	private boolean doesTile1ReferenceThisEdge(){
		for(Orientation orientation : Orientation.values()){
			if(ExtMath.isNumberDirectionEven(orientation) && 
					Orientation.getNumberOrientation(orientation)%3 == getNumberPairDirections() ){
				if(getTile1().getEdge(orientation) == this)
					return true;}
			}
		return false;}
	
	
	/**
	 * Check whether the square stored in square1 references this border as one of its borders.
	 * 
	 * @return	True if and only if the square stored in square1 references this border as 
	 * 			one of its borders.
	 */
	private boolean doesTile2ReferenceThisEdge(){
		for(Orientation orientation : Orientation.values()){
			if(!ExtMath.isNumberDirectionEven(orientation)&& 
					Orientation.getNumberOrientation(orientation)%3 == getNumberPairDirections()){
				if(getTile2().getEdge(orientation) == this)
					return true;}
			}
		return false;}

	/**
	 * Check whether this border has proper squares attached to it.
	 * 
	 * @return	True if and only if this border does not reference an effective square, or 
	 * 			if a square referenced by this border in turn references this border as 
	 * 			the border to which it is attached.
	 * 			| result ==
	 * 			|	( ( (getSquare1() == null) || 
	 *			|       (doesSquare1ReferenceThisBorder()) )
	 *			|			&&
	 *			| 	  ( (getSquare2() == null)||
	 *			|   	(doesSquare2ReferenceThisBorder()) ) )
	 * 			
	 */
	public boolean hasProperTiles(){
		
		return( 
			( (getTile1() == null) || 
				(doesTile1ReferenceThisEdge()) )
							&&
				 ( (getTile2() == null)||
				 (doesTile2ReferenceThisEdge()) ) );
	}
	
	

	
	/**
	 * Set the square attached to this border to the given square and store it at the right field.
	 * 		If the border is located in the north, east or ceiling of the given square,
	 * 		that square is stored in square1.
	 * 		If the border is located in the south, west or floor of the given square,
	 * 		that square is stored in square2.
	 * 
	 * @param 	direction
	 * 			The direction in which the border is located to the square.
	 * @param 	square
	 * 			The new square for this border.
	 * @post	If the given square is effective, and it does not yet reference 
	 * 			this border as one of its borders, return.
	 * 			Else, the new square is equal to the given square and is stored at the right field.
	 * 			| if(ExtMath.isNumberDirectionEven(direction))
	 * 			|  then (new.getSquare1() == square) 
	 * 			| if(! ExtMath.isNumberDirectionEven(direction))
	 * 			|  then (new.getSquare2() == square)
	 */
	public void setTileAtRightField(Orientation orientation, Tile tile){
		if(tile != null && !(tile.getEdge(orientation) == this) )
				return;
		if(ExtMath.isNumberDirectionEven(orientation)){
			this.setTile1(tile);
		}
		else{this.setTile2(tile);}
	}
	

//	@Override
//	public Object clone(){
//		try { 
//			Border border = (Border) super.clone();
//			if(getWall() == null){
//				border.setWall(null);
//			}
//			else{border.setWall((Wall) getWall().clone());}
//			
//			return border;
//		} catch (CloneNotSupportedException e) {
//			return null;
//		}
//	}
	
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
		return (getObstruction() == null);
	}
	
	@Override
	public String toString() {
		String t1 = "T1: null";
		String t2 = "T2: null";
		String obstr = "Free Edge";
		if (getObstruction() != null)
			obstr = getObstruction().toString();
		if (getTile1() != null)
			t1 =  "T1:(" + getTile1().getxCoordinate() + getTile1().getyCoordinate()+")";
		if (getTile2() != null)
			t2 =  "T2:(" + getTile2().getxCoordinate() + getTile2().getyCoordinate()+")";
		return t1 + " " + t2 + " Obstr:"+obstr;
	}

}