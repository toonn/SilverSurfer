package simulator;

import gui.SilverSurferGUI;

public class SimulationPilot {

	private float x = 200;
	private float y = 200;
	private float alpha;
	private SilverSurferGUI gui = new SilverSurferGUI();
	
	public float getX() {
		return x;
	}
	
	public float getY() {
		return y;
	}
	
	public float getAlpha() {
		return alpha;
	}
	
	public void setAlpha(float alpha) {
		this.alpha = alpha;
	}
	
	public void travel(float distance) {
		for (int i = 1; i <= distance; i++) {
			float xOld = (float) (x + i* Math.cos(Math.toRadians(alpha)));
			float yOld = (float) (y + i* Math.sin(Math.toRadians(alpha)));
			gui.getSimulationPanel().setRobotLocation(xOld, yOld);
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {

			}
		}
		this.x = (float) (this.x + distance*Math.cos(Math.toRadians(alpha)));
		this.y = (float) (this.y + distance*Math.sin(Math.toRadians(alpha)));
	}
		
		
	public void rotate(float alpha) {
		this.alpha = ExtMath.addDegree(this.alpha, alpha);
	}	
}