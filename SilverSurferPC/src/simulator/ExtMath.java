/**
 * vooral in deze klasse kan het da er veel fouten zitten
 * me afrondingen en al , veel rekenen blabla,
 * de enige methodes die van toepassing zijn bij het tekenen en detecteren van de muren
 * zijn calculateDistanceFromPointToEdge en calculateWallPoint
 * de rest staan er al van demo 1 dus meot je dnek ik niet meer bekijken
 */

package simulator;

public class ExtMath {

    public static double addDegree(final double a, final double b) {
        if (a + b > 360) {
            return (a + b - 360);
        }
        if (a + b < 0) {
            return (a + b + 360);
        }
        return a + b;
    }

    /**
     * a is the side opposite to alpha (the angle to calculate)
     */
    public static double cosineRuleAngle(final double a, final double b,
            final double c) {

        return Math
                .toDegrees(Math.acos((-a * a + b * b + c * c) / (2 * b * c)));
    }

    public static double cosinusRegelToCalculateZijde(final double a,
            final double b, final double alpha) {
        return Math.sqrt(a * a + b * b - 2 * a * b
                * Math.cos(Math.toRadians(alpha)));
    }

}