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
	
	/**
	 * Deze methode berekent afhankelijk van de orientatie en het punt dat wordt meegegeven
	 * een punt dat wordt gebruikt om de wall te positioneren!
	 * voor noord en west is dit dus het linkerhoekbovenpunt van het tile
	 * voor zuid het rechterbovenhoekpunt en voor zuid het linkeronderhoekpunt
	 * (aan wall wordt altijd het midden van de linker (als wall horizontaal ligt)
	 * of boven(als wall rechtopstaat) breedte meegegeven
	 * (ik heb het midden gepakt en laten meegeven waarna het in wall verwerkt wordt tot
	 * het linkerbovenhoekpunt van de 'rectangle omdat dit makkelijk is om af te ronden
	 * tot op een veelvoud van 40)
	 */
	public static Point2D calculateWallPoint(Orientation orientation, double x, double y){
		
		double xCoordinate = (double) (Math.floor(x/40)*40);
		double yCoordinate = (double) (Math.floor(y/40)*40);
		
		if(orientation.equals(Orientation.NORTH)){
			yCoordinate = yCoordinate -1;
		}
		
		else if(orientation.equals(Orientation.SOUTH)){
			yCoordinate = yCoordinate + 39;
		}
		else if(orientation.equals(Orientation.EAST)) {
			xCoordinate = xCoordinate + 40;
		}
		else{}
		
		Point2D point = new Point2D.Double(xCoordinate, yCoordinate);
		return point;
	}
	
}