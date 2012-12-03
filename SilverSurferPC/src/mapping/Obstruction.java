package mapping;

public enum Obstruction {
	WALL
	{
		@Override
		public boolean isPassible()
		{
			return false;
		}
	},
	WHITE_LINE
	{
		@Override
		public boolean isPassible()
		{
			return true;
		}
	};
	
	public boolean isPassible()
	{
		return true;
	}
}
