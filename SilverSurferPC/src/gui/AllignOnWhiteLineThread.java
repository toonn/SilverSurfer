package gui;

import java.io.IOException;

import communication.UnitCommunicator;

public class AllignOnWhiteLineThread extends Thread {

	private UnitCommunicator unitCommunicator;
	private int command;

	public AllignOnWhiteLineThread(String str) {
		super(str);
	}
	
	
	public void run() {
		try {
			unitCommunicator.sendCommandToUnit(command);
		} catch (IOException e) {

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
}
