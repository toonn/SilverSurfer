package simulator;

public class ExtMath1 {
	
	public static float addDegree(float a, float b){
		if(a+b > 360){
			return (a+b-360);
		}
		return a+b;
	}	
}