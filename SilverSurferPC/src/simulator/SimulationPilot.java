package simulator;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

import exception.SimulatorCrashedException;


import gui.SilverSurferGUI;
import mapping.*;

import java.io.File;
public class SimulationPilot {
	
	private final float startPositionAbsoluteX = 220;
	private final float startPositionAbsoluteY = 220;
	private float currentPositionAbsoluteX = 220;
	private float currentPositionAbsoluteY = 220;
	private float alpha = 90;
	private int speed = 30;
	private SilverSurferGUI SSG = new SilverSurferGUI();
	private File mapFile;
	private MapGraph mapGraph;
	

	
	public SimulationPilot() {
		SSG.getSimulationPanel().setRobotLocation(this.getCurrentPositionAbsoluteX(), this.getCurrentPositionAbsoluteY(), this.getAlpha());
		mapFile = new File("resources/maze_maps/example_map.txt");
		mapGraph = MapReader.createMapFromFile(mapFile);
		SSG.getSimulationPanel().setMapGraphConstructed(new MapGraph(0,0, mapGraph.getTiles().length, mapGraph.getTiles()[0].length));
		setCurrentTileFromThisMapToPanelMap();
	}
	
	public SimulationPilot(int startPositionRelativeX, int startPositionRelativeY) {
		SSG.getSimulationPanel().setRobotLocation(this.getCurrentPositionAbsoluteX(), this.getCurrentPositionAbsoluteY(), this.getAlpha());
		mapFile = new File("resources/maze_maps/example_map.txt");
		mapGraph = MapReader.createMapFromFile(mapFile);
		mapGraph.setStartingTileCoordinates(startPositionRelativeX, startPositionRelativeY);
		SSG.getSimulationPanel().setMapGraphConstructed(new MapGraph(startPositionRelativeX,startPositionRelativeY, mapGraph.getTiles().length, mapGraph.getTiles()[0].length));
		setCurrentTileFromThisMapToPanelMap();
		}

	public float getCurrentPositionAbsoluteX() {
		return currentPositionAbsoluteX;
	}
	
	public void setCurrentPositionAbsoluteX(float x) {
		this.currentPositionAbsoluteX = x;
	}
	
	public float getCurrentPositionAbsoluteY() {
		return currentPositionAbsoluteY;
	}

	public void setCurrentPositionAbsoluteY(float y) {
		this.currentPositionAbsoluteY = y;
	}
	
	public int getCurrentPositionRelativeX(){
		return this.getMapGraph().getCurrentTileCoordinates()[0];
	}
	
	public int getCurrentPositionRelativeY(){
		return this.getMapGraph().getCurrentTileCoordinates()[1];
	}
	
	public int getStartPositionRelativeX(){
		return this.getMapGraph().getStartingTileCoordinates()[0];
	}
	
	public int getStartPositionRelativeY(){
		return this.getMapGraph().getStartingTileCoordinates()[1];
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
			
			Orientation currentOrientation; 
			float xOld = this.getCurrentPositionAbsoluteX();
			float yOld = this.getCurrentPositionAbsoluteY();
			
			for (int i = 1; i <= distance; i++) {
				currentOrientation = Orientation.calculateOrientation(xOld, yOld, this.getAlpha());
				
				 xOld = (float) (this.getCurrentPositionAbsoluteX() + i* Math.cos(Math.toRadians(this.getAlpha())));
				 yOld = (float) (this.getCurrentPositionAbsoluteY() + i* Math.sin(Math.toRadians(this.getAlpha())));
				
				if(onEdge(xOld, yOld) && this.getMapGraph().getObstruction(currentOrientation)!=null ){
					this.setCurrentPositionAbsoluteX((float) (this.getCurrentPositionAbsoluteX() + (i-1)*Math.cos(Math.toRadians(this.getAlpha()))));
					this.setCurrentPositionAbsoluteY((float) (this.getCurrentPositionAbsoluteY() + (i-1)*Math.sin(Math.toRadians(this.getAlpha()))));
					System.out.println("Er staat een muur in de weg");
					return;
				}
				
				this.travelToNextTileIfNeeded(xOld, yOld, 1);
				
				SSG.getSimulationPanel().setRobotLocation(xOld, yOld, this.getAlpha());
				try {
					Thread.sleep(speed);
				} catch (InterruptedException e) {
	
				}
			}
			this.setCurrentPositionAbsoluteX((float) (this.getCurrentPositionAbsoluteX() + distance*Math.cos(Math.toRadians(this.getAlpha()))));
			this.setCurrentPositionAbsoluteY((float) (this.getCurrentPositionAbsoluteY() + distance*Math.sin(Math.toRadians(this.getAlpha()))));
		}
		//TODO achteruit
		else if(distance <= 0) {
			
			Orientation currentOrientation; 
			float xOld = this.getCurrentPositionAbsoluteX();
			float yOld = this.getCurrentPositionAbsoluteY();
			
			for (int i = -1; i >= distance; i--) {
				xOld = (float) (this.getCurrentPositionAbsoluteX() + i* Math.cos(Math.toRadians(this.getAlpha())));
				yOld = (float) (this.getCurrentPositionAbsoluteY() + i* Math.sin(Math.toRadians(this.getAlpha())));
				
				currentOrientation = Orientation.getOppositeOrientation(Orientation.calculateOrientation(xOld, yOld, this.getAlpha()));
				
				if(onEdge(xOld, yOld) && this.getMapGraph().getObstruction(currentOrientation)!=null ){
					this.setCurrentPositionAbsoluteX((float) (this.getCurrentPositionAbsoluteX() + (i+1)*Math.cos(Math.toRadians(this.getAlpha()))));
					this.setCurrentPositionAbsoluteY((float) (this.getCurrentPositionAbsoluteY() + (i+1)*Math.sin(Math.toRadians(this.getAlpha()))));
					System.out.println("Er staat een muur in de weg");
					return;
				}
				
				this.travelToNextTileIfNeeded(xOld, yOld, -1);
				
				SSG.getSimulationPanel().setRobotLocation(xOld, yOld, this.getAlpha());
				try {
					Thread.sleep(speed);
				} catch (InterruptedException e) {
	
				}
			}
			this.setCurrentPositionAbsoluteX((float) (this.getCurrentPositionAbsoluteX() + distance*Math.cos(Math.toRadians(this.getAlpha()))));
			this.setCurrentPositionAbsoluteY((float) (this.getCurrentPositionAbsoluteY() + distance*Math.sin(Math.toRadians(this.getAlpha()))));
		}
	}

	/**
	 * returns false if you have bumped a wall, otherwise (you have crossed a line or you are not on a border) true.
	 * @param  direction
	 *         	 1 if you are travelling foreward,
	 *       	-1 if you are travelling backward
	 */
	private void travelToNextTileIfNeeded(float xOld, float yOld, int direction) {
//		System.out.println(xOld%40);
//		System.out.println((xOld%40) > 40-this.getMaxRoundingError() || (xOld%40) < this.getMaxRoundingError());
//		System.out.println(yOld%40);
//		System.out.println((yOld%40) > 40-this.getMaxRoundingError() || (yOld%40) < this.getMaxRoundingError());
		if((xOld%40) > 40-this.getMaxRoundingError() || (xOld%40) < this.getMaxRoundingError())
		{	
			System.out.println("horizontale tile overgang");
			if( ((this.getAlpha() > 270 || this.getAlpha() < 90) && direction == 1) ||
					(this.getAlpha() > 90 && this.getAlpha() < 270 && direction == -1))
			{
				
				this.getMapGraph().moveToNextTile(Orientation.EAST);
				setCurrentTileFromThisMapToPanelMap();
				SSG.getSimulationPanel().getMapGraphConstructed().moveToNextTile(Orientation.EAST);
//					try {
//						this.getMapGraph().moveToNextTile(Orientation.EAST);
//						setCurrentTileFromThisMapToPanelMap();
//						SSG.getSimulationPanel().getMapGraphConstructed().moveToNextTile(Orientation.EAST);
//					} catch (SimulatorCrashedException e1) {
//						reset();
//						System.out.println("heeft gereset en heeft nu coordinaten: ");
//						System.out.println("pilot");
//						System.out.println(mapGraph.getCurrentTileCoordinates()[0]);
//						System.out.println(mapGraph.getCurrentTileCoordinates()[1]);
//						System.out.println("panel");
//						System.out.println(SSG.getSimulationPanel().getMapGraphConstructed().getCurrentTileCoordinates()[0]);
//						System.out.println(SSG.getSimulationPanel().getMapGraphConstructed().getCurrentTileCoordinates()[1]);
//					}
				
			}
			else
			{	
				this.getMapGraph().moveToNextTile(Orientation.WEST);
				setCurrentTileFromThisMapToPanelMap();
				SSG.getSimulationPanel().getMapGraphConstructed().moveToNextTile(Orientation.WEST);
				
//				try {
//					this.getMapGraph().moveToNextTile(Orientation.WEST);
//					setCurrentTileFromThisMapToPanelMap();
//					SSG.getSimulationPanel().getMapGraphConstructed().moveToNextTile(Orientation.WEST);
//			}
//				catch (SimulatorCrashedException e1) {
//					reset();
//					System.out.println("heeft gereset en heeft nu coordinaten: ");
//					System.out.println("pilot");
//					System.out.println(mapGraph.getCurrentTileCoordinates()[0]);
//					System.out.println(mapGraph.getCurrentTileCoordinates()[1]);
//					System.out.println("panel");
//					System.out.println(SSG.getSimulationPanel().getMapGraphConstructed().getCurrentTileCoordinates()[0]);
//					System.out.println(SSG.getSimulationPanel().getMapGraphConstructed().getCurrentTileCoordinates()[1]);
//				}
			}
			this.checkForObstructions();
		}
		if((yOld%40) > 40-this.getMaxRoundingError() || (yOld%40) < this.getMaxRoundingError())
		{
			System.out.println("verticale tile overgang");
			if(this.getAlpha() < 180 && direction == 1 || this.getAlpha()>180 && direction == -1)
			{	
				this.getMapGraph().moveToNextTile(Orientation.SOUTH);
				setCurrentTileFromThisMapToPanelMap();
				SSG.getSimulationPanel().getMapGraphConstructed().moveToNextTile(Orientation.SOUTH);
				
//				try {
//					this.getMapGraph().moveToNextTile(Orientation.SOUTH);
//					setCurrentTileFromThisMapToPanelMap();
//					SSG.getSimulationPanel().getMapGraphConstructed().moveToNextTile(Orientation.SOUTH);
//				} catch (SimulatorCrashedException e) {
//					reset();
//					System.out.println("heeft gereset en heeft nu coordinaten: ");
//					System.out.println("pilot");
//					System.out.println(mapGraph.getCurrentTileCoordinates()[0]);
//					System.out.println(mapGraph.getCurrentTileCoordinates()[1]);
//					System.out.println("panel");
//					System.out.println(SSG.getSimulationPanel().getMapGraphConstructed().getCurrentTileCoordinates()[0]);
//					System.out.println(SSG.getSimulationPanel().getMapGraphConstructed().getCurrentTileCoordinates()[1]);
//				}

			}
			else
				
				
				
			{	
				this.getMapGraph().moveToNextTile(Orientation.NORTH);
				setCurrentTileFromThisMapToPanelMap();
				SSG.getSimulationPanel().getMapGraphConstructed().moveToNextTile(Orientation.NORTH);
				
//				try {
//					this.getMapGraph().moveToNextTile(Orientation.NORTH);
//					setCurrentTileFromThisMapToPanelMap();
//					SSG.getSimulationPanel().getMapGraphConstructed().moveToNextTile(Orientation.NORTH);
//				} catch (SimulatorCrashedException e) {
//					reset();
//					System.out.println("heeft gereset en heeft nu coordinaten: ");
//					System.out.println("pilot");
//					System.out.println(mapGraph.getCurrentTileCoordinates()[0]);
//					System.out.println(mapGraph.getCurrentTileCoordinates()[1]);
//					System.out.println("panel");
//					System.out.println(SSG.getSimulationPanel().getMapGraphConstructed().getCurrentTileCoordinates()[0]);
//					System.out.println(SSG.getSimulationPanel().getMapGraphConstructed().getCurrentTileCoordinates()[1]);
//				}
				
			}
			this.checkForObstructions();
		}
	}
	
	public void checkForObstructions()
	{
		System.out.println("SimulationPilot.checkForObstructions()");
		Orientation currentOrientation = Orientation.calculateOrientation(this.getCurrentPositionAbsoluteX(), this.getCurrentPositionAbsoluteY(), this.getAlpha());
		
		for(int i = 0; i < 8; i++)
		{
			if(!currentOrientation.equals(Orientation.calculateOrientation(this.getCurrentPositionAbsoluteX(), this.getCurrentPositionAbsoluteY(), this.getAlpha())))
			{
				currentOrientation = Orientation.calculateOrientation(this.getCurrentPositionAbsoluteX(), this.getCurrentPositionAbsoluteY(), this.getAlpha());
				if(this.getMapGraph().getObstruction(currentOrientation) == null)
				{
					SSG.getSimulationPanel().addWhiteLine(Orientation.calculateOrientation(this.getCurrentPositionAbsoluteX(), this.getCurrentPositionAbsoluteY(), this.getAlpha()),
							setRelativeToAbsolute(getCurrentPositionRelativeX(),getCurrentPositionRelativeY()));
				}
				else if(this.getMapGraph().getObstruction(currentOrientation) == Obstruction.WALL)
				{
					SSG.getSimulationPanel().addWall(Orientation.calculateOrientation(this.getCurrentPositionAbsoluteX(), this.getCurrentPositionAbsoluteY(), this.getAlpha()),
							setRelativeToAbsolute(getCurrentPositionRelativeX(),getCurrentPositionRelativeY()));
					System.out.println(currentOrientation + ": muur");
				}
				else
				{
					System.out.println("Unidentified Obstruction!");;
				}

				this.rotate(45);
			}
			else
			{
				this.rotate(45);
			}
		}
	}
		
	public void rotate(float alpha) {
		this.setAlpha(ExtMath.addDegree(this.getAlpha(), alpha));
		SSG.getSimulationPanel().setRobotLocation(this.getCurrentPositionAbsoluteX(), this.getCurrentPositionAbsoluteY(), this.getAlpha());
	}
	
	public boolean onEdge(float x, float y){
		return (x%40) > 40-this.getMaxRoundingError() || (x%40) < this.getMaxRoundingError()||
				(y%40) > 40-this.getMaxRoundingError() || (y%40) < this.getMaxRoundingError();
				
	}
	
	public void clear() {
		SSG.getSimulationPanel().clear();
	}	
	
public int[] setAbsoluteToRelative(float x, float y){
	if(x%40 ==0 || y%40 == 0){
		throw new IllegalArgumentException("you are located on an edge.");
	}
	float a = x - startPositionAbsoluteX;
	float b = y - startPositionAbsoluteY;
	int c;
	int d;
	if(a>0){
	c = (int) a/40 + 1;}
	else
		c = (int) a/40 -1;
	if(b>0){
	d = (int) b/40 + 1;}
	else
		d = (int) b/40 -1;
	if(a==0){
		c = 0;
	}
	if(b==0){
		d = 0;
	}
	int[] array = new int[2];
	array[0] = getStartPositionRelativeX() + c;
	array[1] = getStartPositionRelativeY() + d;
	return array;
}


/**
 * geeft de absolute coordinaten van het middelpunt van het vakje
 * 
 * @param x
 * @param y
 * @return
 */
public float[] setRelativeToAbsolute(int x, int y){
	int a = x - getStartPositionRelativeX();
	int b = y - getStartPositionRelativeY();
	float c = a*40;
	float d = b*40;
	float[] array = new float[2];
	array[0] = startPositionAbsoluteX + c;
	array[1] = startPositionAbsoluteX + d;
	return array;
}



public void setCurrentTileFromThisMapToPanelMap(){
	Tile tile = new Tile(mapGraph.getCurrentTile().getyCoordinate(), mapGraph.getCurrentTile().getxCoordinate());
	for(Orientation orientation: Orientation.values()){
		if(mapGraph.getCurrentTile().getEdge(orientation).getObstruction()!=null){
			tile.getEdge(orientation).setObstruction(Obstruction.WALL);
		}
	}
	SSG.getSimulationPanel().getMapGraphConstructed().setTileXY(mapGraph.getCurrentTileCoordinates()[0],mapGraph.getCurrentTileCoordinates()[1], tile);
	
}

public void reset(){
	System.out.println("reset wordt uitgevoerd");
	int[] relativePosition = setAbsoluteToRelative(getCurrentPositionAbsoluteX(), getCurrentPositionAbsoluteY());
	mapGraph.setCurrentTileCoordinates(relativePosition[0], relativePosition[1]);
	SSG.getSimulationPanel().getMapGraphConstructed().setCurrentTileCoordinates(relativePosition[0], relativePosition[1]);
}

//public static void main(String[] args) {
//
//MapGraph mapGraph = MapReader.createMapFromFile(new File("resources/maze_maps/example_map.txt"));
//for(int i = 0 ; i<6 ; i++){
//	for (int j =0; j<4; j++){
//		System.out.println("alle richtingen");
//		Tile tile = mapGraph.getTileXY(i,j);
//		System.out.println(i+ " " + j);
//		System.out.println(tile.getEdge(Orientation.NORTH).isPassable());
//		System.out.println(tile.getEdge(Orientation.EAST).isPassable());
//		System.out.println(tile.getEdge(Orientation.SOUTH).isPassable());
//		System.out.println(tile.getEdge(Orientation.WEST).isPassable());
//		System.out.println(tile.getxCoordinate());
//		System.out.println(tile.getyCoordinate());
//		System.out.println(" ");
//		System.out.println(" ");
//		System.out.println(" ");
//		System.out.println(" ");
//	}
//}
//
//}


}