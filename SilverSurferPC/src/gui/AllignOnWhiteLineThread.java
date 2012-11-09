package gui;

import java.io.IOException;

import communication.UnitCommunicator;

public class AllignOnWhiteLineThread extends Thread {

	private UnitCommunicator unitCommunicator;

	
	
	
	public AllignOnWhiteLineThread(String str) {
		super(str);
	}
	
	
	public void run() {
		//System.out.println(unitCommunicator.getConsoleTag() + " Turning 90 degrees");
		try {
			unitCommunicator.sendCommandToUnit(14);
		} catch (IOException e) {
			System.out.println(unitCommunicator.getConsoleTag() + "Oops! Failed to execute the turning.");
		}
	}
	
	public UnitCommunicator getUnitCommunicator() {
		return unitCommunicator;
	}
	
	public void setUnitCommunicator(UnitCommunicator unitCommunicator) {
		this.unitCommunicator = unitCommunicator;
	}


}
