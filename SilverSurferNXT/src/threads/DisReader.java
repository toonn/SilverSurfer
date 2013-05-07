package threads;

import java.io.DataInputStream;
import java.util.ArrayList;

public class DisReader extends Thread {

	public ArrayList<Integer> queue = new ArrayList<Integer>();
	public ArrayList<Integer> otherQueue = new ArrayList<Integer>();
	public DataInputStream dis;
	
	public DisReader(DataInputStream dis) {
		this.dis = dis;
	}
	
	public void run() {
		while(true) {
			try {
				System.out.println(dis.readInt());
				//queue.add(dis.readInt());
			} catch(Exception e) {
				e.printStackTrace();
			}
			
		}
	}
	
	public int pop() {
		
		if(queue.size() < 1)
			return 50;
		int a = queue.get(0);
		queue.remove(0);
		return a;
	}
}