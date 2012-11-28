package simulator;

import java.awt.Polygon;

//Gelijkbenige driehoek met 2 basishoeken A en B,
//een tophoek C en het zwaartepunt Z
//als de driehoek naar boven wijst (alpha = 270 graden) , staat A links tov C en B rechts.
public class Triangle extends Polygon {
	
	private final int length = 22; //lengte van de tophoek tot loodrecht op basisas
	private final double topAngle = 42; //graden van de tophoek
	private double gravityCenterX;
	private double gravityCenterY;
	private double alpha = 0; // Hoeveel je gedraaid bent ten opzichte van de X-as

	Triangle(double x, double y, double alpha){
		super();
		this.gravityCenterX = x;
		this.gravityCenterY = y;
		this.alpha = alpha;
		
		resetTriangle();
	}

	private void resetTriangle() {
		this.addPoint((int) this.calculateXCoordinateA(), (int) this.calculateYCoordinateA());
		this.addPoint((int) this.getArrowCenterX(), (int) this.getArrowCenterY());
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
		resetTriangle();
		
	}

	public double getGravityCenterY() {
		return gravityCenterY;
	}

	public void setGravityCenterY(double gravityCenterY){
		this.gravityCenterY = gravityCenterY;
		
		reset();
		resetTriangle();
	}

	public double getArrowCenterX() {
		return this.getGravityCenterX() - 5.5*Math.cos(Math.toRadians(this.getAlpha()));
	}
	
	public double getArrowCenterY(){
		return this.getGravityCenterY() - 5.5*Math.sin(Math.toRadians(this.getAlpha()));
	}

	public double getAlpha() {
		return alpha;
	}
	
	public void setAlpha(double alpha){
		this.alpha = alpha;
		
		reset();
		resetTriangle();
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
