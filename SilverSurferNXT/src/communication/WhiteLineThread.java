package communication;

import statemachine.*;

public class WhiteLineThread extends Thread {

	private CommandUnit CU;
	private boolean quit = false;

	public WhiteLineThread(String str) {
		super(str);
	}

	@Override
	public void run() {
		try {
			Thread.sleep(500);
		} catch (Exception e) {
			
		}
		CU.setCurrentState(new DrivingForward());
		while(!quit);
		CU.setCurrentState(new Waiting());
	}
	
	public void setCommandUnit(CommandUnit CU) {
		this.CU = CU;
	}
	
	public void setQuit(boolean quit) {
		this.quit = quit;
	}
}