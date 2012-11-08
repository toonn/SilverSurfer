package communication;

import statemachine.*;

public class WhiteLineThread extends Thread {
	
	private boolean quit = false;
	private CommandUnit CU;
	private int startState;

	public WhiteLineThread(String str) {
		super(str);
	}

	public void run() {
		try {
			Thread.sleep(500);
		} catch (Exception e) {
			
		}
		
		if(startState == 1)
			CU.setCurrentState(new DrivingForward());
		else if(startState == 2) 
			CU.setCurrentState(new TurnLeft());
		else if(startState == 3) 
			CU.setCurrentState(new TurnRight());
		
		while(!quit);
		
		CU.setCurrentState(new Waiting());
	}
	
	public void setCommandUnit(CommandUnit CU) {
		this.CU = CU;
	}
	
	public void setQuit(boolean quit) {
		this.quit = quit;
	}
	
	public void setStartState(int startState) {
		this.startState = startState;
	}
}