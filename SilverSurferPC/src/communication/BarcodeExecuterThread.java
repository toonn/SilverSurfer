package communication;

import gui.*;

//Executes a barcode in a different thread.
public class BarcodeExecuterThread extends Thread {
	
	private SilverSurferGUI SSG;
	private int barcode;
	
	public BarcodeExecuterThread(String str, SilverSurferGUI SSG, int barcode) {
		super(str);
		this.barcode = barcode;
		this.SSG = SSG;
	}
	
	@Override
	public void run() {
		executeBarcode(barcode);
		SSG.getCommunicator().setExecutingBarcodes(false);
	}
	
	private void executeBarcode(int barcode) {
		try {
            switch (barcode) {
            default:
                break;
            }
        } catch (Exception e) {
            System.out.println("Error in BarcodeExecuterThread.executeBarcode(int barcode)!");
        }
	}
}