package mapping;

public enum Obstruction {
	WALL
	{
		public boolean isPassible()
		{
			return false;
		}
	},
	WHITE_LINE
	{
		public boolean isPassible()
		{
			return false;
		}
	};
	
	public abstract boolean isPassible();
}
