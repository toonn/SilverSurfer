package communication;

import lejos.nxt.Motor;

//Allows the robot to move continuously until stopped (needed for white line, ...).
public class MoveThread extends Thread {
	
	private boolean quit = false;

	public MoveThread(String str) {
		super(str);
	}

	@Override
	public void run() {
		try {
			Thread.sleep(500);
		} catch (Exception e) {
            System.out.println("Error in MoveThread.run()!");
		}
		Motor.A.forward();
		Motor.B.forward();
		while(!quit);
        Motor.A.stop(true);
        Motor.B.stop();
	}
	
	public void setQuit(boolean quit) {
		this.quit = quit;
	}
}