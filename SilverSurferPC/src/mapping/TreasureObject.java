/**
 * 
 */
package mapping;

/**
 * @author Nele
 *
 */
public class TreasureObject extends TileContent {

	/**
     * Creates an object with as value 'value'.
     */
    public TreasureObject(final Tile tile, final int value) {
        super(tile, value);
    }
	
	/* (non-Javadoc)
	 * @see mapping.TileContent#getColorValue(double, double)
	 */
	@Override
	public int getColorValue(double x, double y) {
		// TODO Auto-generated method stub
		return 0;
	}

}
