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

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.Random;
public class SimulationPilot {

	/**
	 * verandert wanneer een nieuwe map wordt ingeladen naar de positie waar
	 * het pijltje staat wanneer de map ingeladen wordt
	 */
	private double startPositionAbsoluteX = 220;
	private double startPositionAbsoluteY = 220;
	/**
	 * coordinaat in het echte assenstelsel van de robot
	 */
	private double currentPositionAbsoluteX = 220;
	private double currentPositionAbsoluteY = 220;


	private double alpha = 270;
	private int speed = 10;
	private SilverSurferGUI SSG = new SilverSurferGUI();
	private File mapFile;
	private MapGraph mapGraph;
	private boolean isRealRobot = false;	
	/**
	 * waarde die afhangt van de robot!
	 */
	private final double detectionDistanceUltrasonicSensorRobot =20;

	private int howManyTimesCheckedOnSameCurrentTileInSameDirection = 0;
	private Orientation previousDirection = null;
	private int currentTileCoordinateXPreviousCheck = -1;
	private int currentTileCoordinateYPreviousCheck = -1;

	public SimulationPilot() {
		SSG.getSimulationPanel().setRobotLocation(this.getCurrentPositionAbsoluteX(), this.getCurrentPositionAbsoluteY(), this.getAlpha());
	}

	public SimulationPilot(int startPositionRelativeX, int startPositionRelativeY) {
		SSG.getSimulationPanel().setRobotLocation(this.getCurrentPositionAbsoluteX(), this.getCurrentPositionAbsoluteY(), this.getAlpha());
	}


	public double getCurrentPositionAbsoluteX() {
		return currentPositionAbsoluteX;
	}

	public void setCurrentPositionAbsoluteX(double x) {
		this.currentPositionAbsoluteX = x;
	}

	public double getCurrentPositionAbsoluteY() {
		return currentPositionAbsoluteY;
	}

	public void setCurrentPositionAbsoluteY(double y) {
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




	public double getAlpha() {
		return alpha;
	}

	public void setAlpha(double alpha) {
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

	public File getMapFile() {
		return this.mapFile;
	}

	public void setMapFile(File mapFile) {
		this.setMapFile(mapFile,0,0);

	}

	public void setMapFile(File mapFile,int xCo,int yCo) {
		this.mapFile = mapFile;
		this.setMapGraph(MapReader.createMapFromFile(mapFile,xCo,yCo));
		this.getSSG().updateCoordinates("Simulator (" + (this.getCurrentPositionAbsoluteX()) + " , "
				+ (this.getCurrentPositionAbsoluteY())+ " , "
				+ (int) this.getAlpha() + "°, Map: " + this.getMapString() + ")");
		this.startPositionAbsoluteX = getCurrentPositionAbsoluteX();
		this.startPositionAbsoluteY = getCurrentPositionAbsoluteY();
		this.getSSG().getSimulationPanel().clearTotal();
	}

	public SilverSurferGUI getSSG(){
		return this.SSG;
	}

	public MapGraph getMapGraph() {
		return this.mapGraph;
	}

	public String getMapString() {
		if(this.getMapGraph() == null)
		{
			return "/";
		}
		return this.mapFile.getName();
	}

	/**
	 * Use this method only intern! If you want to change the map, use the setMapFile-method!
	 * only used when you delete the map
	 */
	public void setMapGraph(MapGraph mapGraph) {
		if(mapGraph == null){
			this.mapGraph = null;
			this.getSSG().getSimulationPanel().clearTotal();
		}
		this.mapGraph = mapGraph;
	}

	/**
	 * Dit is de marge ten opzichte van de edge
	 * wordt gebruikt in travel : wanneer de robot op 1 pixel verwijderd van de edge is
	 * begint hij zijn currentPositionRelative aan te passen afhankelijk van de
	 * currentPositionAbsolute
	 * dit gebeurt in setCurrentTileCoordinates
	 */
	private double getEdgeMarge()
	{
		return (double) 1;
	}


	public void travel(double distance) {
		double xOld = this.getCurrentPositionAbsoluteX();
		double yOld = this.getCurrentPositionAbsoluteY();
		Orientation currentOrientation; 
		SimulationSensorData.setDriving(true);

		if(distance >= 0) {

			for (int i = 1; i <= distance; i++) {
				//dit kan veranderen wanneer je over een edge gaat
				currentOrientation = Orientation.calculateOrientation(xOld, yOld, this.getAlpha());

				xOld = (double) (this.getCurrentPositionAbsoluteX() + i* Math.cos(Math.toRadians(this.getAlpha())));
				yOld = (double) (this.getCurrentPositionAbsoluteY() + i* Math.sin(Math.toRadians(this.getAlpha())));

				if(mapGraph != null){

					if(onEdge(xOld, yOld) && checkForObstructions() ){
						//deze if wordt uitgevoerd wanneer er een wall in de weg staat
						this.setCurrentPositionAbsoluteX((double) (this.getCurrentPositionAbsoluteX() + (i-1)*Math.cos(Math.toRadians(this.getAlpha()))));
						this.setCurrentPositionAbsoluteY((double) (this.getCurrentPositionAbsoluteY() + (i-1)*Math.sin(Math.toRadians(this.getAlpha()))));
						this.getSSG().updateStatus();
						
						System.out.println("Er staat een muur in de weg");
						return;
					}


					this.travelToNextTileIfNeeded(xOld, yOld);

					//dit checkt of hij dicht genoeg bij een edge is om te checken of er een muur staat
					if(ExtMath.calculateDistanceFromPointToEdge(xOld, yOld, getAlpha()) < detectionDistanceUltrasonicSensorRobot){
						if(checkForObstructions()){
							addWall();}
					}
				}

				SSG.getSimulationPanel().setRobotLocation(xOld, yOld, this.getAlpha());
				try{Thread.sleep(speed);}
				catch (InterruptedException e) {}
			}
			this.setCurrentPositionAbsoluteX((double) (this.getCurrentPositionAbsoluteX() + distance*Math.cos(Math.toRadians(this.getAlpha()))));
			this.setCurrentPositionAbsoluteY((double) (this.getCurrentPositionAbsoluteY() + distance*Math.sin(Math.toRadians(this.getAlpha()))));

			this.getSSG().updateStatus();
		}


		else if(distance <= 0) {

			for (int i = -1; i >= distance; i--) {

				xOld = (double) (this.getCurrentPositionAbsoluteX() + i* Math.cos(Math.toRadians(this.getAlpha())));
				yOld = (double) (this.getCurrentPositionAbsoluteY() + i* Math.sin(Math.toRadians(this.getAlpha())));

				currentOrientation = Orientation.calculateOrientation(xOld, yOld, this.getAlpha()).getOppositeOrientation();

				if(mapGraph != null){

					if(onEdge(xOld, yOld) && this.getMapGraph().getObstruction(currentOrientation)!=null ){
						this.setCurrentPositionAbsoluteX((double) (this.getCurrentPositionAbsoluteX() + (i+1)*Math.cos(Math.toRadians(this.getAlpha()))));
						this.setCurrentPositionAbsoluteY((double) (this.getCurrentPositionAbsoluteY() + (i+1)*Math.sin(Math.toRadians(this.getAlpha()))));

						this.getSSG().updateStatus();

						System.out.println("Er staat een muur in de weg");
						return;
					}

					this.travelToNextTileIfNeeded(xOld, yOld);

				}

				SSG.getSimulationPanel().setRobotLocation(xOld, yOld, this.getAlpha());
				try {
					Thread.sleep(speed);
				} catch (InterruptedException e) {}

				this.setCurrentPositionAbsoluteX((double) (this.getCurrentPositionAbsoluteX() + distance*Math.cos(Math.toRadians(this.getAlpha()))));
				this.setCurrentPositionAbsoluteY((double) (this.getCurrentPositionAbsoluteY() + distance*Math.sin(Math.toRadians(this.getAlpha()))));

				this.getSSG().updateStatus();
			}
		}
		SimulationSensorData.setDriving(false);

	}

	/**
	 * Checkt of het een edge is gepasseerd 
	 * zoja past hij zijn currenttileCoordinates aan
	 */
	private void travelToNextTileIfNeeded(double xOld, double yOld) {
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
	public boolean checkForObstructions()
	{
		Orientation currentOrientation = Orientation.calculateOrientation(this.getCurrentPositionAbsoluteX(), this.getCurrentPositionAbsoluteY(), this.getAlpha());

		Point2D point = ExtMath.calculateWallPoint(currentOrientation,
				getCurrentPositionAbsoluteX(),getCurrentPositionAbsoluteY());

		double XOther = point.getX() + currentOrientation.getOtherPointLine()[0];
		double YOther =	point.getY() + currentOrientation.getOtherPointLine()[1];

		Double distance = Line2D.ptSegDist(point.getX(), point.getY(), XOther, YOther, getCurrentPositionAbsoluteX(),getCurrentPositionAbsoluteY());
		if(distance > 21 ){
			return false;
		}

		if (this.getMapGraph().getObstruction(currentOrientation) == Obstruction.WALL){
			getSSG().getInformationBuffer().addUltraSensorInfo(distance.intValue());
			return true;
		}
		
		return false;
	}


	public void addWall(){

		Orientation currentOrientation = Orientation.calculateOrientation(this.getCurrentPositionAbsoluteX(), this.getCurrentPositionAbsoluteY(), this.getAlpha());

		if(!(getCurrentPositionRelativeX() == currentTileCoordinateXPreviousCheck &&
				getCurrentPositionRelativeY() == currentTileCoordinateYPreviousCheck
				&& previousDirection == currentOrientation)){
			howManyTimesCheckedOnSameCurrentTileInSameDirection = 0;
			currentTileCoordinateXPreviousCheck = getCurrentPositionRelativeX();
			currentTileCoordinateYPreviousCheck = getCurrentPositionRelativeY();
			previousDirection = currentOrientation;
			return;
		}

		else if(howManyTimesCheckedOnSameCurrentTileInSameDirection != 4){
			howManyTimesCheckedOnSameCurrentTileInSameDirection++;
			return;
		}

		else if(this.getMapGraph().getObstruction(currentOrientation) == Obstruction.WALL)
		{
			SSG.getSimulationPanel().addWall(currentOrientation,
					getCurrentPositionAbsoluteX(),getCurrentPositionAbsoluteY());
			howManyTimesCheckedOnSameCurrentTileInSameDirection = 0;
		}
		else
		{
			//roept addwhiteline op, deze methode verwijdert de muur terug uit het panel
			SSG.getSimulationPanel().addWhiteLine(currentOrientation,
					getCurrentPositionAbsoluteX(),getCurrentPositionAbsoluteY());
			howManyTimesCheckedOnSameCurrentTileInSameDirection = 0;
		}
	}

	public void rotate(double alpha) {
		SimulationSensorData.setDriving(true);
		this.setAlpha(ExtMath.addDegree(this.getAlpha(), alpha));
		this.getSSG().getSimulationPanel().setRobotLocation(this.getCurrentPositionAbsoluteX(), this.getCurrentPositionAbsoluteY(), this.getAlpha());
		
		this.getSSG().updateStatus();

		//weer een checkForObstructions
		if(mapGraph != null){
			if(ExtMath.calculateDistanceFromPointToEdge(getCurrentPositionAbsoluteX(), getCurrentPositionAbsoluteY(), ExtMath.addDegree(this.getAlpha(), alpha)) < detectionDistanceUltrasonicSensorRobot){
				if(checkForObstructions()){
					addWall();
				}}
		}
		SimulationSensorData.setDriving(false);

	}


	public void clear() {
		this.getSSG().getSimulationPanel().clear();
	}

	public void updateArc(int distance){
		getSSG().getSimulationPanel().updateArc(getCurrentPositionAbsoluteX(), getCurrentPositionAbsoluteY(), getAlpha(), distance);
	}

	public void setRealRobot(boolean isRealRobot) {
		this.isRealRobot = isRealRobot;
	}
	public boolean isRealRobot(){
		return isRealRobot;
	}
	
	/**
	 * Returns a number from a normal districution that represents a lightsensor value.
	 * 
	 * nog niet af!! dit is slechts een schets van hoe de methode moet worden.
	 */
	public int getLightSensorValue()
	{
		if(this.isRealRobot())
		{
			return SilverSurferGUI.getInformationBuffer().getLatestLightSensorInfo();
		}
		else
		{
			// initialisation
			Random random = new Random();
			double mean = 0;
			double standardDeviation = 1;
			
			// check on which sort of underground your are standing
			// and adjust the mean and standardDeviation accordingly
			if(onEmptyTile(getCurrentPositionAbsoluteX(), getCurrentPositionAbsoluteY()))
			{
				mean = SimulationSensorData.getMEmptyPanelLS();
				standardDeviation = SimulationSensorData.getSDEmptyPanelLS();
			}
			else if(onWhiteLine(getCurrentPositionAbsoluteX(), getCurrentPositionAbsoluteY()))
			{
				mean = SimulationSensorData.getMWhiteLineLS();
				standardDeviation = SimulationSensorData.getSDWhiteLineLS();
			}
			else if(onBarcodeTile(getCurrentPositionAbsoluteX(), getCurrentPositionAbsoluteY()))
			{
				int color = this.getMapGraph().getContentCurrentTile().getColorValue(getCurrentPositionAbsoluteX()%40, getCurrentPositionAbsoluteY()%40);
				mean = SimulationSensorData.getMBarcodeTileLS(color);
				standardDeviation = SimulationSensorData.getSDBarcodeTileLS(color);
			}
			
			System.out.println((mean + (random.nextGaussian() * standardDeviation)));
			return (int) Math.round(mean + (random.nextGaussian() * standardDeviation));
		}
	}
	
	public int getUltraSensorValue()
	{
		if(this.isRealRobot())
		{
			return SilverSurferGUI.getInformationBuffer().getLatestUltraSensorInfo();

		}
		else
		{
			Random random = new Random();

			double mean = this.calculateDistanceToWall();
			double standardDeviation = SimulationSensorData.getSDUS();
			
			return (int) Math.round(mean + (random.nextGaussian() * standardDeviation));
		}
	}
	
	public boolean getTouchSensor1Value()
	{
		if(this.isRealRobot())
		{
			return SilverSurferGUI.getInformationBuffer().getLatestTouchSensor1Info();
		}
		else
		{
			// to be implemented!!
			return false;
		}
	}
		
	public boolean getTouchSensor2Value()
	{
		if(this.isRealRobot())
		{
			return SilverSurferGUI.getInformationBuffer().getLatestTouchSensor2Info();
		}
		else
		{
			// to be implemented!!
			return false;
		}
	}
	
	private double calculateDistanceToWall()
	{
		// to be implemented!!
		return 0;
	}

	public void allignOnWhiteLine(){
		Orientation orientation = Orientation.calculateOrientation(getCurrentPositionAbsoluteX(),
				getCurrentPositionAbsoluteY(), getAlpha());

		while(!onEdge(getCurrentPositionAbsoluteX(), getCurrentPositionAbsoluteY())){
			travel(1);
		}

		double requiredAlpha = (double) orientation.getRightAngle();

		//		double requiredAlpha = Math.round(getAlpha()/90) * 90;
		while (!(getAlpha()<requiredAlpha+1 && getAlpha()>requiredAlpha-1) ){
			if(getAlpha() < requiredAlpha){
				rotate(1);
			}
			else
				rotate(-1);
		}
		for(int i = 0; i<6; i++)
			travel(-1);
	}

	public void allignOnWalls() {
		//		
		//		rotate(90);
		//		
		//		Orientation orientation = Orientation.calculateOrientation(getCurrentPositionAbsoluteX(), getCurrentPositionAbsoluteY(), getAlpha());
		//		
		//		if(this.getMapGraph().getObstruction(orientation) == Obstruction.WALL){
		//			
		//			Point2D point = ExtMath.calculateWallPoint(orientation,
		//					getCurrentPositionAbsoluteX(),getCurrentPositionAbsoluteY());
		//			
		//			double XOther = point.getX() + Orientation.getOtherPointLine(orientation)[0];
		//			double YOther =	point.getY() + Orientation.getOtherPointLine(orientation)[1];
		//			
		//			double bla = Line2D.ptSegDist(point.getX(), point.getY(), XOther, YOther, getCurrentPositionAbsoluteX(),getCurrentPositionAbsoluteY());
		//			System.out.println(bla);
		//			if(bla >= 20){
		//				travel(bla - 20);
		//			}
		//			else{
		//				for(int i = 0; i<20-bla; i++)
		//					travel(-1);
		//			}
		//			
		//		}
		//		
		//		rotate(180);
		//		
		//		orientation = Orientation.calculateOrientation(getCurrentPositionAbsoluteX(), getCurrentPositionRelativeY(), getAlpha());
		//		
		//		if(this.getMapGraph().getObstruction(orientation) == Obstruction.WALL){
		//			
		//			Point2D point = ExtMath.calculateWallPoint(orientation,
		//					getCurrentPositionAbsoluteX(),getCurrentPositionAbsoluteY());
		//			
		//			double XOther = point.getX() + Orientation.getOtherPointLine(orientation)[0];
		//			double YOther =	point.getY() + Orientation.getOtherPointLine(orientation)[1];
		//			
		//			double bla = Line2D.ptSegDist(point.getX(), point.getY(), XOther, YOther, getCurrentPositionAbsoluteX(),getCurrentPositionAbsoluteY());
		//			if(bla >= 20){
		//				travel(bla - 20);
		//			}
		//			else{
		//				for(int i = 0; i<20-bla; i++)
		//					travel(-1);
		//			}
		//			
		//		}
		//		
		//		rotate(90);
		//		
	}

	/**
	 * checkt of de robot zich binnen de marge van een edge bevindt
	 */
	public boolean onEdge(double x, double y){
		return (x%40) > 40-this.getEdgeMarge() || (x%40) < this.getEdgeMarge()||
		(y%40) > 40-this.getEdgeMarge() || (y%40) < this.getEdgeMarge();

	}

	/**
	 * True if the robot is on an edge and this edge is not a wall
	 */
	public boolean onWhiteLine(double x, double y){
		//System.out.println("w: " + (this.onEdge(x,y)  && (this.getMapGraph() == null || this.getMapGraph().getObstruction(Orientation.calculateOrientation(x, y, this.getAlpha())) != Obstruction.WALL)));
		return this.onEdge(x,y)
		       && (this.getMapGraph() == null
		    		   || this.getMapGraph().getObstruction(Orientation.calculateOrientation(x, y, this.getAlpha())) != Obstruction.WALL);

	}
	
	/**
	 * True if the robot is not on an edge, but on a tile without a content.
	 */
	public boolean onEmptyTile(double x, double y){
		//System.out.println("e: " + (!this.onEdge(x,y) && (this.getMapGraph() == null || this.getMapGraph().getContentCurrentTile() == null)));
		return (!this.onEdge(x,y) && this.getMapGraph() == null) || (!this.onEdge(x,y) && this.getMapGraph().getContentCurrentTile() == null) ;

	}
	
	/**
	 * True if the robot is not on an edge, but on a tile containing a barcode.
	 */
	public boolean onBarcodeTile(double x, double y){
		if(this.getMapGraph() == null)
		{
		//	System.out.println("b: /");
			return false;
		}
		else
		{
		//	System.out.println("b: " + (!this.onEdge(x,y) && (this.getMapGraph().getContentCurrentTile() instanceof Barcode)));
			return !this.onEdge(x,y) && (this.getMapGraph().getContentCurrentTile() instanceof Barcode) ;
		}
	}

	//zet een double om in een veelvoud van 40 kleiner dan de double (ook bij negatief
	//maar doet normaal niet ter zake aangezien de coordinaten in het echte coordinatensysteem 
	//niet negatief	kunnen zijn
	public static int setToMultipleOf40(double a){
		return (int) (Math.floor(a/40)*40);
	}

	/**
	 * Deze methode zet de coordinaten van het echte systeem om 
	 * in de coordinaten van de matrix
	 */
	public int[] setAbsoluteToRelative(double x, double y){
		double a = x - setToMultipleOf40(startPositionAbsoluteX);
		double b = y - setToMultipleOf40(startPositionAbsoluteY);
		int c;
		int d;
		c = (int) Math.floor(a/40);
		d = (int) Math.floor(b/40);

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
	public double[] setRelativeToAbsolute(int x, int y){
		int a = x - getStartPositionRelativeX();
		int b = y - getStartPositionRelativeY();
		double c = a*40;
		double d = b*40;
		double[] array = new double[2];
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
	public void setCurrentTileCoordinates(MapGraph map, double xOld, double yOld){
		int[] relativePosition = setAbsoluteToRelative(xOld, yOld);
		map.setCurrentTileCoordinates(relativePosition[0], relativePosition[1]);
	}

}
