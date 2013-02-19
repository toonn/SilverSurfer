package gui;

import simulator.ViewPort;

//Used for zooming in.
public class ZoomThread extends Thread {
	
	private ViewPort simulationPanel;
	private boolean ZoomIn = false;
	private final static double scaleDifferencePerZoom = 1f/4f;
	
	public ZoomThread(String str, ViewPort simulationpanel, boolean ZoomIn) {
		super(str);
		this.simulationPanel = simulationpanel;
		this.ZoomIn = ZoomIn;
	}
	
	@Override
	public void run() {
		double scale = 0;
		if(simulationPanel.getScalingfactor() == scaleDifferencePerZoom && !ZoomIn)
			return;		
		if(simulationPanel.getScalingfactor() == 3 && ZoomIn)
			return;		
		if(ZoomIn)
			scale = simulationPanel.getScalingfactor() + scaleDifferencePerZoom;
		else
			scale = simulationPanel.getScalingfactor() - scaleDifferencePerZoom;
		simulationPanel.setScalingfactor(scale);
	}
}