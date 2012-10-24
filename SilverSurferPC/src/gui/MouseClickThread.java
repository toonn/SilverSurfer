package gui;

import java.io.IOException;

import commands.Command;
import communication.UnitCommunicator;

public class MouseClickThread extends Thread {
	
	private UnitCommunicator unitCommunicator;
	private int command;
	private int speed = 30;
	private boolean ride = true;

	public MouseClickThread(String str) {
		super(str);
	}
	public void run() {
		while(ride) {
			try {
				unitCommunicator.sendCommandToUnit(command);
				Thread.sleep(speed);
			} catch (Exception e) {

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