package mapping;

public enum Orientation {
	NORTH
	{
		@Override
		public final int[] getArrayToFindNeighbourAbsolute()
		{
			int[] north = {0, -1};
			return north;
		}
		
		@Override
		public final int[] getArrayToFindNeighbourRelative()
		{
			int[] north = {-1, 0};
			return north;
		}
		
		@Override
		public final int[] getOtherPointLine()
		{
			int[] north = {40,0};
			return north;
		}
		
		@Override
		public final Orientation getOppositeOrientation()
		{
			return SOUTH;
		}
		
		@Override
		public final Orientation getOtherOrientationCorner()
		{
			return WEST;
		}
		
		@Override
		public final int getRightAngle()
		{
			return 270;
		}
		
		@Override
		public final int getNumberOrientation()
		{
			return 0;
		}
	},
	EAST
	{
		@Override
		public final int[] getArrayToFindNeighbourAbsolute()
		{
			int[] east = {1, 0};
			return east;

		}
		
		@Override
		public final int[] getArrayToFindNeighbourRelative()
		{
			int[] east = {0, 1};
			return east;
		}
		
		
		@Override
		public final int[] getOtherPointLine()
		{
			int[] east = {0,40};
			return east;
		}
		
		@Override
		public final Orientation getOppositeOrientation()
		{
			return WEST;
		}
		
		@Override
		public final Orientation getOtherOrientationCorner()
		{
			return NORTH;
		}
		
		@Override
		public final int getRightAngle()
		{
			return 0;
		}
		
		@Override
		public final int getNumberOrientation()
		{
			return 4;
		}
	},
	SOUTH
	{
		@Override
		public final int[] getArrayToFindNeighbourAbsolute()
		{
			int[] south = {0, 1};
			return south;
		}
		
		@Override
		public final int[] getArrayToFindNeighbourRelative()
		{
			int[] south = {1, 0};
			return south;
		}
		
		@Override
		public final int[] getOtherPointLine()
		{
			int[] south = {40,0};
			return south;
		}
		
		@Override
		public final Orientation getOppositeOrientation()
		{
			return NORTH;
		}
		
		@Override
		public final Orientation getOtherOrientationCorner()
		{
			return EAST;
		}
		
		@Override
		public final int getRightAngle()
		{
			return 90;
		}
		
		@Override
		public final int getNumberOrientation()
		{
			return 3;
		}
	},
	WEST
	{
		@Override
		public final int[] getArrayToFindNeighbourAbsolute()
		{
			int[] west = {-1, 0};
			return west;
		}
		
		@Override
		public final int[] getArrayToFindNeighbourRelative()
		{
			int[] west = {0,-1};
			return west;
		}
		
		@Override
		public final int[] getOtherPointLine()
		{
			int[] west = {0,40};
			return west;
		}
		
		@Override
		public final Orientation getOppositeOrientation()
		{
			return EAST;
		}
		
		@Override
		public final Orientation getOtherOrientationCorner()
		{
			return SOUTH;
		}
		
		@Override
		public final int getRightAngle()
		{
			return 180;
		}
		
		@Override
		public final int getNumberOrientation()
		{
			return 1;
		}
	};

	/**
	 * This methode returns an array containing an absolute x- and y-coordinate for the given orientation.
	 * The implementation is orientation specific and is typed above.
	 * 
	 * er is dus een verschil met getArrayToFindNeighbourRelative die hier onder staat
	 * ook weer door het verschil in coordinatensysteem zie uitleg simulationpilot bovenaan
	 */
	public int[] getArrayToFindNeighbourAbsolute()
	{
		// This line will never be reached, each valid direction has a return statement.
		return new int[0];
	}
	
	/**
	 * This methode returns an array containing a relative x- and y-coordinate for the given orientation.
	 * The implementation is orientation specific and is typed above.
	 * 
	 * er is dus een verschil met getArrayToFindNeighbourAbsoltue die hier onder staat
	 * ook weer door het verschil in coordinatensysteem zie uitleg simulationpilot bovenaan
	 */
	public int[] getArrayToFindNeighbourRelative()
	{
		// implementation is orientation dependent
		// This line will never be reached, each valid direction has a return statement.
		return new int[0];
	}
	
	public int[] getOtherPointLine()
	{
		// implementation is orientation dependent
		// This line will never be reached, each valid direction has a return statement.
		return new int[0];
	}
	
	public Orientation getOppositeOrientation()
	{
		// implementation is orientation dependent
		return null;
	}
	
	/**
	 * enkel gebruikt in mapreader , doet verder niet ter zake
	 */
	public Orientation getOtherOrientationCorner()
	{
		// implementation is orientation dependent
		return null;
	}
	
	/**
	 * enkel gebruikt in whiteAllignAlgoritme , doet verder niet ter zake
	 */
	public int getRightAngle()
	{
		// implementation is orientation dependent
		// This line will never be reached, each valid direction has a return statement.
		return -1;
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
	public int getNumberOrientation()
	{
		// implementation is orientation dependent
		return -1;
	}

	private static double getMaxRoundingError()
	{
		// implementation is orientation dependent
		return (double) 0.4;
	}

	/**
	 * Hier wordt de richting van de edge die ge eerst snijdt (dus NORTH edge, SOUTH edge,...)
	 * berekend adhv uw coordinaten waar de robot zich bevindt en hoek waaronder die staat.
	 * bij dit soort methodes is kans op tel of afrondingsfouten groot!!
	 * TODO normaal wel in orde maar zou kunnen dat het toch soms foute resultaten geeft
	 */
	public static Orientation calculateOrientation(double x, double y, double alpha)
	{
		double xTemp = x;
		double yTemp = y;
		int i = 1;

		while(!(((xTemp%40) > 40-getMaxRoundingError() || (xTemp%40) < getMaxRoundingError())
				|| ((yTemp%40) > 40-getMaxRoundingError() || (yTemp%40) < getMaxRoundingError())))
		{
			xTemp = (double) (x + i* Math.cos(Math.toRadians(alpha)));
			yTemp = (double) (y + i* Math.sin(Math.toRadians(alpha)));
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
}