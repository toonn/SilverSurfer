/**
 * vooral in deze klasse kan het da er veel fouten zitten
 * me afrondingen en al , veel rekenen blabla,
 * de enige methodes die van toepassing zijn bij het tekenen en detecteren van de muren
 * zijn calculateDistanceFromPointToEdge en calculateWallPoint
 * de rest staan er al van demo 1 dus meot je dnek ik niet meer bekijken
 */

package simulator;

import java.awt.geom.Point2D;
import java.util.Random;
import mapping.Orientation;

public class ExtMath {
	
	public static double addDegree(double a, double b){
		if(a+b > 360){
			return (a+b-360);
		}
		if(a+b < 0){
			return (a+b+360);
		}
		return a+b;
	}
	
	public static double cosinusRegelToCalculateZijde(double a, double b, double alpha){
		return (double) Math.sqrt(a*a + b*b -2*a*b*Math.cos(Math.toRadians(alpha)));
	}
	
	/**
	 * a is the side opposite to alpha (the angle to calculate)
	 */
	public static double cosineRuleAngle(double a, double b, double c){
		
		return (double) Math.toDegrees((double) Math.acos((- a*a + b*b + c*c)/(2*b*c)));
	}
	
}