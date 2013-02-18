/*package gui;

import simulator.SimulationJPanel;

//Used for moving the simulatorscreen.
public class ShiftThread extends Thread {
	private SimulationJPanel simulationPanel;
	private boolean horizontal = false;
	private int shift = 0;
	
	public ShiftThread(String str) {
		super(str);
	}
	
	@Override
	public void run() {	
		if(horizontal)
			simulationPanel.setShiftToTheRight(shift);
		else
			simulationPanel.setShiftDown(shift);
	}

	public void setSimulationPanel(SimulationJPanel simulationpanel){
		this.simulationPanel = simulationpanel;
	}
	
	public void setHorizontal(boolean horizontal){
		this.horizontal = horizontal;
	}
	
	public void setShift(int shift){
		this.shift = shift;
	}

}*/