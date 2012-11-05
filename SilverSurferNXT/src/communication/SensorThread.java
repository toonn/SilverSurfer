package communication;

public class SensorThread extends Thread {
	
	private boolean quit = false;
	private CommandUnit CU;

	public SensorThread(String str) {
		super(str);
	}

	public void run() {
		while(!quit) {
			CU.updateStatus();
		}
	}
	
	public void setQuit(boolean quit) {
		this.quit = quit;
	}
	
	public void setCommandUnit(CommandUnit CU) {
		this.CU = CU;
	}
}