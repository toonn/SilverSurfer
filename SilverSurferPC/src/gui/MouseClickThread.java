package gui;

import java.io.IOException;

import communication.*;

public class MouseClickThread extends Thread {
	
	private UnitCommunicator unitCommunicator;
	private int command;
	private int speed = 30;
	private boolean ride = true;

	public MouseClickThread(String str) {
		super(str);
	}
	
	public void run() {
//		System.err.println("MouseClickThread.run()");
		while(ride) {
			try {
//				System.err.println("about to try to send command by unitComm "+unitCommunicator);
				unitCommunicator.sendCommandToUnit(command);
//				System.err.println("command " + command + " uitgevoerd" );
				Thread.sleep(speed);
//				System.err.println("just slept "+speed);
			} catch (IOException e) {
//				System.err.println("Error in mouseClickThread: "+e.getClass());
				e.printStackTrace();
			} catch (InterruptedException e) {
//				System.err.println("Error in mocuseClickThread: "+e.getClass());
				e.printStackTrace();
			}
		}
	}
	
	public UnitCommunicator getUnitCommunicator() {
		return unitCommunicator;
	}
	
	public void setUnitCommunicator(UnitCommunicator unitCommunicator) {
		this.unitCommunicator = unitCommunicator;
	}
	
	public void setCommand(int command) {
		this.command = command;
	}
	
	public void setRide(boolean ride) {
		this.ride = ride;
	}
	
	public void setSpeed(int speed) {
		if(speed == 1)
			this.speed = 40;
		else if(speed == 2)
			this.speed = 30;
		else if(speed == 3)
			this.speed = 20;
		else
			this.speed = 10;
	}
}