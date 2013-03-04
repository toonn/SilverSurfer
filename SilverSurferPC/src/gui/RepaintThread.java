package gui;

import simulator.viewport.AbstractViewPort;

public class RepaintThread extends Thread {

	private AbstractViewPort viewPort;
	
	public RepaintThread(AbstractViewPort viewPort) {
		this.viewPort = viewPort;
	}
	
	public void run() {
		while(true) {
			viewPort.repaint();
			try {
				Thread.sleep(30);
			}
			catch(Exception e) {
				
			}
		}
	}
}