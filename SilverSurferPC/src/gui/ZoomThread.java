package gui;

import simulator.SimulationJPanel;

//Used for zooming in.
public class ZoomThread extends Thread {
	
	private SimulationJPanel simulationPanel;
	private boolean ZoomIn = false;
	private final static double scaleDifferencePerZoom = 1f/4f;
	
	public ZoomThread(String str) {
		super(str);
	}
	
	@Override
	public void run() {
		double scale = 0;
		if(simulationPanel.getScalingfactor() == scaleDifferencePerZoom && !ZoomIn){
			return;
		}
		
		if(simulationPanel.getScalingfactor() == 3 && ZoomIn){
			return;
		}
		
		if(ZoomIn){
			scale = simulationPanel.getScalingfactor() + scaleDifferencePerZoom;
		}
		else{
			scale = simulationPanel.getScalingfactor() - scaleDifferencePerZoom;
		}
		
		simulationPanel.setScalingfactor(scale);
		
	}

	public void setSimulationPanel(SimulationJPanel simulationpanel){
		this.simulationPanel = simulationpanel;
	}
	
	public void Zoomin(boolean Zoomin){
		this.ZoomIn = Zoomin;
	}
	
}