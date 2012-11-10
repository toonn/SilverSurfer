package mapping;

public enum Orientation {
	NORTH, EAST, WEST, SOUTH;
	
	private static float getMaxRoundingError()
	{
		return (float) 0.4;
	}

	/**
	 * Hier wordt de richting van de edge die ge eerst snijdt (dus NORTH edge, SOUTH edge,...)
	 * berekend adhv uw coordinaten waar de robot zich bevindt en hoek waaronder die staat.
	 * bij dit soort methodes is kans op tel of afrondingsfouten groot!!
	 * TODO normaal wel in orde maar zou kunnen dat het toch soms foute resultaten geeft
	 */
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

		if((yTemp%40) > 40-getMaxRoundingError() || (yTemp%40) < getMaxRoundingError())
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
		//if((yTemp%40) > 40-this.getMaxRoundingError() || (yTemp%40) < this.getMaxRoundingError())
		else
		{
			if(alpha >= 270 || alpha < 90)
			{
				return EAST;
			}
			else
			{
				return WEST;
			}
		}
	}
	
	/**
	 * er is dus een verschil met getArrayToFindNeighbourRelative die hier onder staat
	 * ook weer door het verschil in coordinatensysteem zie uitleg simulationpilot bovenaan
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
	
	/**
	 * hulpmethode die verder niet gebruikt wordt..
	 * is in de setTileMethode van nut
	 */
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
	
	/**
	 * enkel gebruikt in mapreader , doet verder niet ter zake
	 */
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
	
	/**
	 *  dit wordt enkel gebruikt in tile en edges doet voor de rest ook niet echt
	 *  ter zake
	 *  
	 *  
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

	/**
	 * enkel gebruikt in mapreader , doet verder niet ter zake
	 */
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
	
	/**
	 * enkel gebruikt in whiteAllignAlgoritme , doet verder niet ter zake
	 */
	public static int getRightAngle(Orientation orientation){

		if(orientation == null) {
		throw new IllegalArgumentException();}
		
		switch(orientation){
		case NORTH:
			return 270;
		case SOUTH:
			return 90;
		case EAST:
			return 0;
		case WEST:
			return 180;
		// This line will never be reached, each valid direction has a return statement.
		default:
			return -1;
		}
	}
	
	public static int[] getOtherPointLine(Orientation orientation){

		switch(orientation){
	case NORTH:
			int[] north = new int[2];
			north[0] = 40;
			north[1] = 0;
			return north;
	case SOUTH:
			int[] south = new int[2];
			south[0] = 40;
			south[1] = 0;
			return south;
	case EAST:
			int[] east = new int[2];
			east[0] = 0;
			east[1] = 40;
			return east;
	case WEST:
		int[] west = new int[2];
			west[0] = 0;
			west[1] = 40;
			return west;
		// This line will never be reached, each valid direction has a return statement.
			default:
				return new int[0];
		}}
	
}