package communication;

//Continuously updates status info.
public class SensorThread extends Thread {

    private CommandUnit CU;
    private boolean quit = false;

    public SensorThread(String str) {
        super(str);
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

    public void setCommandUnit(CommandUnit CU) {
        this.CU = CU;
    }

    public void setQuit(boolean quit) {
        this.quit = quit;
    }
}