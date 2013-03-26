package mapping;

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
}