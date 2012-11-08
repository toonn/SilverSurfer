package simulator;

import gui.SilverSurferGUI;
import mapping.*;
import java.io.File;
public class SimulationPilot {
	
	private final float startPositionAbsoluteX = 220;
	private final float startPositionAbsoluteY = 220;
	private float currentPositionAbsoluteX = 220;
	private float currentPositionAbsoluteY = 220;


	private float alpha = 270;
	private int speed = 10;
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
		if(speed == 48)
			return 4;
		else if(speed == 58)
			return 3;
		else if(speed == 86)
			return 2;
		else
			return 1;
	}
	
	public void setSpeed(int speed) {
		if(speed == 1)
			this.speed = 194;
		else if(speed == 2)
			this.speed = 86;
		else if(speed == 3)
			this.speed = 58;
		else
			this.speed = 48;
	}

	public MapGraph getMapGraph() {
		return this.mapGraph;
	}
	public void setMapGraph(MapGraph mapGraph) {
		this.mapGraph = mapGraph;
	}
	
	private float getMaxRoundingError()
	{
		return (float) 1;
	}
	
	public void travel(float distance) {
		float xOld = this.getCurrentPositionAbsoluteX();
		float yOld = this.getCurrentPositionAbsoluteY();
		Orientation currentOrientation; 
		
		if(distance >= 0) {
			
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
				
				this.travelToNextTileIfNeeded(xOld, yOld);

				
				SSG.getSimulationPanel().setRobotLocation(xOld, yOld, this.getAlpha());
				try{Thread.sleep(speed);}
				catch (InterruptedException e) {}
			}
			this.setCurrentPositionAbsoluteX((float) (this.getCurrentPositionAbsoluteX() + distance*Math.cos(Math.toRadians(this.getAlpha()))));
			this.setCurrentPositionAbsoluteY((float) (this.getCurrentPositionAbsoluteY() + distance*Math.sin(Math.toRadians(this.getAlpha()))));

		}
		
		
		else if(distance <= 0) {
			
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
				
				this.travelToNextTileIfNeeded(xOld, yOld);
				
				SSG.getSimulationPanel().setRobotLocation(xOld, yOld, this.getAlpha());
				try {
					Thread.sleep(speed);
				} catch (InterruptedException e) {}
			
			this.setCurrentPositionAbsoluteX((float) (this.getCurrentPositionAbsoluteX() + distance*Math.cos(Math.toRadians(this.getAlpha()))));
			this.setCurrentPositionAbsoluteY((float) (this.getCurrentPositionAbsoluteY() + distance*Math.sin(Math.toRadians(this.getAlpha()))));
	
		}
	}
	}

	/**
	 * returns false if you have bumped a wall, otherwise (you have crossed a line or you are not on a border) true.
	 */
	private void travelToNextTileIfNeeded(float xOld, float yOld) {
		if((xOld%40) > 40-this.getMaxRoundingError() || (xOld%40) < this.getMaxRoundingError() ||
				(yOld%40) > 40-this.getMaxRoundingError() || (yOld%40) < this.getMaxRoundingError())
		{			
				setCurrentTileCoordinates(mapGraph, xOld, yOld);
				setCurrentTileFromThisMapToPanelMap();
				setCurrentTileCoordinates(SSG.getSimulationPanel().getMapGraphConstructed(), xOld, yOld);	
		}
		else
			return;
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
				//TODO if(this.getMapGraph().getObstruction(currentOrientation) == Obstruction.WHITE_LINE)
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
			}
			this.rotate(45);
		}
	}

		
	public void rotate(float alpha) {
		this.setAlpha(ExtMath.addDegree(this.getAlpha(), alpha));
		SSG.getSimulationPanel().setRobotLocation(this.getCurrentPositionAbsoluteX(), this.getCurrentPositionAbsoluteY(), this.getAlpha());
	}

	
	public void clear() {
		SSG.getSimulationPanel().clear();
	}
	
	public boolean onEdge(float x, float y){
		return (x%40) > 40-this.getMaxRoundingError() || (x%40) < this.getMaxRoundingError()||
				(y%40) > 40-this.getMaxRoundingError() || (y%40) < this.getMaxRoundingError();
				
	}

	//steeds naar beneden
		public int setToMultipleOf40(float a){
			return (int) (Math.floor(a/40)*40);
		}
		
	public int[] setAbsoluteToRelative(float x, float y){
		System.out.println(x + " en " + y);
		float a = x - setToMultipleOf40(startPositionAbsoluteX);
		float b = y - setToMultipleOf40(startPositionAbsoluteY);
		int c;
		int d;
		if(a>0){
		c = (int) Math.floor(a/40);}
		else
			c = (int) Math.ceil(a/40);
		if(b>0){
		d = (int) Math.floor(b/40);}
		else
			d = (int) Math.ceil(b/40);
		if(a==0){
			c = 0;
		}
		if(b==0){
			d = 0;
		}
		int[] array = new int[2];
		array[0] = getStartPositionRelativeX() + d;
		array[1] = getStartPositionRelativeY() + c;
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

	public void setCurrentTileCoordinates(MapGraph map, float xOld, float yOld){
		System.out.println("setCurrentCoordinates wordt uitgevoerd");
		int[] relativePosition = setAbsoluteToRelative(xOld, yOld);
		System.out.println(relativePosition[0] + " en " + relativePosition[1] );
		map.setCurrentTileCoordinates(relativePosition[0], relativePosition[1]);
	}



	public static void main(String[] args) {
		float a = 239;
		System.out.println(Math.floor(a/40)*40);
		System.out.println(Math.floor(-a/40)*40);
		}


	}