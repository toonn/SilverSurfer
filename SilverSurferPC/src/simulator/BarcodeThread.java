package simulator;

import gui.SilverSurferGUI;
import lejos.nxt.*;

public class BarcodeThread extends Thread {

	private boolean quit = false;
	private boolean found = false;
	
	private SilverSurferGUI gui;

	public BarcodeThread(String str, SilverSurferGUI gui) {
		super(str);
		setgui(gui);
	}
	
	@Override
	public void run() {
		while(!quit) {
			try {
				Thread.sleep(50);
			} catch(Exception e) {
				
			}
			int lightsensorValue = SilverSurferGUI.getInformationBuffer().getLatestLightSensorInfo();
			if(lightsensorValue < 40 && lightsensorValue > 10) {
				found = true;
			}
		}
	}
	
	public void setgui(SilverSurferGUI gui) {
		this.gui = gui;
	}
	
	public SilverSurferGUI getGui() {
		return gui;
	}
	
	public void setQuit(boolean quit) {
		this.quit = quit;
	}
	
	public boolean getFound() {
		return found;
	}
}