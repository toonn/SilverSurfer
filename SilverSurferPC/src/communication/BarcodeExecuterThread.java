package communication;

//Executes a barcode in a different thread.
public class BarcodeExecuterThread extends Thread {

    private Communicator communicator;
    private int barcode;

    public BarcodeExecuterThread(String str, Communicator communicator,
            int barcode) {
        super(str);
        this.barcode = barcode;
        this.communicator = communicator;
    }

    @Override
    public void run() {
        executeBarcode(barcode);
        communicator.setExecutingBarcodes(false);
    }

    private void executeBarcode(int barcode) {
        try {
            switch (barcode) {
            default:
                break;
            }
        } catch (Exception e) {
            System.out
                    .println("Error in BarcodeExecuterThread.executeBarcode(int barcode)!");
        }
    }
}