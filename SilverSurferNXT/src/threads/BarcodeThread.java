package threads;

import lejos.nxt.*;

public class BarcodeThread extends Thread {

	private String result = "";
	private int distance;
	private LightSensor lightSensor;
	private double lengthCoef;
	private boolean bool = true;

	public BarcodeThread(String str, int distance, LightSensor lightSensor, double lenghtCoef) {
		super(str);
		this.distance = distance;
		this.lightSensor = lightSensor;
		this.lengthCoef = lenghtCoef;
	}
	
	@Override
	public void run() {
		Motor.A.resetTachoCount();
		Motor.B.resetTachoCount();
    	Motor.A.rotateTo(distance, true);
    	Motor.B.rotateTo(distance, true);
    	while(bool)
    		if(Motor.A.getTachoCount() >= distance)
    			return;
    	int tachoCount = Motor.A.getTachoCount();
		Motor.A.rotateTo((int)Math.round(tachoCount + 16*lengthCoef), true);
		Motor.B.rotateTo((int)Math.round(tachoCount + 16*lengthCoef), true);
		for(int i = 1; i <= 6; i++) {
			while(Motor.A.getTachoCount() < (int)Math.round(tachoCount + i*2*lengthCoef));
			if(lightSensor.getLightValue() < 40) 
				result = result + "0";
			else
				result = result + "1";
		}
		while(Motor.A.isMoving());
		changeBool();
		while(bool);
	}
	
	public void changeBool() {
		bool = !bool;
	}
	
	public boolean getBool() {
		return bool;
	}
	
	public int getResult() {
		changeBool();
		Byte byteResult = Byte.valueOf(result, 2);
		System.out.println("Barcode: " + Integer.valueOf(byteResult.intValue()));
		return Integer.valueOf(byteResult.intValue());
	}
}