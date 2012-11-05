package mapping;

public enum Orientation {
	NORTH, EAST, SOUTH, WEST;
	
	
	private static float getMaxRoundingError()
	{
		return (float) 0.4;
	}

	public static Orientation calculateOrientation(float x, float y, float alpha)
	{
		float xTemp = x;
		float yTemp = y;
		int i = 1;

		while(((xTemp%40) > 40-getMaxRoundingError() || (xTemp%40) < getMaxRoundingError())
				|| ((yTemp%40) > 40-getMaxRoundingError() || (yTemp%40) < getMaxRoundingError()))
		{
			xTemp = (float) (x + i* Math.cos(Math.toRadians(alpha)));
			yTemp = (float) (y + i* Math.sin(Math.toRadians(alpha)));
			i++;
		}

		if((xTemp%40) > 40-getMaxRoundingError() || (xTemp%40) < getMaxRoundingError())
		{
			if(alpha < 45 || alpha > 315)
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
			if(alpha < 180)
			{
				return SOUTH;
			}
			else
			{
				return NORTH;
			}
		}
	}
	
}