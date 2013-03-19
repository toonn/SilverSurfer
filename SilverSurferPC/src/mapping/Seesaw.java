/**
 * 
 */
package mapping;

/**
 * @author Nele
 *
 */
public class Seesaw extends TileContent {
	
	private Orientation ori;

	/**
	 * Creates an Seesaw with a direction.
	 */
	public Seesaw(final Tile tile, final Orientation ori) {
		this(tile, ori, 0);
	}
	
	public Seesaw(final Tile tile, final Orientation ori, int value) {
		super(tile, value);
		this.ori = ori;
	}

	/**
	 * Flips the Seesaw. If the current Seesaw-edge is up, it is set down and vice versa.
	 */
	public void flipSeesaw() {
		for(Edge edge: tile.getEdges()) {
			if(edge.getObstruction() == Obstruction.SEESAW_DOWN)
				edge.setObstruction(Obstruction.SEESAW_UP);
			else if(edge.getObstruction() == Obstruction.SEESAW_UP)
				edge.setObstruction(Obstruction.SEESAW_DOWN);
		}
	}

	/* (non-Javadoc)
	 * @see mapping.TileContent#getColorValue(double, double)
	 */
	@Override
	public int getColorValue(double x, double y) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public Orientation getOrientation() {
		return ori;
	}
	
	

	public boolean isUp() {
		for(Edge edge: tile.getEdges()) 
			if(edge.getObstruction() == Obstruction.SEESAW_UP)
				return true;
		return false;
	}
}
