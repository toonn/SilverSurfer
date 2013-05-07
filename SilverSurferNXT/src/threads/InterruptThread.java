package threads;

import lejos.nxt.UltrasonicSensor;
import brick.CommandUnit;

public class InterruptThread extends Thread {

	private CommandUnit CU;
	private UltrasonicSensor ultrasonicSensor;
	private int distance;
	private double lengthCoef;
	public boolean quit = false;
	
	public InterruptThread(CommandUnit CU, UltrasonicSensor ultrasonicSensor, int distance, double lenghtCoef) {
		this.CU = CU;
		this.ultrasonicSensor = ultrasonicSensor;
		this.distance = distance;
		this.lengthCoef = lenghtCoef;
	}
	
	@Override
	public void run() {
		while(!quit && ultrasonicSensor.getDistance() > 21) {
			try {
				Thread.sleep(100);
			} catch(Exception e) {
				
			}
		}
		if(!quit)
			CU.interrupt = true;
	}
}