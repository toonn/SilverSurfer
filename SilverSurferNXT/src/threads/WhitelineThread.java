package threads;

import lejos.nxt.Motor;

public class WhitelineThread extends Thread {

	private double lengthCoef;
	private double angleCoefRight;
	private double angleCoefLeft;
	private boolean firstQuit = false;
	private boolean secondQuit = false;

	public WhitelineThread(String str, double lenghtCoef, double angleCoefRight, double angleCoefLeft) {
		super(str);
		this.lengthCoef = lenghtCoef;
		this.angleCoefRight = angleCoefRight;
		this.angleCoefLeft = angleCoefLeft;
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
		Motor.A.rotate(-(int)(angleCoefLeft*90), true);
		Motor.B.rotate((int)(angleCoefRight*90));
		Motor.A.rotate((int)Math.round(20*lengthCoef), true);
		Motor.B.rotate((int)Math.round(20*lengthCoef));
	}
	
	public void setFirstQuit(boolean firstQuit) {
		this.firstQuit = firstQuit;
	}
	
	public void setSecondQuit(boolean secondQuit) {
		this.secondQuit = secondQuit;
	}
}