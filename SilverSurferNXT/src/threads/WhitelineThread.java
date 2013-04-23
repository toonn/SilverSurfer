package threads;

import lejos.nxt.Motor;
import lejos.nxt.UltrasonicSensor;

public class WhitelineThread extends Thread {

	private UltrasonicSensor ultrasonicSensor;
	private double lengthCoef;
	private double angleCoefRight;
	private double angleCoefLeft;
	private int wallDistance;
	private int wallDistanceLimit;
	private double lightSensorDistance;
	private boolean firstQuit = false;
	private boolean secondQuit = false;

	public WhitelineThread(String str, UltrasonicSensor ultrasonicSensor, double lenghtCoef, double angleCoefRight, double angleCoefLeft, int wallDistance, int wallDistanceLimit, double lightSensorDistance) {
		super(str);
		this.ultrasonicSensor = ultrasonicSensor;
		this.lengthCoef = lenghtCoef;
		this.angleCoefRight = angleCoefRight;
		this.angleCoefLeft = angleCoefLeft;
		this.wallDistance = wallDistance;
		this.wallDistanceLimit = wallDistanceLimit;
		this.lightSensorDistance = lightSensorDistance;
	}

	@Override
	public void run() {
		Motor.A.forward();
		Motor.B.forward();
		while(!firstQuit);
		Motor.A.rotate((int)Math.round(lightSensorDistance*lengthCoef), true);
		Motor.B.rotate((int)Math.round(lightSensorDistance*lengthCoef));
		Motor.A.setAcceleration(1000);
		Motor.B.setAcceleration(1000);
		Motor.A.forward();
		Motor.B.backward();
		Motor.A.setAcceleration(6000);
		Motor.B.setAcceleration(6000);
		while(!secondQuit);
		try {
			Motor.A.stop(true);
			Motor.B.stop(true);
			Thread.sleep(500);
			int value = ultrasonicSensor.getDistance();
			if(value != wallDistance && value < wallDistanceLimit) {
				int backup = Motor.A.getSpeed();
				Motor.A.setSpeed(50);
				Motor.B.setSpeed(50);
		        Motor.A.rotate((int)Math.round((value-wallDistance)*lengthCoef), true);
		        Motor.B.rotate((int)Math.round((value-wallDistance)*lengthCoef));
				Motor.A.setSpeed(backup);
				Motor.B.setSpeed(backup);
			}
			Thread.sleep(500);
		} catch(Exception e) {
			
		}
		Motor.A.rotate(-(int)(angleCoefLeft*90), true);
		Motor.B.rotate((int)(angleCoefRight*90));
	}
	
	public void setFirstQuit(boolean firstQuit) {
		this.firstQuit = firstQuit;
	}
	
	public void setSecondQuit(boolean secondQuit) {
		this.secondQuit = secondQuit;
	}
}