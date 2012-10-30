package communication;

import java.io.*;

public class InfoReceiverThread extends Thread{
	
	private static DataInputStream dis;
	private static DataOutputStream dos;
	private boolean quit = false;
	private StatusInfoBuffer infoBuffer;
	
	public InfoReceiverThread(StatusInfoBuffer info){
		this.infoBuffer = info;
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
					infoBuffer.setLightSensorInfo(Integer.parseInt(a.substring(5).trim()));
				else if(a.startsWith("[US]"))
					infoBuffer.setUltraSensorInfo(Integer.parseInt(a.substring(5).trim()));
				else if(a.startsWith("[PS1]"))
					infoBuffer.setPushSensor1Info(Boolean.valueOf(a.substring(6).trim()));
				else if(a.startsWith("[PS2]"))
					infoBuffer.setPushSensor2Info(Boolean.valueOf(a.substring(6).trim()));
				else if(a.startsWith("[MLM]")) {
					if(a.substring(6).startsWith("true")) {
						infoBuffer.setLeftMotorMoving(true);
						infoBuffer.setLeftMotorSpeed(Integer.parseInt(a.substring(11).trim()));
					}
					else if(a.substring(6).startsWith("false")) {
						infoBuffer.setLeftMotorMoving(false);
						infoBuffer.setLeftMotorSpeed(Integer.parseInt(a.substring(12).trim()));
					}
				}
				else if(a.startsWith("[MRM]")) {
					if(a.substring(6).startsWith("true")) {
						infoBuffer.setRightMotorMoving(true);
						infoBuffer.setRightMotorSpeed(Integer.parseInt(a.substring(11).trim()));
					}
					else if(a.substring(6).startsWith("false")) {
						infoBuffer.setRightMotorMoving(false);
						infoBuffer.setRightMotorSpeed(Integer.parseInt(a.substring(12).trim()));
					}
				}
			} catch (IOException e) {

			}
		}
	}
}
