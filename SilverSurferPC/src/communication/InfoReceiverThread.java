package communication;

import java.io.*;

public class InfoReceiverThread extends Thread{
	
	private static DataInputStream dis;
	private static DataOutputStream dos;
	private boolean quit = false;
	
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
				if(a.startsWith("LIG"))
					System.out.println("dit is lichtinfo");
				else if(a.startsWith("DRU"))
					System.out.println("dit is drukinfo");
				else if(a.startsWith("RES"))
					System.out.println("dit is restinfo");
			} catch (IOException e) {

			}
		}
	}
}
