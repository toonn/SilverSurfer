package threads;

import lejos.nxt.Motor;

public class WhitelineThread extends Thread {

	private double lengthCoef;
	private double angleCoef;
	private boolean firstQuit = false;
	private boolean secondQuit = false;

	public WhitelineThread(String str, double lenghtCoef, double angleCoef) {
		super(str);
		this.lengthCoef = lenghtCoef;
		this.angleCoef = angleCoef;
	}

	@Override
	public void run() {
		Motor.A.forward();
		Motor.B.forward();
		while(!firstQuit);
		Motor.A.rotate((int)Math.round(6.5*lengthCoef), true);
		Motor.B.rotate((int)Math.round(6.5*lengthCoef));
		Motor.A.setAcceleration(1000);
		Motor.B.setAcceleration(1000);
		Motor.A.forward();
		Motor.B.backward();
		Motor.A.setAcceleration(6000);
		Motor.B.setAcceleration(6000);
		while(!secondQuit);
		Motor.A.rotate(-(int)(angleCoef*90), true);
		Motor.B.rotate((int)(angleCoef*90));
	}
	
	public void setFirstQuit(boolean firstQuit) {
		this.firstQuit = firstQuit;
	}
	
	public void setSecondQuit(boolean secondQuit) {
		this.secondQuit = secondQuit;
	}
}