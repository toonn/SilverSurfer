package communication;

import lejos.nxt.Motor;

//Allows the robot to move continuously until stopped (needed for white line, ...).
public class MoveThread extends Thread {
	
	private boolean quit = false;
	private int command = 0;

	public MoveThread(String str) {
		super(str);
	}

	@Override
	public void run() {
		try {
			Thread.sleep(500);
		} catch (Exception e) {
			
		}
		if(command == 0) { //Move forward
			Motor.A.forward();
			Motor.B.forward();
		}
		else { //Turn right
			Motor.A.backward();
			Motor.B.forward();
		}
		while(!quit);
        Motor.A.stop(true);
        Motor.B.stop();
	}
	
	public void setQuit(boolean quit) {
		this.quit = quit;
	}
	
	public void setCommand(int command) {
		this.command = command;
	}
}