package threads;

import brick.CommandUnit;

public class SensorThread extends Thread {

    private CommandUnit CU;
    private boolean quit = false;

    public SensorThread(String str, CommandUnit CU) {
        super(str);
        this.CU = CU;
    }

    @Override
    public void run() {
        while (!quit) {
            CU.updateStatus();
            try {
                Thread.sleep(100);
            } catch (Exception e) {
                System.out.println("Error in SensorThread.run()!");
            }
        }
    }

    public void setQuit(boolean quit) {
        this.quit = quit;
    }
}