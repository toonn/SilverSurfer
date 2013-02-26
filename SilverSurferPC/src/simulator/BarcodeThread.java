package simulator;

import gui.SilverSurferGUI;

public class BarcodeThread extends Thread {

    private boolean quit = false;
    private boolean found = false;

    public BarcodeThread(final String str) {
        super(str);
    }

    public boolean getFound() {
        return found;
    }

    @Override
    public void run() {
        while (!quit) {
            try {
                Thread.sleep(2);
            } catch (final Exception e) {

            }
            final int lightsensorValue = SilverSurferGUI.getStatusInfoBuffer()
                    .getLatestLightSensorInfo();
            if (lightsensorValue < 40 && lightsensorValue > 10) {
                found = true;
            }
        }
    }

    public void setQuit(final boolean quit) {
        this.quit = quit;
    }
}