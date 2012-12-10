package communication;

import lejos.nxt.Motor;

public class WhiteLineThread extends Thread {
	
	private boolean quit = false;
	private int command = 0;

	public WhiteLineThread(String str) {
		super(str);
	}

	@Override
	public void run() {
		try {
			Thread.sleep(500);
		} catch (Exception e) {
			
		}
		if(command == 0) {
			Motor.A.forward();
			Motor.B.forward();
		}
		else {
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