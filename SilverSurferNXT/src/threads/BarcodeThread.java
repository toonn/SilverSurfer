package threads;

import lejos.nxt.*;

public class BarcodeThread extends Thread {

	private String result = "";
	private LightSensor lightSensor;
	private int blackLineTreshold;
	private double lengthCoef;
	private boolean bool = true;
	private boolean found = false;
	private int command;
	
	public BarcodeThread(String str, LightSensor lightSensor, double lenghtCoef, int command, int blackLineTreshold) {
		super(str);
		this.lightSensor = lightSensor;
		this.lengthCoef = lenghtCoef;
		this.command = command;
		this.blackLineTreshold = blackLineTreshold;
	}
	
	@Override
	public void run() {
		if(command == 0) {
			while(bool) {
				if(lightSensor.getLightValue() < blackLineTreshold)
					found = true;
			}
		}
		else {
			Motor.A.resetTachoCount();
			Motor.B.resetTachoCount();
			Motor.A.rotateTo((int)Math.round(17*lengthCoef), true);
			Motor.B.rotateTo((int)Math.round(17*lengthCoef), true);
			for(int i = 1; i <= 6; i++) {
				while(Motor.A.getTachoCount() < (int)Math.round((i*2)*lengthCoef));
				if(lightSensor.getLightValue() < blackLineTreshold) 
					result = result + "0";
				else
					result = result + "1";
			}
			while(Motor.A.isMoving());
			changeBool();
			while(bool);
		}
	}
	
	public void changeBool() {
		bool = !bool;
	}
	
	public boolean getBool() {
		return bool;
	}
	
	public boolean getFound() {
		return found;
	}
	
	public int getResult() {
		changeBool();
		Byte byteResult = Byte.valueOf(result, 2);
		System.out.println("Barcode: " + Integer.valueOf(byteResult.intValue()));
		return Integer.valueOf(byteResult.intValue());
	}
}