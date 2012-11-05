package gui;

import communication.*;

import java.io.IOException;

public class PolygonDrawThread extends Thread {
	
	private UnitCommunicator unitCommunicator;
	private int angles;
	private int length;

	public PolygonDrawThread(String str) {
		super(str);
	}
	
	public void run() {
		System.out.println(unitCommunicator.getConsoleTag() + " Executing polygon with " + angles + " angles and sides of " + length + " cm.");
		try {
			unitCommunicator.runPolygon(angles, length);
		} catch (IOException e) {
			System.out.println(unitCommunicator.getConsoleTag() + "Oops! Failed to execute this polygon.");
		}
	}
	
	public void runForward() {
		System.out.println(unitCommunicator.getConsoleTag() + " Goes 40 centimeters forward" );
		try {
			unitCommunicator.runForward(angles, length);
		} catch (IOException e) {
			System.out.println(unitCommunicator.getConsoleTag() + "Oops! Failed to execute the forwarding.");
		}
	}
	
	public void runTurning() {
		System.out.println(unitCommunicator.getConsoleTag() + " Turning 90 degrees");
		try {
			unitCommunicator.runTurning(angles, length);
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
