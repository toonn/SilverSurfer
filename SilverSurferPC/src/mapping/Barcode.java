package mapping;

public class Barcode extends TileContent{

	private int value;
	
	/**
	 * Creates a barcode with as value 'value'.
	 */
	public Barcode(int value){
		setValue(value);
	}
	
	/**
	 * Creates a default, empty barcode. 
	 * (value == 0).
	 */
	public Barcode(){
		this(0);
	}
	
	/**
	 * Gets the decimal value this barcode represents. Should be used to perform the right action.
	 */
	public int getValue() {
		return value;
	}
	
	public void setValue(int value) {
		this.value = value;
	}
	
	/**
	 * Gives the color of the barcode on the given x-y-coordinate

	 * @param x relative to the tile!
	 * @param y relative to the tile!
	 * @return	0, when standing on a black part of the barcode
	 * 			1, when standing on a white part of the barcode
	 * 			something else, when standing next to the barcode but on the same tile (brown underground)
	 */
	public int getColorValue(double x, double y)
	{
		// to be implemented!
		return 0;
	}

	
	@Override
	public String toString() {
		return "Barcode-value: " + getValue();
	}
}
