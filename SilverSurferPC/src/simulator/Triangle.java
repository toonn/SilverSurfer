package simulator;

import java.awt.Polygon;

//Gelijkbenige driehoek met 2 basishoeken A en B,
//een tophoek C en het zwaartepunt Z
//als de driehoek naar boven wijst (alpha = 270 graden) , staat A links tov C en B rechts.
public class Triangle extends Polygon {
	
	private final int length = 20; //lengte van de tophoek tot loodrecht op basisas
	private final float topAngle = 40; //graden van de tophoek
	private float gravityCenterX;
	private float gravityCenterY;
	private float alpha = 0; // Hoeveel je gedraaid bent ten opzichte van de X-as

	Triangle(float x, float y, float alpha){
		super();
		this.gravityCenterX = x;
		this.gravityCenterY = y;
		this.alpha = alpha;
		
		addPoint((int) calculateXCoordinateA(), (int) calculateYCoordinateA());
		addPoint((int) calculateXCoordinateB(), (int) calculateYCoordinateB());
		addPoint((int) calculateXCoordinateC(), (int) calculateYCoordinateC());
		
		System.out.println(super.xpoints[0]);
		System.out.println(super.xpoints[1]);
		System.out.println(super.xpoints[2]);
		System.out.println(super.ypoints[0]);
		System.out.println(super.ypoints[1]);
		System.out.println(super.ypoints[2]);
		
	}
	
	public int getLength() {
		return length;
	}


	public float getTopAngle() {
		return topAngle;
	}

	public float getGravityCenterX() {
		return gravityCenterX;
	}

	public float getGravityCenterY() {
		return gravityCenterY;
	}

	public float getAlpha() {
		return alpha;
	}
	
	private float calculateXCoordinateA(){
		float hoekTenOpzichteVanXas = ExtMath.addDegree(calculateHoekBetweenZCandZA(), getAlpha());
		return (float) (getGravityCenterX() + getLengthZA()*Math.cos(Math.toRadians(hoekTenOpzichteVanXas)));
	}
	
	private float calculateYCoordinateA(){
		float hoekTenOpzichteVanXas = ExtMath.addDegree(calculateHoekBetweenZCandZA(), getAlpha());
		return (float) (getGravityCenterY() + getLengthZA()*Math.sin(Math.toRadians(hoekTenOpzichteVanXas)));
	}
	
	private float calculateXCoordinateB(){
		float hoekTenOpzichteVanXas = ExtMath.addDegree(calculateHoekBetweenZCandZB(), getAlpha());
		return (float) (getGravityCenterX() + getLengthZA()*Math.cos(Math.toRadians(hoekTenOpzichteVanXas)));
	}
	
	private float calculateYCoordinateB(){
		float hoekTenOpzichteVanXas = ExtMath.addDegree(calculateHoekBetweenZCandZB(), getAlpha());
		return (float) (getGravityCenterY() + getLengthZA()*Math.sin(Math.toRadians(hoekTenOpzichteVanXas)));
	}
	
	private float calculateXCoordinateC(){
		return (float) (getGravityCenterX() + getLengthZC()*Math.cos(Math.toRadians(getAlpha())));	
	}

	private float calculateYCoordinateC(){
		return (float) (getGravityCenterY() + getLengthZC()*Math.sin(Math.toRadians(getAlpha())));	
	}
	
	private float calculateHoekBetweenZCandZB(){
		return ExtMath.cosineRuleAngle(getLengthAC(), 
									   getLengthZA(),
									   getLengthZC());
	}
	
	private float calculateHoekBetweenZCandZA(){
		return 360 - calculateHoekBetweenZCandZB();
	}
	
	/**
	 * de lengte van ZA is gelijk aan de lengte van ZB
	 * @return
	 */
	private float getLengthZA(){
		return ExtMath.cosinusRegelToCalculateZijde(getLengthZC(),
													getLengthAC(),
													getTopAngle()/2);
	}
	
	private float getLengthZC(){
	return (float) getLength()*2/3;
	}
	
	/**
	 * lengte AC is gelijk aan lengte BC (de gelijke benen van de driehoek)
	 * @return
	 */
	private float getLengthAC(){
		return (float) ((float) getLength()/(Math.cos((float) Math.toRadians(getTopAngle()/2))));
	}


	
	
	
	
}
