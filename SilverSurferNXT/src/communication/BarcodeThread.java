package communication;

import lejos.nxt.*;

public class BarcodeThread extends Thread {

	private LightSensor lightSensor;
	private boolean quit = false;
	private boolean found = false;

	public BarcodeThread(String str) {
		super(str);
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
	
	public void setLightSensor(LightSensor lightSensor) {
		this.lightSensor = lightSensor;
	}
	
	public void setQuit(boolean quit) {
		this.quit = quit;
	}
	
	public boolean getFound() {
		return found;
	}
}