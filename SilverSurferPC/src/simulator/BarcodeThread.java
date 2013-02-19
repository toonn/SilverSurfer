package simulator;

import gui.SilverSurferGUI;

public class BarcodeThread extends Thread {

    private boolean quit = false;
    private boolean found = false;

    public BarcodeThread(String str) {
        super(str);
    }

    @Override
    public void run() {
        while (!quit) {
            try {
                Thread.sleep(2);
            } catch (Exception e) {

            }
            int lightsensorValue = SilverSurferGUI.getStatusInfoBuffer().getLatestLightSensorInfo();
            if (lightsensorValue < 40 && lightsensorValue > 10) {
                found = true;
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