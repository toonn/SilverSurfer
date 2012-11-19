package communication;

import lejos.nxt.*;

public class BarcodeThread extends Thread {
	
	private boolean quit = false;
	private LightSensor lightSensor;
	private boolean found = false;

	public BarcodeThread(String str) {
		super(str);
	}
	
	public void run() {
		while(!quit) {
			try {
				Thread.sleep(100);
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
	
	public void setLightSensor(LightSensor lightSensor) {
		this.lightSensor = lightSensor;
	}
	
	public boolean getFound() {
		return found;
	}
}