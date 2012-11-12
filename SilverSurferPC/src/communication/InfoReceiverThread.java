package communication;

import java.io.*;

public class InfoReceiverThread extends Thread {
	
	private static DataInputStream dis;
	private static DataOutputStream dos;
	private boolean quit = false;
	private StatusInfoBuffer infoBuffer;
	
	public InfoReceiverThread(StatusInfoBuffer infoBuffer) {
		this.infoBuffer = infoBuffer;
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
	
	public void run() {
		byte[] b = new byte[500];

		while(!quit) {
			try {
				b = new byte[500];
				dis.read(b);
				String a = new String(b);
				if(a.startsWith("[LS]"))
					infoBuffer.addLightSensorInfo(Integer.parseInt(a.substring(5).trim()));
				else if(a.startsWith("[US]"))
					infoBuffer.addUltraSensorInfo(Integer.parseInt(a.substring(5).trim()));
				else if(a.startsWith("[TS1]"))
					infoBuffer.addTouchSensor1Info(Boolean.valueOf(a.substring(6).trim()));
				else if(a.startsWith("[TS2]"))
					infoBuffer.addTouchSensor2Info(Boolean.valueOf(a.substring(6).trim()));
				else if(a.startsWith("[LM]")) {
					if(a.substring(5).startsWith("true")) {
						infoBuffer.setLeftMotorMoving(true);
						infoBuffer.setLeftMotorSpeed(Integer.parseInt(a.substring(10).trim()));
					}
					else if(a.substring(5).startsWith("false")) {
						infoBuffer.setLeftMotorMoving(false);
						infoBuffer.setLeftMotorSpeed(Integer.parseInt(a.substring(11).trim()));
					}
				}
				else if(a.startsWith("[RM]")) {
					if(a.substring(5).startsWith("true")) {
						infoBuffer.setRightMotorMoving(true);
						infoBuffer.setRightMotorSpeed(Integer.parseInt(a.substring(10).trim()));
					}
					else if(a.substring(5).startsWith("false")) {
						infoBuffer.setRightMotorMoving(false);
						infoBuffer.setRightMotorSpeed(Integer.parseInt(a.substring(11).trim()));
					}
				}
				else if(a.startsWith("[B]"))
					infoBuffer.setBusy(Boolean.valueOf(a.substring(4).trim()));
				else if(a.startsWith("[R]"))
					System.out.println("Align on walls: " + Boolean.valueOf(a.substring(4).trim()));
			} catch (IOException e) {

			}
		}
	}
}