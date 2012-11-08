/**
 * ff algemene uitleg over het coordinatenstelsel en dingen waar je best rekening meehoudt:
 * het veld tiles[][] in mapGraph stelt dus een matrix voor met tiles die dus ook 
 * zo genummerd worden dus dit betekent tiles[0][1] is het vakje op rij 0 kolom 1
 * dit is anders als in het coordinatenstelsel van de panel omdat hier x horizontaal is 
 * en y verticaal en dus 0,1 het vakje voorstelt in de 2de rij en 1ste kolom.
 * deze coordinaten zijn opgeslagen in de tile zelf. dus als ge getx oproept van tile krijgt
 * ge de coordinaten in het echte assenstelsel, en als ge tiles[][].getcurrentXposition
 * oproept, krijgt ge de x coordinaat waarmee die in de matrix staat. deze 2 coordinaten zijn dus
 * eigenlijk gewoon het omgekeerde van elkaar.
 * 
 * de stand van de robot in het echte coordinatenstelsel staat vast, als ge dus een locatie
 * ingeeft waarop de robot moet starten in de map, verplaatst ge deze map dus eig ten opzichte
 * van uw robot en niet de robot ten opzichte van de map
 * dit is belangrijk om het bereik te weten! het echte coordinatenstelsel gaat niet negatief
 * dus miss moeten we de robot verder positioneren zodat de map groter kan zijn, das natuurlijk
 * ook rekeninghoudend met de pech da ze de robot laten vertrekken van rechtsvanonder
 */

package simulator;

import gui.SilverSurferGUI;
import mapping.*;

import java.io.File;
public class SimulationPilot {
	
	/**
	 * blijft vast
	 */
	private final float startPositionAbsoluteX = 220;
	private final float startPositionAbsoluteY = 220;
	/**
	 * coordinaat in het echte assenstelsel van de robot
	 */
	private float currentPositionAbsoluteX = 220;
	private float currentPositionAbsoluteY = 220;


	private float alpha = 270;
	private int speed = 10;
	private SilverSurferGUI SSG = new SilverSurferGUI();
	private File mapFile;
	private MapGraph mapGraph;
	
	/**
	 * waarde die afhangt van de robot!
	 */
	private final float detectionDistanceUltrasonicSensorRobot =20;
	
	
	public SimulationPilot() {
		SSG.getSimulationPanel().setRobotLocation(this.getCurrentPositionAbsoluteX(), this.getCurrentPositionAbsoluteY(), this.getAlpha());
		mapFile = new File("resources/maze_maps/example_map.txt");
		mapGraph = MapReader.createMapFromFile(mapFile);
	}
	
	//TODO
	//Hier zou nog een knop op de GUI moeten gemaakt worden die deze startpositie van
	//de robot op de map ingeeft.
	public SimulationPilot(int startPositionRelativeX, int startPositionRelativeY) {
		SSG.getSimulationPanel().setRobotLocation(this.getCurrentPositionAbsoluteX(), this.getCurrentPositionAbsoluteY(), this.getAlpha());
		mapFile = new File("resources/maze_maps/example_map.txt");
		mapGraph = MapReader.createMapFromFile(mapFile);
		mapGraph.setStartingTileCoordinates(startPositionRelativeX, startPositionRelativeY);
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
	
	/**
	 * de relatieve coordinaat is dus de coordinaat in de matrix!
	 */
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
	
	/**
	 * Dit is de marge ten opzichte van de edge
	 * wordt gebruikt in travel : wanneer de robot op 1 pixel verwijderd van de edge is
	 * begint hij zijn currentPositionRelative aan te passen afhankelijk van de
	 * currentPositionAbsolute
	 * dit gebeurt in setCurrentTileCoordinates
	 */
	private float getEdgeMarge()
	{
		return (float) 1;
	}
	
	
	public void travel(float distance) {
		float xOld = this.getCurrentPositionAbsoluteX();
		float yOld = this.getCurrentPositionAbsoluteY();
		Orientation currentOrientation; 
		
		if(distance >= 0) {
			
			for (int i = 1; i <= distance; i++) {
				//dit kan veranderen wanneer je over een edge gaat
				currentOrientation = Orientation.calculateOrientation(xOld, yOld, this.getAlpha());

				xOld = (float) (this.getCurrentPositionAbsoluteX() + i* Math.cos(Math.toRadians(this.getAlpha())));
				yOld = (float) (this.getCurrentPositionAbsoluteY() + i* Math.sin(Math.toRadians(this.getAlpha())));

				if(onEdge(xOld, yOld) && this.getMapGraph().getObstruction(currentOrientation)!=null ){
					//deze if wordt uitgevoerd wanneer er een wall in de weg staat
					this.setCurrentPositionAbsoluteX((float) (this.getCurrentPositionAbsoluteX() + (i-1)*Math.cos(Math.toRadians(this.getAlpha()))));
					this.setCurrentPositionAbsoluteY((float) (this.getCurrentPositionAbsoluteY() + (i-1)*Math.sin(Math.toRadians(this.getAlpha()))));
					System.out.println("SimulationPilot.travel() : Er staat een muur in de weg");
					return;
				}
				
				this.travelToNextTileIfNeeded(xOld, yOld);

				//dit checkt of hij dicht genoeg bij een edge is om te checken of er een muur staat
				if(ExtMath.calculateDistanceFromPointToEdge(xOld, yOld, getAlpha()) < detectionDistanceUltrasonicSensorRobot){
					checkForObstructions();
				}
				
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
	 * Checkt of het een edge is gepasseerd 
	 * zoja past hij zijn currenttileCoordinates aan
	 */
	private void travelToNextTileIfNeeded(float xOld, float yOld) {
		if((xOld%40) > 40-this.getEdgeMarge() || (xOld%40) < this.getEdgeMarge() ||
				(yOld%40) > 40-this.getEdgeMarge() || (yOld%40) < this.getEdgeMarge())
		{			
				setCurrentTileCoordinates(mapGraph, xOld, yOld);	
		}
		else
			return;
	}


	/**
	 * deze wordt opgeroepen in travel en rotate, 
	 *
	 */
	public void checkForObstructions()
	{
		System.out.println("SimulationPilot.checkForObstructions()");
		Orientation currentOrientation = Orientation.calculateOrientation(this.getCurrentPositionAbsoluteX(), this.getCurrentPositionAbsoluteY(), this.getAlpha());

		if(this.getMapGraph().getObstruction(currentOrientation) == Obstruction.WALL)
		{
			SSG.getSimulationPanel().addWall(currentOrientation,
					getCurrentPositionAbsoluteX(),getCurrentPositionAbsoluteY());
		}
		else
		{
			//roept addwhiteline op, deze methode verwijdert de muur terug uit het panel
				SSG.getSimulationPanel().addWhiteLine(currentOrientation,
						getCurrentPositionAbsoluteX(),getCurrentPositionAbsoluteY());
		}
	}


		
	public void rotate(float alpha) {
		this.setAlpha(ExtMath.addDegree(this.getAlpha(), alpha));
		SSG.getSimulationPanel().setRobotLocation(this.getCurrentPositionAbsoluteX(), this.getCurrentPositionAbsoluteY(), this.getAlpha());
		//weer een checkForObstructions
		if(ExtMath.calculateDistanceFromPointToEdge(getCurrentPositionAbsoluteX(), getCurrentPositionAbsoluteY(), ExtMath.addDegree(this.getAlpha(), alpha)) < detectionDistanceUltrasonicSensorRobot){
			checkForObstructions();
		}
	}

	
	public void clear() {
		SSG.getSimulationPanel().clear();
	}
	
	/**
	 * checkt of de robot zich binnen de marge van een edge bevindt
	 */
	public boolean onEdge(float x, float y){
		return (x%40) > 40-this.getEdgeMarge() || (x%40) < this.getEdgeMarge()||
				(y%40) > 40-this.getEdgeMarge() || (y%40) < this.getEdgeMarge();
				
	}

	//zet een float om in een veelvoud van 40 kleiner dan de float (ook bij negatief
	//maar doet normaal niet ter zake aangezien de coordinaten in het echte coordinatensysteem 
	//niet negatief	kunnen zijn
		public int setToMultipleOf40(float a){
			return (int) (Math.floor(a/40)*40);
		}
		
		/**
		 * Deze methode zet de coordinaten van het echte systeem om 
		 * in de coordinaten van de matrix
		 */
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
		//d en c wisselen om door de echte naar relatieve coordinatensysteem transformatie
		array[0] = getStartPositionRelativeX() + d;
		array[1] = getStartPositionRelativeY() + c;
		return array;
	}


	/**
	 * Deze methode wordt voor het moment nog nergens gebruikt dus ook niet echt veel getest
	 * kunnen fouten inzitten
	 * geeft het middelpunt van het vak weer da overeenkomt met de coordinaten van 
	 * de matrix die je moet ingeven als argumenten
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

	/**
	 * zet dus de map terug op zijn juiste currenttilecoorinates berekend uit
	 * de xOld en yOld
	 * xOld en yOld mogen eig enkel de huidige positie voorstellen!!!!!!!!!!!!!!
	 * moeten hier ingegeven worden omdat bij de travelmethode je de currentabsoluteposition
	 * pas terug juist zet op het einde van de lus
	 */
	public void setCurrentTileCoordinates(MapGraph map, float xOld, float yOld){
		System.out.println("setCurrentCoordinates wordt uitgevoerd");
		int[] relativePosition = setAbsoluteToRelative(xOld, yOld);
		System.out.println(relativePosition[0] + " en " + relativePosition[1] );
		map.setCurrentTileCoordinates(relativePosition[0], relativePosition[1]);
	}

	}
