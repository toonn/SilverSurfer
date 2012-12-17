package communication;

import gui.*;

public class BarcodeExecuterThread extends Thread {
	
	private SilverSurferGUI SSG;
	
	public BarcodeExecuterThread(String str) {
		super(str);
	}
	
	@Override
	public void run() {
		SSG.executeBarcode();
		SSG.getCommunicator().setExecutingBarcodes(false);
	}
	
	public void setSSG(SilverSurferGUI SSG) {
		this.SSG = SSG;
	}
}