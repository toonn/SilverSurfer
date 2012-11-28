package communication;

import lejos.nxt.Motor;

public class WhiteLineThread extends Thread {
	
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