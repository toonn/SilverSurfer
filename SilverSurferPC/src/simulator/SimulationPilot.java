package simulator;

import javax.swing.ImageIcon;


import gui.SilverSurferGUI;
import mapping.*;
import java.io.File;
public class SimulationPilot {
	
	private float x = 220;
	private float y = 220;
	private float alpha = 270;
	private int speed = 30;
	private SilverSurferGUI SSG = new SilverSurferGUI();
	private File mapFile;
	private MapGraph mapGraph;
	

	
	public SimulationPilot() {
		SSG.getSimulationPanel().setRobotLocation(this.getX(), this.getY(), this.getAlpha());
		mapFile = new File("resources/maze_maps/example_map.txt");
		mapGraph = MapReader.createMapFromFile(mapFile);
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

	public MapGraph getMapGraph() {
		return this.mapGraph;
	}
	public void setMapGraph(MapGraph mapGraph) {
		this.mapGraph = mapGraph;
	}
	
	private float getMaxRoundingError()
	{
		return (float) 0.4;
	}
	
	public void travel(float distance) {
		if(distance >= 0) {
			for (int i = 1; i <= distance; i++) {
				float xOld = (float) (this.getX() + i* Math.cos(Math.toRadians(this.getAlpha())));
				float yOld = (float) (this.getY() + i* Math.sin(Math.toRadians(this.getAlpha())));
				
				this.travelToNextTileIfNeeded(xOld, yOld);
				
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
				
				this.travelToNextTileIfNeeded(xOld, yOld);
				
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

	private void travelToNextTileIfNeeded(float xOld, float yOld) {
		if((xOld%40) > 40-this.getMaxRoundingError() || (xOld%40) < this.getMaxRoundingError())
		{
			if(this.getAlpha() > 270 || this.getAlpha() < 90)
			{
				this.getMapGraph().moveToNextTile(Orientation.EAST);
				// you have crossed a white line
			}
			else
			{
				this.getMapGraph().moveToNextTile(Orientation.WEST);
				// you have crossed a white line
			}
		}
		if((yOld%40) > 40-this.getMaxRoundingError() || (yOld%40) < this.getMaxRoundingError())
		{
			if(this.getAlpha() < 180)
			{
				this.getMapGraph().moveToNextTile(Orientation.SOUTH);
			}
			else
			{
				this.getMapGraph().moveToNextTile(Orientation.NORTH);
			}
		}
	}
	
	public void checkForObstructions()
	{
		Orientation currentOrientation = null;
		
		for(int i = 0; i < 8; i++)
		{
			if(currentOrientation != Orientation.calculateOrientation(this.getX(), this.getY(), this.getAlpha()))
			{
				currentOrientation = Orientation.calculateOrientation(this.getX(), this.getY(), this.getAlpha());
				if(this.getMapGraph().getObstruction(currentOrientation) == null)
				{
					SSG.getSimulationPanel().addWhiteLine(Orientation.calculateOrientation(this.getX(), this.getY(), this.getAlpha()));
					System.out.println(currentOrientation + ": witte lijn");
				}
				else if(this.getMapGraph().getObstruction(currentOrientation) == Obstruction.WALL)
				{
					SSG.getSimulationPanel().addWall(Orientation.calculateOrientation(this.getX(), this.getY(), this.getAlpha()));
					System.out.println(currentOrientation + ": muur");
				}
				else
				{
					System.out.println("Unidentified Obstruction!");;
				}
			}
			this.rotate(45);
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