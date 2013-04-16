package threads;

import lejos.nxt.*;

public class BarcodeThread extends Thread {

	private String result = "";
	private LightSensor lightSensor;
	private double lengthCoef;
	private boolean bool = true;
	private boolean found = false;
	private int command;

	public BarcodeThread(String str, LightSensor lightSensor, int command) {
		super(str);
		this.lightSensor = lightSensor;
		this.command = command;
	}
	
	public BarcodeThread(String str, LightSensor lightSensor, double lenghtCoef, int command) {
		super(str);
		this.lightSensor = lightSensor;
		this.lengthCoef = lenghtCoef;
		this.command = command;
	}
	
	@Override
	public void run() {
		if(command == 0) {
			while(bool) {
				if(lightSensor.getLightValue() < 35)
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
				if(lightSensor.getLightValue() < 35) 
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


/*
Motor.A.resetTachoCount();
Motor.B.resetTachoCount();
Motor.A.forward();
Motor.B.forward();
Motor.A.rotateTo(distance, true);
Motor.B.rotateTo(distance, true);
while(bool)
	if(Motor.A.getTachoCount() >= distance)
		return;
int tachoCount = Motor.A.getTachoCount();
Motor.A.rotateTo((int)Math.round(tachoCount + 17*lengthCoef), true);
Motor.B.rotateTo((int)Math.round(tachoCount + 17*lengthCoef), true);
for(int i = 1; i <= 6; i++) {
	while(Motor.A.getTachoCount() < (int)Math.round(tachoCount + (1+i*2)*lengthCoef));
	if(lightSensor.getLightValue() < 40) 
		result = result + "0";
	else
		result = result + "1";
}
while(Motor.A.isMoving());
changeBool();
while(bool);
*/