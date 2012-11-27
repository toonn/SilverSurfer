package communication;

import java.io.*;

public class InfoReceiverThread extends Thread {
	
	private static DataInputStream dis;
	private static DataOutputStream dos;
	private StatusInfoBuffer statusInfoBuffer;
	private boolean quit = false;
	private double[] coordinates = new double[2];
	private int[] lookAroundResult = new int[4];
	
	public InfoReceiverThread(StatusInfoBuffer statusInfoBuffer) {
		this.statusInfoBuffer = statusInfoBuffer;
	}
	
	@Override
	public void run() {
		byte[] b = new byte[500];

		while(!quit) {
			try {
				b = new byte[500];
				dis.read(b);
				String a = new String(b);
				if(a.startsWith("[LS]"))
					statusInfoBuffer.addLightSensorInfo(Integer.parseInt(a.substring(5).trim()));
				else if(a.startsWith("[US]"))
					statusInfoBuffer.addUltraSensorInfo(Integer.parseInt(a.substring(5).trim()));
				else if(a.startsWith("[TS1]"))
					statusInfoBuffer.addTouchSensor1Info(Boolean.valueOf(a.substring(6).trim()));
				else if(a.startsWith("[TS2]"))
					statusInfoBuffer.addTouchSensor2Info(Boolean.valueOf(a.substring(6).trim()));
				else if(a.startsWith("[LM]")) {
					if(a.substring(5).startsWith("true")) {
						statusInfoBuffer.setLeftMotorMoving(true);
						statusInfoBuffer.setLeftMotorSpeed(Integer.parseInt(a.substring(10).trim()));
					}
					else if(a.substring(5).startsWith("false")) {
						statusInfoBuffer.setLeftMotorMoving(false);
						statusInfoBuffer.setLeftMotorSpeed(Integer.parseInt(a.substring(11).trim()));
					}
				}
				else if(a.startsWith("[RM]")) {
					if(a.substring(5).startsWith("true")) {
						statusInfoBuffer.setRightMotorMoving(true);
						statusInfoBuffer.setRightMotorSpeed(Integer.parseInt(a.substring(10).trim()));
					}
					else if(a.substring(5).startsWith("false")) {
						statusInfoBuffer.setRightMotorMoving(false);
						statusInfoBuffer.setRightMotorSpeed(Integer.parseInt(a.substring(11).trim()));
					}
				}
				else if(a.startsWith("[B]"))
					statusInfoBuffer.setBusy(Boolean.valueOf(a.substring(4).trim()));
				else if(a.startsWith("[X]"))
					coordinates[0] = Double.valueOf(a.substring(4).trim());
				else if(a.startsWith("[Y]")) {
					coordinates[1] = Double.valueOf(a.substring(4).trim());
					statusInfoBuffer.setCoordinatesAbsolute(coordinates);
				}
				else if(a.startsWith("[ANG]"))
					statusInfoBuffer.setAngle(Double.valueOf(a.substring(6).trim()));
				else if(a.startsWith("[RAL]"))
					System.out.println("Align on white line: " + a.substring(6).trim());
				else if(a.startsWith("[RAW]"))
					System.out.println("Align on walls: " + Boolean.valueOf(a.substring(6).trim()));
				else if(a.startsWith("[LA0]"))
					lookAroundResult[0] = Integer.valueOf(a.substring(6).trim());
				else if(a.startsWith("[LA1]"))
					lookAroundResult[1] = Integer.valueOf(a.substring(6).trim());
				else if(a.startsWith("[LA2]"))
					lookAroundResult[2] = Integer.valueOf(a.substring(6).trim());
				else if(a.startsWith("[LA3]"))
					lookAroundResult[3] = Integer.valueOf(a.substring(6).trim());
				else if(a.startsWith("[BC]"))
					statusInfoBuffer.setBarcode(Integer.parseInt(a.substring(5).trim()));
				else if(a.startsWith("[TEST]"))
					System.out.println("Testing: " + a.substring(7).trim());
			} catch (Exception e) {
				System.out.println("Error in InfoReceiverThread.run()!");
				e.printStackTrace();
			}
		}
	}
	
	public DataInputStream getDis() {
		return dis;
	}
	
	public void setDis(DataInputStream dis) {
		InfoReceiverThread.dis = dis;
	}
	
	public DataOutputStream getDos() {
		return dos;
	}
	
	public void setDos(DataOutputStream dos) {
		InfoReceiverThread.dos = dos;
	}
	
	public void setQuit(boolean quit) {
		this.quit = quit;
	}
}