/**
 * 
 */
package mapping;

/**
 * @author Nele
 *
 */
public class Seesaw extends TileContent {

	/**
	 * Creates an object with as value 'value'.
	 */
	public Seesaw(final Tile tile, final int value) {
		super(tile, value);
	}

	/**
	 * Flips the seesaw. If the current seesaw-edge is up, it is set down and vice versa.
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

	public boolean isUp() {
		for(Edge edge: tile.getEdges()) 
			if(edge.getObstruction() == Obstruction.SEESAW_UP)
				return true;
		return false;
	}
}