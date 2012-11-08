package mapping;

/**
 * A collection of mathematical functions not offered by the
 * standard class Math in the Java API.
 */
public class ExtMath {

	
	/**
	 * Check whether the number corresponding with the given direction is an even number.
	 * 
	 * @param 	direction
	 * 			The direction to be checked if its corresponding number is even or not.
	 * @return	True if and only if the number corresponding with the given direction is even.
	 * 			| result ==
	 * 			|   (Direction.getNumberDirection(direction)%2 == 0)
	 */
	public static boolean isNumberDirectionEven(Orientation orientation){
		return (Orientation.getNumberOrientation(orientation)%2 == 0);
	}

}