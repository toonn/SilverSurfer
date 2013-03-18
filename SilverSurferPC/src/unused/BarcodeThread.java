package unused;
/*package simulator;

import simulator.pilot.AbstractPilot;

public class BarcodeThread extends Thread {

    private boolean quit = false;
    private boolean found = false;
    private AbstractPilot pilot;

    public BarcodeThread(final String str, AbstractPilot pilot) {
        super(str);
        this.pilot = pilot;
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
            final int lightsensorValue = pilot.getLightSensorValue();
            if (lightsensorValue < 40 && lightsensorValue > 10) {
                found = true;
            }
        }
    }

    public void setQuit(final boolean quit) {
        this.quit = quit;
    }
}*/