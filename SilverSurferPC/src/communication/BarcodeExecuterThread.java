package communication;

import gui.*;

//Executes a barcode in a different thread.
public class BarcodeExecuterThread extends Thread {
	
	private SilverSurferGUI SSG;
	private int barcode;
	
	public BarcodeExecuterThread(String str) {
		super(str);
	}
	
	@Override
	public void run() {
		executeBarcode(barcode);
		SSG.getCommunicator().setExecutingBarcodes(false);
	}
	
	public void setBarcode(int barcode) {
		this.barcode = barcode;
	}
	
	public void setSSG(SilverSurferGUI SSG) {
		this.SSG = SSG;
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