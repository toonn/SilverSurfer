package communication;

import lejos.nxt.*;

//Continuously searches for a barcode (= lightvalue < 40), stops the motors when one is found.
public class BarcodeThread extends Thread {

	private LightSensor lightSensor;
	private boolean quit = false;
	private boolean found = false;

	public BarcodeThread(String str, LightSensor lightSensor) {
		super(str);
		this.lightSensor = lightSensor;
	}
	
	@Override
	public void run() {
		while(!quit) {
			try {
				Thread.sleep(50);
			} catch(Exception e) {
				
			}
			if(lightSensor.getLightValue() < 40) {
				found = true;
				Motor.A.stop(true);
				Motor.B.stop();
			}
		}
	}
	
	public void setQuit(boolean quit) {
		this.quit = quit;
	}
	
	public boolean getFound() {
		return found;
	}
}