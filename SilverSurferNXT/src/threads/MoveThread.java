package threads;

import lejos.nxt.Motor;

//Allows the robot to move continuously until stopped (needed for white line, ...).
public class MoveThread extends Thread {
	
	private boolean quit = false;
	private int command = 0;

	public MoveThread(String str, int command) {
		super(str);
		this.command = command;
	}

	@Override
	public void run() {
		try {
			Thread.sleep(500);
		} catch (Exception e) {
            System.out.println("Error in MoveThread.run()!");
		}
		if(command == 0) {
			Motor.A.forward();
			Motor.B.forward();
		}
		else if(command == 1) {
			Motor.A.backward();
			Motor.B.backward();
		}
		while(!quit);
        Motor.A.stop(true);
        Motor.B.stop();
	}
	
	public void setQuit(boolean quit) {
		this.quit = quit;
	}
}