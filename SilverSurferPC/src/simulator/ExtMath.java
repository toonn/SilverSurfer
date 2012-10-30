package simulator;

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
	
	public static void main(String[] args) {
		
	}
	
	
}