package mapping;

public class StartBase extends TileContent{

	private Orientation ori;
	
	protected StartBase(Tile tile, int value, Orientation ori) {
		super(tile, value);
		this.ori = ori;
	}

	@Override
	public int getColorValue(double x, double y) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public Orientation getOrientation() {
		return ori;
	}

}
