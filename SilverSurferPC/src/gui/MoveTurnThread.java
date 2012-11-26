package gui;

import communication.*;

public class MoveTurnThread extends Thread {
	
	private Communicator communicator;
	private int length;
	private int angles;
	private int amtOfAngles;
	private int command;
	
	public MoveTurnThread(String str) {
		super(str);
	}
	
	@Override
	public void run() {
		if(length == 0 && angles == 0 && amtOfAngles == 0)
			communicator.sendCommand(command);
		else
			communicator.moveTurn(length, angles, amtOfAngles);
	}
	
	public Communicator getCommunicator() {
		return communicator;
	}
	
	public void setCommunicator(Communicator communicator) {
		this.communicator = communicator;
	}
	
	public int getLength() {
		return length;
	}
	
	public void setLength(int length) {
		this.length = length;
	}
	
	public int getAngles() {
		return angles;
	}
	
	public void setAngles(int angles) {
		this.angles = angles;
	}
	
	public int getAmtOfAngles() {
		return amtOfAngles;
	}
	
	public void setAmtOfAngles(int amtOfAngles) {
		this.amtOfAngles = amtOfAngles;
	}

	public int getCommand() {
		return command;
	}

	public void setCommand(int command) {
		this.command = command;
	}
}