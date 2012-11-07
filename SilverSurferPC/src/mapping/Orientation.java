package mapping;

public enum Orientation {
	NORTH, EAST, WEST, SOUTH;
	
	private static float getMaxRoundingError()
	{
		return (float) 0.4;
	}

	public static Orientation calculateOrientation(float x, float y, float alpha)
	{
		float xTemp = x;
		float yTemp = y;
		int i = 1;

		while(!(((xTemp%40) > 40-getMaxRoundingError() || (xTemp%40) < getMaxRoundingError())
				|| ((yTemp%40) > 40-getMaxRoundingError() || (yTemp%40) < getMaxRoundingError())))
		{
			xTemp = (float) (x + i* Math.cos(Math.toRadians(alpha)));
			yTemp = (float) (y + i* Math.sin(Math.toRadians(alpha)));
			i++;
		}

		if((xTemp%40) > 40-getMaxRoundingError() || (xTemp%40) < getMaxRoundingError())
		{
			if(alpha <= 45 || alpha >= 315)
			{
				return EAST;
			}
			else
			{
				return WEST;
			}
		}
		//if((yTemp%40) > 40-this.getMaxRoundingError() || (yTemp%40) < this.getMaxRoundingError())
		else
		{
			if(alpha <= 180)
			{
				return SOUTH;
			}
			else
			{
				return NORTH;
			}
		}
	}
	
	/**
	 * Return an array with 3 long values for each direction. 
	 * This array can be added at a dimension to find
	 * the dimension of the neighbour-square in that direction.
	 * 
	 * @param 	direction
	 * 			The direction to get the array of.
	 * @return	The array of the given direction.
	 * @throws  IllegalArgumentException 
	 * 			The direction is not effective.
	 * 			| direction == null
	 */
	public static int[] getArrayToFindNeighbourAbsolute(Orientation orientation){
	
		if(orientation == null) {
			throw new IllegalArgumentException();}
		
		switch(orientation){
	case NORTH:
			int[] north = new int[2];
			north[0] = 0;
			north[1] = -1;
			return north;
	case SOUTH:
			int[] south = new int[2];
			south[0] = 0;
			south[1] = 1;
			return south;
	case EAST:
			int[] east = new int[2];
			east[0] = 1;
			east[1] = 0;
			return east;
	case WEST:
		int[] west = new int[2];
			west[0] = -1;
			west[1] = 0;
			return west;
		// This line will never be reached, each valid direction has a return statement.
			default:
				return new int[0];
		}
	}
	
	public static int[] getArrayToFindNeighbourRelative(Orientation orientation){
		
		if(orientation == null) {
			throw new IllegalArgumentException("orientatie null");}
		
		switch(orientation){
	case NORTH:
			int[] north = new int[2];
			north[0] = -1;
			north[1] = 0;
			return north;
	case SOUTH:
			int[] south = new int[2];
			south[0] = 1;
			south[1] = 0;
			return south;
	case EAST:
			int[] east = new int[2];
			east[0] = 0;
			east[1] = 1;
			return east;
	case WEST:
		int[] west = new int[2];
			west[0] = 0;
			west[1] = -1;
			return west;
		// This line will never be reached, each valid direction has a return statement.
			default:
				return new int[0];
		}
	}
	
	public static Orientation getOrientationOfArray(int[] array){
		if(array[0]==1)
			return EAST;
		else if(array[0]==-1)
			return WEST;
		else if(array[1]==-1)
			return NORTH;
		else
			return SOUTH;
	}
	
	/**
	 *  Return the number of the given direction.
	 *  
	 * 	Each pair of opposite directions has received an even and an odd number.
	 * 	Like that, it separates the directions in two groups:
	 *	The even numbers are North, East and Ceiling. 
	 * 	The corresponding odd numbers are the opposites, respectively South, West and Floor.
	 * 
	 *  Each pair has the same residual when you divide the number according to the direction by 3.
	 *  Like that, it separates the directions in three groups, 
	 *  each group contains one pair :
	 *  
	 *  The directions with residual 0 are North and South,
	 *  the directions with residual 1 are West and East,
	 *  the directions with residual 2 are Floor and Ceiling.
	 *  
	 * 
	 * @param   direction
	 * 			the direction to get the number of. 
	 * @return  the number of the given direction.
	 * @throws  IllegalArgumentException 
	 * 			The given direction is not effective.
	 * 			| direction == null
	 */
	public static int getNumberOrientation(Orientation orientation){
		
		if(orientation == null) {
		throw new IllegalArgumentException();}
		
		switch(orientation){
		case NORTH:
			return 0;
		case SOUTH:
			return 3;
		case EAST:
			return 4;
		case WEST:
			return 1;
		// This line will never be reached, each valid direction has a return statement.
		default:
			return -1;
		}
	}
	
	public static Orientation getOppositeOrientation(Orientation orientation){
		switch(orientation){
		case NORTH:
			return SOUTH;
		case SOUTH:
			return NORTH;
		case EAST:
			return WEST;
		default:
			return EAST;
		}
	}
	
	public static Orientation getOtherOrientationCorner(Orientation orientation){
		switch(orientation){
		case NORTH:
			return WEST;
		case SOUTH:
			return EAST;
		case EAST:
			return NORTH;
		default:
			return SOUTH;
		}
	}
	
	public static Orientation switchStringToOrientation(String string){
		if(string.equals("N"))
			return NORTH;
		else if(string.equals("S"))
			return SOUTH;
		else if(string.equals("E"))
			return EAST;
		else
			return WEST;
	}
	
}