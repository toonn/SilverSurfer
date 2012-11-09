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
	
	public static float addDegree(float a, float b){
		if(a+b > 360){
			return (a+b-360);
		}
		return a+b;
	}
	
	public static float cosinusRegelToCalculateZijde(float a, float b, float alpha){
		return (float) Math.sqrt(a*a + b*b -2*a*b*Math.cos(Math.toRadians(alpha)));
	}
	
	/**
	 * a is the side opposite to alpha (the angle to calculate)
	 */
	public static float cosineRuleAngle(float a, float b, float c){
		
		return (float) Math.toDegrees((float) Math.acos((- a*a + b*b + c*c)/(2*b*c)));
	}
	
	/**
	 * dit is de methode die telkens de afstand berekent van de robot tot de edge
	 * in de richting waarin hij gekeerd is
	 * als de afstand klein genoeg is worden 'de sensoren' ingeschakeld en wordt
	 * de checkObstruction methode opgeroepen
	 * dus dit wordt opgeroepen in de iftest van rotate en float voor checkForobstructions
	 * wordt opgeroepen
	 */
	public static double calculateDistanceFromPointToEdge(float x, float y, float alpha){
		int i =1;
		float xEdge = x;
		float yEdge = y;
		while (!(xEdge%40<4 || yEdge%40 <4)) {
			xEdge = (float) (x + i* Math.cos(Math.toRadians(alpha)));
			yEdge = (float) (y + i* Math.sin(Math.toRadians(alpha)));
			i++;}
		Point2D xy = new Point2D.Double(x, y);
		Point2D xyEdge = new Point2D.Double(xEdge, yEdge);
		return xy.distance(xyEdge);

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
	public static Point2D calculateWallPoint(Orientation orientation, float x, float y){
		
		float xCoordinate = (float) (Math.floor(x/40)*40);
		float yCoordinate = (float) (Math.floor(y/40)*40);
		
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
		
		Point2D point = new Point2D.Float(xCoordinate, yCoordinate);
		return point;
	}
	
	public static void main(String[] args) {
		Random generator = new Random();
		float a = (float) Math.abs(generator.nextGaussian())*100;
		float b = (float) Math.abs(generator.nextGaussian())*100;
		System.out.println(a);
		System.out.println(b);
		System.out.println(calculateWallPoint(Orientation.NORTH, a, b).getX());
		System.out.println(calculateWallPoint(Orientation.NORTH, a, b).getY());
		System.out.println(calculateWallPoint(Orientation.EAST, a, b).getX());
		System.out.println(calculateWallPoint(Orientation.EAST, a, b).getY());
		System.out.println(calculateWallPoint(Orientation.SOUTH, a, b).getX());
		System.out.println(calculateWallPoint(Orientation.SOUTH, a, b).getY());
		System.out.println(calculateWallPoint(Orientation.WEST, a, b).getX());
		System.out.println(calculateWallPoint(Orientation.WEST, a, b).getY());
	}
	
	
}