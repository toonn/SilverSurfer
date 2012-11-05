package mapping;

public enum Orientation {
	NORTH
	{
		public Orientation getOppositeOrientation()
		{
			return Orientation.SOUTH;
		}
		
		@Override
		public int getRelativeYCoordinate()
		{
			return -1;
		}
	},
	EAST
	{
		public Orientation getOppositeOrientation()
		{
			return Orientation.WEST;
		}
		
		@Override
		public int getRelativeXCoordinate()
		{
			return 1;
		}
	},
	SOUTH
	{
		public Orientation getOppositeOrientation()
		{
			return Orientation.NORTH;
		}
		
		@Override
		public int getRelativeYCoordinate()
		{
			return 1;
		}
	},
	WEST{
		public Orientation getOppositeOrientation()
		{
			return Orientation.EAST;
		}

		@Override
		public int getRelativeXCoordinate()
		{
			return -1;
		}
	};
	
	
	public abstract Orientation getOppositeOrientation();
	
	public int getRelativeXCoordinate()
	{
		return 0;
	}
	public int getRelativeYCoordinate()
	{
		return 0;
	}
	
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
	
}