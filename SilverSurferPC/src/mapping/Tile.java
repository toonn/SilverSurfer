package mapping;

import java.util.ArrayList;
import java.util.HashMap;

public class Tile{
	
	private int xCoordinate = -1000;
	private int yCoordinate = -1000;
	private TileContent content;
	private boolean isMarkedExploreMaze = false;
	private boolean isMarkedShortestPath = false;
	private int manhattanValue;
	private int cost = -1;
	
	public Tile(){
		populateEdges();
	}
	
	/**
	 * return -1000 if this tile is not yet set on a mapgraph
	 */
	public int getxCoordinate() {
		return xCoordinate;
	}
	
	public void setxCoordinate(int xCoordinate) {
		this.xCoordinate = xCoordinate;
	}
	
	/**
	 * return -1000 if this tile is not yet set on a mapgraph
	 */
	public int getyCoordinate() {
		return yCoordinate;
	}
	public void setyCoordinate(int yCoordinate) {
		this.yCoordinate = yCoordinate;
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
	
	//Terminate
	
		/**
		 * Terminate this square.
		 * 
		 * @post	this square is terminated.
		 * 			| new.isTerminated()
		 * @post	This square no longer references any borders as the borders 
		 * 			to which it is attached.
		 * 			|for each direction in borders.keySet():
		 * 			| new.getBorder(direction) == null
		 * @post 	if this square was not already terminated, the borders to which this square was attached,
		 * 			no longer have this square attached to them. If a border doesn't have any square attached
		 * 			to it anymore, that border is terminated.
		 * 			|for each direction in borders.keySet():
		 * 			| if (! isTerminated())
		 * 			|  then (new getBorder(direction)).getSquare1() != this 
		 * 			|	&& (new getBorder(direction)).getSquare2() != this
		 * 			|for each direction in borders.keySet():
		 * 			| if( (new getBorder(direction)).getSquare1() == null &&
		 * 			|     (new getBorder(direction)).getSquare2() == null )
		 * 			|  then( (new getBorder(direction)).isTerminated() )
		 */
		public void terminate(){
			if(! isTerminated()) {
				this.isTerminated = true;
				for(Orientation orientation : Orientation.values()){
					Edge formerEdge = getEdge(orientation);
					setEdge(orientation, null);
					formerEdge.setTileAtRightField(orientation, null);
					formerEdge.terminate();
				}
			}
		}
		
		/**
		 * Check whether this square is terminated.
		 */
		public boolean isTerminated(){
			return this.isTerminated;
		}
		
		/**
		 * Variable registering whether this square is terminated.
		 */
		private boolean isTerminated;
		
		
		// Edges
		
		
		/**
		 * Return the border in the given direction of this square.
		 * 
		 * @param 	direction
		 * 			The direction of the border to be returned.
		 */
		public Edge getEdge(Orientation orientation){
			return edges.get(orientation);
		}
		
		/**
		 * Check whether this square can have the given border as its border.
		 * 
		 * @param 	border
		 * 			the border to check.
		 * @return 	If this square is terminated, true if and only if the given 
		 * 			border is not effective.
		 * 			| if (isTerminated())
		 * 			|  then result == (border == null)
		 * 			Otherwise, true if and only if the given border is effective
		 * 			and not yet terminated.
		 * 			| else result == 
		 * 			| ( (border != null)
		 * 			| && (! border.isTerminated()) )
		 */
		public boolean canHaveAsEdge(Edge edge){
			if(isTerminated()){
				return edge == null;
			}
			else
				return (edge != null) && (!edge.isTerminated()); }
		
		
		/**
		 * Check whether this square can have the given border as one of its borders 
		 * in the given direction.
		 * 
		 * @param 	border
		 * 			The border to check.
		 * @param 	direction
		 * 			The direction to check.
		 * @return	True if this square does already contains the given border at the given direction.
		 * 			| result == this.getBorder(direction).equals(border)
		 * 			False if this square can not have the given border as a border at the given direction.
		 * 			| if (! canHaveAsBorder(border))
		 * 			|   then result == false
		 * 			Otherwise, false if this square can not have the given border at the given direction.
		 * 			| if (! canHaveAsReplacingBorder(direction, border) )
		 * 			|   then result == false
		 * 			Otherwise, false if the given dungeon contains a wall with a door and the given direction
		 * 			is equal to the floor.
		 * 			| result == ! (border.hasWall()&& border.getWall().hasDoor() && direction == Direction.FLOOR)
		 * 			Otherwise, true if and only if the given border is not yet attached to any square.
		 * 			| result == ( border.getSquare1() == null && border.getSquare2() == null )
		 * 			Otherwise, true if and only if there is already one square attached to the given border,
		 * 			that square must be located at the opposite side of the border.
		 * 			| result == 
		 *			|	if((ExtMath.isNumberDirectionEven(direction) && (border.getSquare1() == null) ) 
		 *			|		|| (! ExtMath.isNumberDirectionEven(direction) && (border.getSquare2() == null)) )
		 *			|	then result == ( Direction.getNumberDirection(direction)%3 == border.getNumberPairDirections() )
		 *			Otherwise, false because the last possibility is that the border is
		 *			already attached to two squares.
		 *			| result == false 
		 * 			
		 */
		// dat de border kan replaced worden wanneer deze er al staat is van belang bij de 
		// canNavigateTo-methode, bij !canShareBorders check je of de "overkoepelende" border
		// bij beide kan gezet worden, wat dus in het geval dat de border er al staat, mogelijk is.
		public boolean canHaveAsBorderAtDirection(Edge edge, Orientation orientation){
			
			if(this.getEdge(orientation).equals(edge))
				return true;
			
			if(!canHaveAsEdge(edge))
				return false;
			
			if(edge.howManyTilesAttached() == 0){
				return true;
			}
			else if(edge.howManyTilesAttached() == 1){
				if((ExtMath.isNumberDirectionEven(orientation) && 
						(edge.getTile1() == null) &&
						this.areNeighbours(edge.getTile2())) 
						|| (! ExtMath.isNumberDirectionEven(orientation) && 
								(edge.getTile2() == null)&&
								this.areNeighbours(edge.getTile1()))) 
				return ( edge.getNumberPairDirections() == orientation.getNumberOrientation()%3);
			}
			return false;
			}
		
		/**
		 * Replace the border in the given direction with the given border.
		 * 
		 * @param 	direction
		 * 		  	The direction of the square in which we want to replace the border.
		 * @param 	border
		 * 			the border to which this square must be attached.
		 * @post 	This square is attached to the given border.
		 * 			| new.getBorder(direction) == border
		 * @post 	The given border has this square as one of its squares.
		 * 			| ( ((new border).getSquare1() == this) ||
		 * 			|   ((new border).getSquare2() == this) )
		 * @post 	If this square was attached to some other border in the given direction, that
		 * 			border no longer references this square as one of the squares attached to it.
		 * 			| if ( (getBorder(direction) != null) 
		 * 			| && (getBorder(direction) != border) )
		 * 			|  then (! ( (new getBorder()).getSquare1 == this || (new getBorder()).getSquare2 == this )
		 * @post 	if this square is terminated or does not can have this border at the given direction
		 * 			or if be placed, it exceeds the maximum numbers of doors or has less then one wall,
		 * 			the state of this square will remain the same.
		 */
		//Border ligt steeds ten noord of west van tile1
		public void replaceEdge(Orientation orientation, Edge edge) 
		{
			if(this.getEdge(orientation).equals(edge))
				return;
			if(this.isTerminated()){
				return;}
			if(! this.canHaveAsBorderAtDirection(edge, orientation)){
				return;}
			
			if(this.getEdge(orientation) != null){
				this.getEdge(orientation).setTileAtRightField(orientation, null);
				this.getEdge(orientation).terminate();
			}
			setEdge(orientation, edge);
			edge.setTileAtRightField(orientation, this);
			edge.setNumberPairDirections(orientation.getNumberOrientation()%3);
		}
		
		/**
		 * Set the border to which this square is attached to the given border in the given direction.
		 * 
		 * @param 	direction
		 * 			The direction to set the border.
		 * @param 	border
		 * 			The border to which this square must be attached.
		 * @post	This square is attached to the given border in the given direction.
		 * 			| new.getBorder(direction) == border
		 */
		protected void setEdge(Orientation orientation,Edge edge){
			if(! this.canHaveAsEdge(edge))
				return;
			edges.put(orientation, edge);
		}
		
		/**
		 * Map collecting references to edges associated with the 4 directions of this
		 * tile.
		 * 
		 * @invar 	Each square contains 4 edges. 
		 * 			| for each direction in edges.keySet():
		 *        	| getEdge(direction) != null
		 */
		private HashMap<Orientation, Edge> edges =  new HashMap<Orientation, Edge>();
	
		
		/**
		 * Check whether the given square and this square are neighbours.
		 * Two squares are neighbours if they share a same border.
		 * 
		 * @param 	square
		 * 			the square to check.
		 * @return	True if and only if this square has a neighboursquare in a certain direction  and 
		 * 			if that neighboursquare is equal to the given square.
		 * 			| for each direction in borders.keySet() :
		 * 			|	if(hasNeighbour(direction)
		 * 			| 		then (result == getBorder(direction).getNeighbour(this).equals(square) )
		 */
		public boolean areNeighbours(Tile tile) {
			if(Math.abs(getxCoordinate()- tile.getxCoordinate())==1 && (getyCoordinate() - tile.getyCoordinate()) == 0)
				return true;
			if(Math.abs(getyCoordinate()- tile.getyCoordinate())==1 && (getxCoordinate() - tile.getxCoordinate()) == 0)
				return true;
			else
				return false;
		}		
		
		/**
		 * geeft een arraylist weer met neighbourtiles enkel die waar geen muur tussen staat!
		 * de orientation horende bij de index is als volgt:
		 * 0: north
		 * 1: east
		 * 2: south
		 * 3: west
		 * als er zich geen tile bevindt in die orientation, wordt er een null in de arraylist opgeslagen.
		 */
		public ArrayList getReachableNeighbours(){
			ArrayList neighbours = new ArrayList(4);
			for(Orientation orientation: Orientation.values()){
				if(this.getEdge(orientation).getObstruction() == null){
					neighbours.add(this.getEdge(orientation).getNeighbour(this));
				}
				else neighbours.add(null);
			}
			
			return neighbours;
		}
		
		public ArrayList getAllNeighbours() {
			ArrayList neighbours = new ArrayList(4);
			for(Orientation orientation: Orientation.values()){
					neighbours.add(this.getEdge(orientation).getNeighbour(this));
			}
			
			return neighbours;
		}
		
//		public boolean isReachableNeighbour(Tile tile){
//			return getReachableNeighbours().contains(tile);
//		}
	
	/**
	 * Set the borders in each direction for this square.
	 * Each border is attached to this square in the given direction.
	 * This is a method only used in the constructor.
	 * 
	 * @post 	This square is attached to the new border for the given direction.
	 * 			| for each direction in Direction.values()
	 * 			|	this.getBorderAt(direction) == new Border(this, null)
	 */
	protected void populateEdges(){
		for (Orientation orientation : Orientation.values()) {
					Edge edge = new Edge();
					setEdge(orientation, edge);
					edge.setTileAtRightField(orientation, this);
					edge.setNumberPairDirections(orientation.getNumberOrientation()%3);
		}
	}
	
//velden aangemaakt voor gebruik van algoritmes
	
	/**
	 * 
	 * @return de heuristiek
	 */
	public int getManhattanValue(){
		return manhattanValue;
	}
	
	public void setManhattanValue(int manhattanValue){
		this.manhattanValue = manhattanValue;
	}
	
	/**
	 * Geeft true als hij al gebruikt is in het ExploreMaze-algoritme.
	 */
	public boolean isMarkedExploreMaze(){
		return isMarkedExploreMaze;
	}
	
	
	public void setMarkingExploreMaze (boolean marking){
		this.isMarkedExploreMaze = marking;

	}
	
	
	/**
	 * Geeft true als hij al gebruikt is in het shortestPath-algoritme.
	 */
	public boolean isMarkedShortestPath(){
		return isMarkedShortestPath;
	}
	
	public void setMarkingShortestPath (boolean marking){
		this.isMarkedShortestPath = marking;

	}
	
	public int getCost(){
		return cost;
	}
	
	/**
	 * Deze zet de kost terug op zijn initiele waarde. De tile bevat normaal steeds deze waarde
	 * behalve als ze betrokken is bij een shortestPath algoritme. Dit houdt ook in dat ze maar
	 * in 1 shortestPath-algoritme tegelijkertijd kan gebruikt worden.
	 * Op het einde van het shortestPath algoritme wordt deze methode steeds opgeroepen op elke
	 * tile die erin betrokken was.
	 */
	public void setCostBackToInitiatedValue(){
		this.cost = -1;
	}
	
	/**
	 * Hierin wordt de kost gezet, deze is afhankelijk van het shortestPath algoritme.
	 * De kost wordt enkel gezet, als deze kleiner is dan de kost die er nu al staat of
	 * als ze nog op -1 staat (initiele waarde).
	 */
	public void setCost(int cost){
		if(this.cost == -1 || this.cost > cost){
			this.cost = cost;
		}
	}

	@Override
	public String toString() {
		return "Content: " + getContent() + " Edges -- North: " + getEdge(Orientation.NORTH) + " East: " + getEdge(Orientation.EAST) + " South: "+getEdge(Orientation.SOUTH)+ " West: "+getEdge(Orientation.WEST);
	}

}