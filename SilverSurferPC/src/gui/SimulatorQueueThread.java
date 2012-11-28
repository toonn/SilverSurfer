package gui;

import communication.*;

import java.util.LinkedList;

public class SimulatorQueueThread extends Thread {

	private Communicator communicator;
	private LinkedList<Integer> queue;
	private boolean quit = false;

	public SimulatorQueueThread(String str) {
		super(str);
	}
	
	@Override
	public void run() {
		while(!quit) {
			while(!queue.isEmpty()) {
				communicator.executeCommand(queue.poll());
			}
			try {
				Thread.sleep(50);
			} catch(Exception e) {
				System.out.println("Error in SimulatorQueueThread.run()!");
			}
		}
	}
	
	public Communicator getCommunicator() {
		return communicator;
	}
	
	public void setCommunicator(Communicator communicator) {
		this.communicator = communicator;
	}
	
	public void setQueue(LinkedList<Integer> queue) {
		this.queue = queue;
	}
	
	public void addCommand(int command) {
		queue.offer(command);
	}
	
	public void setQuit(boolean quit) {
		this.quit = quit;
	}
}