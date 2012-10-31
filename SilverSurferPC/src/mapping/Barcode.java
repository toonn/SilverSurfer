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
	@Override
	public String toString() {
		return "Barcode-value: " + getValue();
	}
}
