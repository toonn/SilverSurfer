/*package gui;

import communication.*;

public class MouseClickThread extends Thread {
	
	private Communicator communicator;
	private int command;
	private boolean quit = false;

	public MouseClickThread(String str) {
		super(str);
	}
	
	@Override
	public void run() {
		while(!quit) {
			communicator.sendCommand(command);
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {

			}
		}
	}
	
	public Communicator getCommunicator() {
		return communicator;
	}
	
	public void setCommunicator(Communicator communicator) {
		this.communicator = communicator;
	}
	
	public void setCommand(int command) {
		this.command = command;
	}
	
	public void setQuit(boolean quit) {
		this.quit = quit;
	}
}*/