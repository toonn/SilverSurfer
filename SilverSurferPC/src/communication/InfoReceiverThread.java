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
				dis.read(b);
				String a = new String(b);
				if(a.startsWith("[LS]"))
					System.out.println();
				else if(a.startsWith("[US]"))
					System.out.println("ultrasonic");
				else if(a.startsWith("[PS1]"))
					System.out.println("push");
				else if(a.startsWith("[PS2]"))
					System.out.println("ps2");
			} catch (IOException e) {

			}
		}
	}
}
