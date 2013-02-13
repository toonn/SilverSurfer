package simulator;

import gui.SilverSurferGUI;
import lejos.nxt.*;

public class BarcodeThread extends Thread {

    private boolean quit = false;
    private boolean found = false;

    private SimulationPilot simulationPilot;

    public BarcodeThread(String str, SimulationPilot simulationPilot) {
        super(str);
        this.simulationPilot = simulationPilot;
    }

    @Override
    public void run() {
        while (!quit) {
            try {
                Thread.sleep(2);
            } catch (Exception e) {

            }
            int lightsensorValue = SilverSurferGUI.getInformationBuffer()
                    .getLatestLightSensorInfo();
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