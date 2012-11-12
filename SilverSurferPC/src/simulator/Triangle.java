package simulator;

import java.awt.Polygon;

//Gelijkbenige driehoek met 2 basishoeken A en B,
//een tophoek C en het zwaartepunt Z
//als de driehoek naar boven wijst (alpha = 270 graden) , staat A links tov C en B rechts.
public class Triangle extends Polygon {
	
	private final int length = 15; //lengte van de tophoek tot loodrecht op basisas
	private final double topAngle = 40; //graden van de tophoek
	private double gravityCenterX;
	private double gravityCenterY;
	private double alpha = 0; // Hoeveel je gedraaid bent ten opzichte van de X-as

	Triangle(double x, double y, double alpha){
		super();
		this.gravityCenterX = x;
		this.gravityCenterY = y;
		this.alpha = alpha;
		
		this.addPoint((int) this.calculateXCoordinateA(), (int) this.calculateYCoordinateA());
		this.addPoint((int) this.getGravityCenterX(), (int) this.getGravityCenterY());
		this.addPoint((int) this.calculateXCoordinateB(), (int) this.calculateYCoordinateB());
		this.addPoint((int) this.calculateXCoordinateC(), (int) this.calculateYCoordinateC());
	}
	
	public int getLength() {
		return length;
	}

	public double getTopAngle() {
		return topAngle;
	}

	public double getGravityCenterX() {
		return gravityCenterX;
	}
	
	public void setGravityCenterX(double gravityCenterX){
		this.gravityCenterX = gravityCenterX;
		reset();
		this.addPoint((int) this.calculateXCoordinateA(), (int) this.calculateYCoordinateA());
		this.addPoint((int) this.getGravityCenterX(), (int) this.getGravityCenterY());
		this.addPoint((int) this.calculateXCoordinateB(), (int) this.calculateYCoordinateB());
		this.addPoint((int) this.calculateXCoordinateC(), (int) this.calculateYCoordinateC());
		
	}

	public double getGravityCenterY() {
		return gravityCenterY;
	}
	
	public void setGravityCenterY(double gravityCenterY){
		this.gravityCenterY = gravityCenterY;
		
		reset();
		this.addPoint((int) this.calculateXCoordinateA(), (int) this.calculateYCoordinateA());
		this.addPoint((int) this.getGravityCenterX(), (int) this.getGravityCenterY());
		this.addPoint((int) this.calculateXCoordinateB(), (int) this.calculateYCoordinateB());
		this.addPoint((int) this.calculateXCoordinateC(), (int) this.calculateYCoordinateC());
	}

	public double getAlpha() {
		return alpha;
	}
	
	public void setAlpha(double alpha){
		this.alpha = alpha;
		
		reset();
		this.addPoint((int) this.calculateXCoordinateA(), (int) this.calculateYCoordinateA());
		this.addPoint((int) this.getGravityCenterX(), (int) this.getGravityCenterY());
		this.addPoint((int) this.calculateXCoordinateB(), (int) this.calculateYCoordinateB());
		this.addPoint((int) this.calculateXCoordinateC(), (int) this.calculateYCoordinateC());
	}
	
	private double calculateXCoordinateA(){
		double hoekTenOpzichteVanXas = ExtMath.addDegree(calculateHoekBetweenZCandZA(), getAlpha());
		return (double) (getGravityCenterX() + getLengthZA()*Math.cos(Math.toRadians(hoekTenOpzichteVanXas)));
	}
	
	private double calculateYCoordinateA(){
		double hoekTenOpzichteVanXas = ExtMath.addDegree(calculateHoekBetweenZCandZA(), getAlpha());
		return (double) (getGravityCenterY() + getLengthZA()*Math.sin(Math.toRadians(hoekTenOpzichteVanXas)));
	}
	
	private double calculateXCoordinateB(){
		double hoekTenOpzichteVanXas = ExtMath.addDegree(calculateHoekBetweenZCandZB(), getAlpha());
		return (double) (getGravityCenterX() + getLengthZA()*Math.cos(Math.toRadians(hoekTenOpzichteVanXas)));
	}
	
	private double calculateYCoordinateB(){
		double hoekTenOpzichteVanXas = ExtMath.addDegree(calculateHoekBetweenZCandZB(), getAlpha());
		return (double) (getGravityCenterY() + getLengthZA()*Math.sin(Math.toRadians(hoekTenOpzichteVanXas)));
	}
	
	private double calculateXCoordinateC(){
		return (double) (getGravityCenterX() + getLengthZC()*Math.cos(Math.toRadians(getAlpha())));	
	}

	private double calculateYCoordinateC(){
		return (double) (getGravityCenterY() + getLengthZC()*Math.sin(Math.toRadians(getAlpha())));	
	}
	
	private double calculateHoekBetweenZCandZB(){
		return ExtMath.cosineRuleAngle(getLengthAC(), 
									   getLengthZA(),
									   getLengthZC());
	}
	
	private double calculateHoekBetweenZCandZA(){
		return 360 - calculateHoekBetweenZCandZB();
	}
	
	/**
	 * de lengte van ZA is gelijk aan de lengte van ZB
	 * @return
	 */
	private double getLengthZA(){
		return ExtMath.cosinusRegelToCalculateZijde(getLengthZC(),
													getLengthAC(),
													getTopAngle()/2);
	}
	
	private double getLengthZC(){
	return (double) getLength()*2/3;
	}
	
	/**
	 * lengte AC is gelijk aan lengte BC (de gelijke benen van de driehoek)
	 * @return
	 */
	private double getLengthAC(){
		return (double) ((double) getLength()/(Math.cos((double) Math.toRadians(getTopAngle()/2))));
	}
}
