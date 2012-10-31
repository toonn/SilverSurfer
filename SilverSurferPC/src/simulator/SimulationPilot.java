package simulator;

import gui.SilverSurferGUI;

public class SimulationPilot {
	
	private float x = 220;
	private float y = 220;
	private float alpha = 0;
	private int speed = 30;
	private SilverSurferGUI SSG = new SilverSurferGUI();
	
	public SimulationPilot() {
		SSG.getSimulationPanel().setRobotLocation(this.getX(), this.getY(), this.getAlpha());
	}

	public float getX() {
		return x;
	}
	
	public void setX(float x) {
		this.x = x;
	}
	
	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
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
				float xOld = (float) (this.getX() + i* Math.cos(Math.toRadians(this.getAlpha())));
				float yOld = (float) (this.getY() + i* Math.sin(Math.toRadians(this.getAlpha())));
				SSG.getSimulationPanel().setRobotLocation(xOld, yOld, this.getAlpha());
				try {
					Thread.sleep(speed);
				} catch (InterruptedException e) {
	
				}
			}
			this.setX((float) (this.getX() + distance*Math.cos(Math.toRadians(this.getAlpha()))));
			this.setY((float) (this.getY() + distance*Math.sin(Math.toRadians(this.getAlpha()))));
		}
		else if(distance <= 0) {
			for (int i = -1; i >= distance; i--) {
				float xOld = (float) (this.getX() + i* Math.cos(Math.toRadians(this.getAlpha())));
				float yOld = (float) (this.getY() + i* Math.sin(Math.toRadians(this.getAlpha())));
				SSG.getSimulationPanel().setRobotLocation(xOld, yOld, this.getAlpha());
				try {
					Thread.sleep(speed);
				} catch (InterruptedException e) {
	
				}
			}
			this.setX((float) (this.getX() + distance*Math.cos(Math.toRadians(this.getAlpha()))));
			this.setY((float) (this.getY() + distance*Math.sin(Math.toRadians(this.getAlpha()))));
		}
	}
		
	public void rotate(float alpha) {
		this.setAlpha(ExtMath.addDegree(this.getAlpha(), alpha));
		SSG.getSimulationPanel().setRobotLocation(this.getX(), this.getY(), this.getAlpha());
	}
	
	public void clear() {
		SSG.getSimulationPanel().clear();
	}
}