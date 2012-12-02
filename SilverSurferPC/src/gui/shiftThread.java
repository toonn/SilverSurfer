package gui;

import simulator.SimulationJPanel;

public class shiftThread extends Thread {
	private SimulationJPanel simulationPanel;
	private boolean horizontal = false;
	private int shift = 0;
	
	public shiftThread(String str) {
		super(str);
	}
	
	@Override
	public void run() {
				
		if(horizontal){
			simulationPanel.setShiftToTheRigth(shift);
		}
		else{
			simulationPanel.setShiftDown(shift);
		}
		
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

}
