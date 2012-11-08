package communication;

public class SensorThread extends Thread {
	
	private boolean quit = false;
	private CommandUnit CU;

	public SensorThread(String str) {
		super(str);
	}

	public void run() {
		try {
			while(!quit) {
					CU.updateStatus();
					Thread.sleep(100);
			}
		} catch(Exception e) {
			
		}
	}
	
	public void setQuit(boolean quit) {
		this.quit = quit;
	}
	
	public void setCommandUnit(CommandUnit CU) {
		this.CU = CU;
	}
}