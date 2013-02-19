package gui;

import communication.*;

//Thread to move and turn real-time, only needed for manual testing with the arrow-buttons (forward, left, right).
public class MoveTurnThread extends Thread {
	
	private Communicator communicator;
	private int length;
	private int angles;
	private int amtOfAngles;
	private int command;
	
	public MoveTurnThread(String str, Communicator communicator, int length, int angles, int amtOfAngles, int command) {
		super(str);
		this.communicator = communicator;
		this.length = length;
		this.angles = angles;
		this.amtOfAngles = amtOfAngles;
		this.command = command;
	}
	
	@Override
	public void run() {
		if(length == 0 && angles == 0 && amtOfAngles == 0)
			communicator.sendCommand(command);
		else
			communicator.moveTurn(length, angles, amtOfAngles);
	}
}