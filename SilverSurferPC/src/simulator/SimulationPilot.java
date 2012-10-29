package simulator;

import gui.SilverSurferGUI;

public class SimulationPilot {
	
	public SimulationPilot(){
		gui.getSimulationPanel().setRobotLocation(x, y);
	}

	private float x = 200;
	private float y = 200;
	private float alpha;
	private int speed = 30;
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
	
	public int getSpeed() {
		if(speed == 10)
			return 4;
		else if(speed == 20)
			return 3;
		else if(speed == 30)
			return 2;
		else
			return 1;
	}
	
	public void setSpeed(int speed) {
		if(speed == 1)
			this.speed = 40;
		else if(speed == 2)
			this.speed = 30;
		else if(speed == 3)
			this.speed = 20;
		else
			this.speed = 10;
	}
	
	public void travel(float distance) {
		if(distance >= 0) {
			for (int i = 1; i <= distance; i++) {
				float xOld = (float) (x + i* Math.cos(Math.toRadians(alpha)));
				float yOld = (float) (y + i* Math.sin(Math.toRadians(alpha)));
				gui.getSimulationPanel().setRobotLocation(xOld, yOld);
				try {
					Thread.sleep(speed);
				} catch (InterruptedException e) {
	
				}
			}
			this.x = (float) (this.x + distance*Math.cos(Math.toRadians(alpha)));
			this.y = (float) (this.y + distance*Math.sin(Math.toRadians(alpha)));
		}
		else if(distance <= 0) {
			for (int i = -1; i >= distance; i--) {
				float xOld = (float) (x + i* Math.cos(Math.toRadians(alpha)));
				float yOld = (float) (y + i* Math.sin(Math.toRadians(alpha)));
				gui.getSimulationPanel().setRobotLocation(xOld, yOld);
				try {
					Thread.sleep(speed);
				} catch (InterruptedException e) {
	
				}
			}
			this.x = (float) (this.x + distance*Math.cos(Math.toRadians(alpha)));
			this.y = (float) (this.y + distance*Math.sin(Math.toRadians(alpha)));
		}
	}
		
		
	public void rotate(float alpha) {
		this.alpha = ExtMath.addDegree(this.alpha, alpha);
	}
	
	public void clear(){
		gui.getSimulationPanel().clear();
	}
}