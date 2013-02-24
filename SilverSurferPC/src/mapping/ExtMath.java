package mapping;

/**
 * A collection of mathematical functions not offered by the standard class Math
 * in the Java API.
 */
public class ExtMath {

    private static boolean lastTurnRight = false;

    public static double getSmallestAngle(double angle) {
        if (angle < -180) {
            angle = angle + 360;
        } else if (angle > 180) {
            angle = angle - 360;
        } else if (lastTurnRight && angle == 180) {
            angle = -angle;
        } else if (!lastTurnRight && angle == -180) {
            angle = -angle;
        }
        if (angle >= 0) {
            lastTurnRight = true;
        } else {
            lastTurnRight = false;
        }
        return angle;
    }

    /**
     * Check whether the number corresponding with the given direction is an
     * even number.
     * 
     * @param direction
     *            The direction to be checked if its corresponding number is
     *            even or not.
     * @return True if and only if the number corresponding with the given
     *         direction is even. | result == |
     *         (Direction.getNumberDirection(direction)%2 == 0)
     */
    public static boolean isNumberDirectionEven(final Orientation orientation) {
        return (orientation.getNumberOrientation() % 2 == 0);
    }

}