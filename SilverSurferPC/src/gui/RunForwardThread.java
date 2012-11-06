package gui;

import communication.*;

import java.io.IOException;

public class RunForwardThread extends Thread {
	
	private UnitCommunicator unitCommunicator;
	private int angles;
	private int length;

	public RunForwardThread(String str) {
		super(str);
	}
	
	
	
	public void run() {
		System.out.println(unitCommunicator.getConsoleTag() + " Goes 40 centimeters forward" );
		try {
			unitCommunicator.runForward(angles, length);
		} catch (IOException e) {
			System.out.println(unitCommunicator.getConsoleTag() + "Oops! Failed to execute the forwarding.");
		}
	}
	
		
	public UnitCommunicator getUnitCommunicator() {
		return unitCommunicator;
	}
	
	public void setUnitCommunicator(UnitCommunicator unitCommunicator) {
		this.unitCommunicator = unitCommunicator;
	}
	
	public int getAngles() {
		return angles;
	}
	
	public void setAngles(int angles) {
		this.angles = angles;
	}
	
	public int getLength() {
		return length;
	}
	
	public void setLength(int length) {
		this.length = length;
	}
}
