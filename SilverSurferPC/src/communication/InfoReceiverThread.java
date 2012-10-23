package communication;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class InfoReceiverThread extends Thread{
	
	private static InputStream dis;
	private static OutputStream dos;
	private boolean quit = false;
	
	public static void setDis(InputStream dis) {
		InfoReceiverThread.dis = dis;
	}
	public static InputStream getDis() {
		return dis;
	}
	public static void setDos(OutputStream dos) {
		InfoReceiverThread.dos = dos;
	}
	public static OutputStream getDos() {
		return dos;
	}
	
	public void setQuit(boolean quit) {
		this.quit = quit;
	}

	
	public void run() {
		byte[] b = new byte[500];

		while(!quit){
			try {
				dis.read(b);
				System.out.println(new String(b));
				System.out.println("testCommandReceiverThread");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
}
