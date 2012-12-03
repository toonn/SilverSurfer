package simulator;

import gui.SilverSurferGUI;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.*;

import datastructures.Bag;
import datastructures.Tuple;


import mapping.*;

public class SimulationJPanel extends JPanel implements Runnable {

	private SilverSurferGUI SSG;
	private SimulationPilot simulationPilot;
	private MapGraph mapGraphConstructed;
	private double scalingfactor = 1;
	private int shiftToTheRight = 0;
	private int shiftDown = 0;

	/**
	 * Images die de muur getekend worden (komende van de 8bit Pokemon games!)
	 */
	private BufferedImage verticalWallImage;
	private BufferedImage horizontalWallImage;
	/**
	 * 2 driehoeken die elkaar afwisselen om afgebeeld te worden
	 * de ene wordt afgebeeld terwijl de andere zijn nieuwe coordinaten berekend worden
	 * neem Triangle(220,220,270) ipv (0,0,0) om lelijke dot op 0,0 te voorkomen.
	 */
	private Triangle triangle1 = new Triangle(5*getSizeTile() + getSizeTile()/2,5*getSizeTile() + getSizeTile()/2,270, scalingfactor);
	private Triangle triangle2 = new Triangle(5*getSizeTile() + getSizeTile()/2,5*getSizeTile() + getSizeTile()/2,270, scalingfactor);
	/**
	 * geeft het getal van de driehoek die afgebeeld wordt
	 */
	private int isVisible = 1;
	/**
	 * is true als de coordinaten van de driehoek die niet afgebeeld wordt, berekend zijn.
	 */
	private boolean isUpdated = false;

	private Arc2D sonarArc = new Arc2D.Double();
	private Ellipse2D undergroundCircle = new Ellipse2D.Double(); 

	private Vector<Shape> shapes = new Vector<Shape>();

	/**
	 * Houdt een map bij met coordinaten die verwijzen naar de muur die erop staat
	 * de positie van de muur ten opzichte van de coordinaten staat uitgelegd in de klasse
	 * wall
	 */
	private HashMap<Point2D, Wall> walls = new HashMap<Point2D, Wall>();
	
	/**
	 * A bag doesn't have any ordering, just used for iterating. 
	 * The placing of these 'barcodes' is specified in the seperate rectangles.
	 */
	private Bag<Tuple<String, Rectangle2D[]>> barcodes = new Bag<Tuple<String, Rectangle2D[]>>();

	public SimulationJPanel()
	{
		mapGraphConstructed = new MapGraph();
		
		try
		{
			verticalWallImage = ImageIO.read(new File("resources/wallImages/verticalwall2.png"));
			horizontalWallImage = ImageIO.read(new File("resources/wallImages/horizontalwall2.png"));
		}
		catch (IOException e) {
			System.out.println("1");
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("2");
		}

		shapes.add(triangle1);
		shapes.add(triangle2);
		
	}
	
	public void run() {
		while(true) {
		try {
		Thread.sleep(1000);
		} catch(InterruptedException ee) {}
		this.repaint();
		}
		}

	public void addCircle(double x, double y, double degrees) {
		// remove the last triangle and draw little circles to indicate the path
		if(shapes.size()>0)
		{ 	
			double oldX = this.getVisibleTriangle().getGravityCenterX();
			double oldY = this.getVisibleTriangle().getGravityCenterY();

			// add a bigger circle where the robot starts
			if(shapes.size()<3)
			{
				double diam = scalingfactor * 5;
				Shape bigCircle = new Ellipse2D.Double(oldX - (diam/2), oldY - (diam/2), diam, diam); 
				shapes.add(bigCircle);
			}
			// add smaller red circles to indicate the path of the robot
			else
			{
				double diam = scalingfactor * 2;
				Shape path = new Ellipse2D.Double(x - (diam/2), y - (diam/2), diam, diam);
				shapes.add(path); 
			}						
		}

		// add a big triangle, indicating the position of the robot and its orientation
		getNotVisibleTriangle().setGravityCenterX(x);
		getNotVisibleTriangle().setGravityCenterY(y);
		getNotVisibleTriangle().setAlpha(degrees);
		setUpdated(true);		
	}

	public void setVisibleTriangle1(){
		isVisible = 1;
	}

	public void setVisibleTriangle2(){
		isVisible = 2;
	}

	public void setOtherTriangleVisible(){
		if(isVisible == 1)
			isVisible = 2;
		else
			isVisible = 1;
	}

	public Triangle getVisibleTriangle(){
		if(isVisible == 1){
			return triangle1;
		}
		else
			return triangle2;
	}

	public Triangle getNotVisibleTriangle(){
		if(isVisible == 2){
			return triangle1;
		}
		else
			return triangle2;
	}

	public boolean waitingTriangleIsUpdated(){
		return isUpdated;
	}

	public void updateArc(double robotX, double robotY, double robotAngle, double USDistance){
		double correctedUSDistance = USDistance;
		correctedUSDistance = correctedUSDistance-5.5;
		double arcUpperLeftX = robotX-correctedUSDistance;
		double arcUpperLeftY = robotY-correctedUSDistance;
		double arcStart = 360 - robotAngle - 15;
		double arcExtent = 30;

		double side = 2*correctedUSDistance;
		this.sonarArc = new Arc2D.Double(arcUpperLeftX,arcUpperLeftY,side,side,arcStart,arcExtent,Arc2D.PIE);
	}

	public void setUpdated(boolean isUpdated){
		this.isUpdated = isUpdated;
	}

	public void updateUndergroundCircle(double robotX, double robotY, double LSValue)
	{
		
		double diam = scalingfactor * 7;
		undergroundCircle = new Ellipse2D.Double(robotX - (diam/2), robotY - (diam/2), diam, diam); 
	}
	
	

	/**
	 * Methode die alle paint methodes samenvoegd en uitvoert in het JPanel
	 */
	@Override
	protected void paintComponent(Graphics graph) {
		
		paintPathComponent(graph);
		//		paintGridComponent(graph);
		paintWallComponent(graph);
		paintUndergroundComponent(graph);
		paintBeamComponent(graph);
//		paintGridComponent(graph);
	}

	/**
	 * The arc is painted light blue when the measurement is not to be trusted (>250).
	 * Otherwise, it is painted in a darker blue.
	 */

	private void paintBeamComponent(Graphics graph) {

		this.updateArc(this.getSimulationPilot().getUltrasonicSensorPositionX() - getShiftToTheRight(),
				this.getSimulationPilot().getUltrasonicSensorPositionY() - getShiftDown(),
				this.getSimulationPilot().getAlpha(),
				this.getSimulationPilot().getUltraSensorValue()*getScalingfactor());
		//TODO
		if(simulationPilot != null)
		{
			((Graphics2D) graph).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
					0.4f));
			if(this.getSimulationPilot().getUltraSensorValue() > 200 || this.getSimulationPilot().getUltraSensorValue() < 20)
			{
				graph.setColor(new Color(12,168,244));
			}
			else
			{
				graph.setColor(new Color(12,24,244));
			}
			((Graphics2D) graph).fill(sonarArc);
		}
	}

	/**
	 * tekent de muren op het JPanel paneel
	 * @param graph
	 */
	private void paintWallComponent(Graphics graph) {
		((Graphics2D) graph).setColor(Color.BLACK);

		for(Wall wall : walls.values()){
			((Graphics2D) graph).fill(wall);
				
//				graph.drawImage(getVerticalWallImage(), wall.x, wall.y, null);
//			else graph.drawImage(getHorizontalWallImage(), wall.x, wall.y, null);
		}
	}

	/**
	 * Tekent het grid (rooster) op de achtergrond van de mapping
	 * @param graph
	 */
	private void paintGridComponent(Graphics graph) {

		int count = 50;
		int size = (int) getSizeTile();

		((Graphics2D) graph).setColor(Color.lightGray);

		for( int i = 0; i < count; i ++)
			for( int j = 0; j < count; j++)
			{
				Rectangle grid = new Rectangle( i * size,j * size, size, size);	
				((Graphics2D) graph).draw(grid);
			}
	}

	/**
	 * Tekent het pad van de robot en de robot zelf met daarachter het grid.
	 * @param graph
	 */
	private void paintPathComponent(Graphics graph){
		super.paintComponent(graph);
		paintBarcodeComponent(graph);		

		Vector<Shape> shapesx = new Vector<Shape>();
		shapesx.addAll(shapes);


		((Graphics2D) graph).setColor(Color.red);

		if(isUpdated){
			setOtherTriangleVisible();
			setUpdated(false);
		}


		int count = 50;
		int size = (int) getSizeTile();

		((Graphics2D) graph).setColor(Color.lightGray);

		for( int i = 0; i < count; i ++)
			for( int j = 0; j < count; j++)
			{
				Rectangle grid = new Rectangle( i * size - getShiftToTheRight(),j * size - getShiftDown(), size, size);	
				((Graphics2D) graph).draw(grid);
			}


		((Graphics2D) graph).setColor(Color.red);
		for(Shape s : shapesx)

		{

			int x;
			int y;

			if(s instanceof Triangle)
			{	if(s.equals(getVisibleTriangle()))
				((Graphics2D) graph).fill(s);
			x = (int) ((Triangle) s).getGravityCenterX();
			y = (int) ((Triangle) s).getGravityCenterY();


			if(simulationPilot!= null)
				getSSG().updateCoordinates("Simulator (" + (x) + " , " + (y )+ " , " + simulationPilot.getAlpha() + "°, Map: " + simulationPilot.getMapString() + ")");
			else
				getSSG().updateCoordinates("Simulator (" + (x) + " , " + (y) + ")");

			}
			else
			{	
				((Graphics2D) graph).fill(s);
			}
		}
		

	}

	/**
	 * Draws a dot in the color of the underground
	 */
	private void paintUndergroundComponent(Graphics graph){

		this.updateUndergroundCircle(this.getSimulationPilot().getLightsensorPositionX() - getShiftToTheRight(),
				this.getSimulationPilot().getLightsensorPositionY() - getShiftDown(), 
				this.getSimulationPilot().getLightSensorValue());
		if(this.getSimulationPilot().getLightSensorValue() < 45)
			((Graphics2D) graph).setColor(Color.black);
		else if(this.getSimulationPilot().getLightSensorValue() > 53)
			((Graphics2D) graph).setColor(Color.white);
		else
			((Graphics2D) graph).setColor(new Color(252,221,138));

		((Graphics2D) graph).fill(undergroundCircle);
		
	}
	
	private void paintBarcodeComponent(Graphics graph) {
		
		
		if (!SSG.getCommunicator().getRobotConnected() && simulationPilot.getMapGraph()!= null && getSimulationPilot().getLightSensorValue() <45){
			
				Rectangle2D[] barcode = Barcode.createVisualBarCode(((Barcode)simulationPilot.getMapGraph().getTileWithCoordinates(simulationPilot.getCurrentPositionRelativeX(), simulationPilot.getCurrentPositionRelativeY()).getContent()),getSimulationPilot().getCenterAbsoluteCurrentTile()[0],getSimulationPilot().getCenterAbsoluteCurrentTile()[1]);
				getMapGraphConstructed().getCurrentTile().setContent(simulationPilot.getMapGraph().getTileWithCoordinates(simulationPilot.getCurrentPositionRelativeX(), simulationPilot.getCurrentPositionRelativeY()).getContent());
				if((Barcode)simulationPilot.getMapGraph().getTileWithCoordinates(simulationPilot.getCurrentPositionRelativeX(), simulationPilot.getCurrentPositionRelativeY()).getContent() != null){
				addBarcode(((Barcode)simulationPilot.getMapGraph().getTileWithCoordinates(simulationPilot.getCurrentPositionRelativeX(), simulationPilot.getCurrentPositionRelativeY()).getContent()).toString(), barcode);}
		
		}
		
		
		//teken alle rectangles van alle barcodes
		for (Tuple t : barcodes) {
			String rep = (String) t.getItem1();
			Rectangle2D[] bc = (Rectangle2D[]) t.getItem2();
			for (int i = 0; i < 8; i++) {
				if(rep.charAt(i) == '0')
					((Graphics2D)graph).setColor(Color.black);
				else
					((Graphics2D)graph).setColor(Color.white);

					((Graphics2D)graph).fill(bc[i]);

			}
		}
			
	}

	public void setRobotLocation(double x, double y, double degrees){
		x = x - this.getShiftToTheRight();
		y = y - this.getShiftDown();
		this.addCircle(x*1, y*1, degrees);
	}

	/**
	 * Deletes the former path of the robot
	 */
	public void clearPath() {

		Shape triangle = getVisibleTriangle();
		Shape triangletwo = getNotVisibleTriangle();
		shapes.removeAllElements();		

		shapes.add(triangle);
		shapes.add(triangletwo);
	}
	
	/**
	 * Removes all Wall-objects this panel is keeping track of.
	 */
	public void removeWalls(){
		walls.clear();
	}

	/**
	 * Removes all Barcodes this panel is keeping track of.
	 */
	public void removeBarCodes() {
		barcodes = new Bag<Tuple<String, Rectangle2D[]>>();
	}
	/**
	 * Deletes the former path of the robot and all the walls that have been explored as yet.
	 */
	public void clearTotal() {
		clearPath();
		removeWalls();
		removeBarCodes();	
	}

	/**
	 * Deletes the constructed mapGraph and sets up a new one.
	 * Also calls clearTotal() and resets the triangles. (angle 270);
	 */
	public void resetMap(){
		getSimulationPilot().reset();
		SSG.getInformationBuffer().resetBuffer();
		triangle1 = new Triangle(5*getSizeTile() + getSizeTile()/2, 5*getSizeTile() + getSizeTile()/2, 270, scalingfactor);
		triangle2 = new Triangle(5*getSizeTile() + getSizeTile()/2, 5*getSizeTile() + getSizeTile()/2, 270, scalingfactor);
		mapGraphConstructed = new MapGraph();
		clearTotal();

	}
	
	
	public void setSSG(SilverSurferGUI SSG) {
		this.SSG = SSG;
	}

	public SilverSurferGUI getSSG() {
		return SSG;
	}

	public SimulationPilot getSimulationPilot() {
		return this.simulationPilot;
	}

	public void setSimulationPilot(SimulationPilot simulationPilot) {
		this.simulationPilot = simulationPilot;
	}

	public MapGraph getMapGraphConstructed() {
		return this.mapGraphConstructed;
	}
	
	public double getSizeTile(){
		return 40*scalingfactor;
	}
	
	public double getScalingfactor(){
		return scalingfactor;
	}
	
	public void setScalingfactor(double scalingfactor){
		scaleWalls(scalingfactor);
		scaleShapes(scalingfactor);
		scaleBarcodes(scalingfactor);
		getSimulationPilot().setStartPositionAbsoluteX(getSimulationPilot().getStartPositionAbsoluteX()*scalingfactor/this.getScalingfactor());
		getSimulationPilot().setStartPositionAbsoluteY(getSimulationPilot().getStartPositionAbsoluteY()*scalingfactor/this.getScalingfactor());
		this.scalingfactor = scalingfactor;
	}
	

	/**
	 * Updates the width/height/coordinates of the barcodes.
	 */
	private void scaleBarcodes(double scalingfactor) {
		//TODO update coordinates
		for (Tuple t : barcodes) {
			String rep = (String) t.getItem1();
			Rectangle2D[] bc = (Rectangle2D[]) t.getItem2();
			for (int i = 0; i < 8; i++){
				bc[i] = new Rectangle((int)(bc[i].getX()*scalingfactor/this.getScalingfactor()), (int)(bc[i].getY()*scalingfactor/this.getScalingfactor()), (int)(bc[i].getWidth()*scalingfactor/this.getScalingfactor()), (int)(bc[i].getHeight()*scalingfactor/this.getScalingfactor()));	
			}
		}

	}
	
	private void scaleShapes(double scalingfactor){
		for(Shape shape : shapes){
			if(shape instanceof Triangle){
				((Triangle) shape).setScalingfactor(scalingfactor);
				
				double newGravityCenterX = ((Triangle) shape).getGravityCenterX()*scalingfactor/this.getScalingfactor();
				double newGravityCenterY = ((Triangle) shape).getGravityCenterY()*scalingfactor/this.getScalingfactor();
				
				((Triangle) shape).setGravityCenterX(newGravityCenterX);
				((Triangle) shape).setGravityCenterY(newGravityCenterY);
				
				if(((Triangle) shape).equals(getVisibleTriangle())){
					getSimulationPilot().setCurrentPositionAbsoluteX(newGravityCenterX);
					getSimulationPilot().setCurrentPositionAbsoluteY(newGravityCenterY);
				}
				
			}
			
			else{
				double oldDiam = ((Ellipse2D.Double) shape).getHeight();
				double OldXCoordinate = ((Ellipse2D.Double) shape).getX() + oldDiam/2;
				double OldYCoordinate = ((Ellipse2D.Double) shape).getY() + oldDiam/2;
				
				double newDiam = oldDiam*scalingfactor/this.getScalingfactor();
				double newXCoordinate = (OldXCoordinate - (newDiam/2))*scalingfactor/this.getScalingfactor();
				double newYCoordinate = (OldYCoordinate - (newDiam/2))*scalingfactor/this.getScalingfactor();
				
				((Ellipse2D.Double) shape).setFrame(newXCoordinate, newYCoordinate, newDiam, newDiam);				
			}			
		}
	}
	
	private void scaleWalls(double scalingfactor){
		
		Vector oldPoints = new Vector<Point2D>();
		for(Point2D point: walls.keySet()){
			oldPoints.add(point);
		}
		
		for(Object point: oldPoints){
			
			Wall wall = walls.get(point);
			
			int whichXTile = (int) Math.round(((Point2D) point).getX()/getSizeTile());
			int whichYTile = (int) Math.round(((Point2D) point).getY()/getSizeTile());
			
			double newXCoordinate = whichXTile*(40*scalingfactor);
			double newYCoordinate = whichYTile*(40*scalingfactor);
			
			int newWidth = (int) Math.round((wall.getWidth()*scalingfactor/this.getScalingfactor()));
			int newHeight = (int) Math.round((wall.getHeight()*scalingfactor/this.getScalingfactor()));
			
			wall.setSize(newWidth, newHeight);
			
			if(wall.getState()==State.HORIZONTAL){
				newYCoordinate = newYCoordinate - (Wall.getStandardWidth() * scalingfactor)/2;
			}
			
			else{
				newXCoordinate = newXCoordinate - (Wall.getStandardWidth() * scalingfactor)/2;
			}
			
			wall.setLocation((int) newXCoordinate, (int) newYCoordinate);
			walls.remove(point);
			walls.put(new Point2D.Double(newXCoordinate, newYCoordinate), wall);

		}
		
	}
	
	public int getShiftToTheRight(){
		return shiftToTheRight;
	}
	
	public void setShiftToTheRigth(int shiftRight){
		this.shiftToTheRight = this.shiftToTheRight + shiftRight;
		shiftWalls(true, shiftRight);
		shiftShapes(true, shiftRight);
	}
	
	public int getShiftDown(){
		return shiftDown;
	}
	
	public void setShiftDown(int shiftDown){
		this.shiftDown = shiftDown;
		shiftWalls(false, shiftDown);
		shiftShapes(false, shiftDown );
	}

	private void shiftWalls(boolean shiftHorizontal, int shift) {
		Vector oldPoints = new Vector<Point2D>();
		for(Point2D point: walls.keySet()){
			oldPoints.add(point);
		}
		
		for(Object point: oldPoints){
			
			Wall wall = walls.get(point);
			
			double newXCoordinate = ((Point2D.Double) point).getX();
			double newYCoordinate = ((Point2D.Double) point).getY();
			
			if(shiftHorizontal){
			newXCoordinate = wall.getX()-shift;
			}
			else{
				newYCoordinate = wall.getY()-shift;
			}
			wall.setLocation((int) newXCoordinate, (int) wall.getY());
			
			walls.remove(point);
			walls.put(new Point2D.Double(newXCoordinate, newYCoordinate), wall);

		}
	}
	
	private void shiftShapes(boolean shiftHorizontal, int shift) {
		for(Shape shape : shapes){
			if(shape instanceof Triangle){	
				
				double newGravityCenterX = ((Triangle) shape).getGravityCenterX();
				double newGravityCenterY = ((Triangle) shape).getGravityCenterY();
				
				if(shiftHorizontal){
					newGravityCenterX = newGravityCenterX-shift;
					}
					else{
					newGravityCenterY = newGravityCenterY-shift;
					}
				
				((Triangle) shape).setGravityCenterX(newGravityCenterX);
				((Triangle) shape).setGravityCenterY(newGravityCenterY);

				}
			
			else{
				double diam = ((Ellipse2D.Double) shape).getHeight();
				double newXCoordinate = ((Ellipse2D.Double) shape).getX();
				double newYCoordinate = ((Ellipse2D.Double) shape).getY();
				
				if(shiftHorizontal){
					newXCoordinate = newXCoordinate-shift;
					}
					else{
					newYCoordinate = newYCoordinate-shift;
					}
				
				((Ellipse2D.Double) shape).setFrame(newXCoordinate, newYCoordinate, diam, diam);				
			}			
		}
	}
//	
//	/**
//	 * verwijdert de muur als er een muur staat,
//	 * als er geen muur staat, return
//	 */
//	public void addWhiteLine(Orientation orientation, double x, double y)
//	{
//		Point2D point = calculateWallPoint(orientation, x, y);
//		if(!walls.containsKey(point))
//			return;
//		removeWallFrom(point);
//	}

	/**
	 * voegt een muur bij aan hashmap
	 * muur is afhankelijk van de orientatie
	 */
	public void addWall(Orientation orientation, double x, double y)
	{	
		Point2D point = calculateWallPoint(orientation, x, y);
		point.setLocation(point.getX()-getShiftToTheRight() , point.getY()-getShiftDown() );
		Wall wall;
		if(orientation.equals(Orientation.NORTH) || orientation.equals(Orientation.SOUTH)){
			wall = new Wall(State.HORIZONTAL, (double) point.getX(), (double) point.getY(), scalingfactor);
		}
		else{
			wall = new Wall(State.VERTICAL, (double) point.getX(), (double) point.getY(), scalingfactor);
		}
		setWall(point, wall);
	}
	
	/**
	 * Voegt een barcode toe aan de bag met barcodes.
	 * @pre De posities van de rectangles etc moeten nu al helemaal ingevuld zijn.
	 */
	public void addBarcode(String stringRepr,  Rectangle2D[] visual){
		barcodes.add(new Tuple<String, Rectangle2D[]>(stringRepr, visual));
	}


	public void setWall(Point2D point, Wall wall){
		walls.put(point, wall);
	}

	public void removeWallFrom(Point2D point){
		walls.remove(point);
	}

	public void setTile(int x, int y){
		getMapGraphConstructed().setTileXY(x, y, new Tile());
	}
	
	public void setWallOnTile(int x, int y, Orientation orientation){
		if(getMapGraphConstructed().getTileWithCoordinates(x, y) == null)
			throw new IllegalArgumentException("in simulationPanel bij methode SetWallOnTile " +
					"zijn coordinaten meegegeven die de mapgraph niet bevat nl" +
					x + " en " +y);
		getMapGraphConstructed().getTileWithCoordinates(x, y).getEdge(orientation).setObstruction(Obstruction.WALL);
	}
	
	public void removeWallFromTile(int x, int y, Orientation orientation){
		if(getMapGraphConstructed().getTileWithCoordinates(x, y) == null)
			throw new IllegalArgumentException("in simulationPanel bij methode removeWallFromTile " +
					"zijn coordinaten meegegeven die de mapgraph niet bevat");
		getMapGraphConstructed().getTileWithCoordinates(x, y).getEdge(orientation).setObstruction(null);
	}
	
	/**
	 * Deze methode berekent afhankelijk van de orientatie en het punt dat wordt meegegeven
	 * een punt dat wordt gebruikt om de wall te positioneren!
	 * voor noord en west is dit dus het linkerhoekbovenpunt van het tile
	 * voor zuid het rechterbovenhoekpunt en voor zuid het linkeronderhoekpunt
	 * (aan wall wordt altijd het midden van de linker (als wall horizontaal ligt)
	 * of boven(als wall rechtopstaat) breedte meegegeven
	 * (ik heb het midden gepakt en laten meegeven waarna het in wall verwerkt wordt tot
	 * het linkerbovenhoekpunt van de 'rectangle omdat dit makkelijk is om af te ronden
	 * tot op een veelvoud van 40)
	 */
	private Point2D calculateWallPoint(Orientation orientation, double x, double y){
		double xCoordinate = (Math.floor(x/getSizeTile())*getSizeTile());
		double yCoordinate = (Math.floor(y/getSizeTile())*getSizeTile());
		
		if(orientation.equals(Orientation.NORTH)){
			yCoordinate = yCoordinate - (Wall.getStandardWidth() * scalingfactor)/2;
		}
		
		else if(orientation.equals(Orientation.SOUTH)){
			yCoordinate = yCoordinate + getSizeTile() - (Wall.getStandardWidth() * scalingfactor)/2;
		}
		else if(orientation.equals(Orientation.EAST)) {
			xCoordinate = xCoordinate + getSizeTile() - (Wall.getStandardWidth() * scalingfactor)/2;
		}
		else{
			xCoordinate = xCoordinate - (Wall.getStandardWidth() * scalingfactor)/2;
		}
		
		Point2D point = new Point2D.Double(xCoordinate, yCoordinate);
		return point;
	}

//	public BufferedImage getVerticalWallImage() {
//		return verticalWallImage;
//	}
//	public BufferedImage getHorizontalWallImage() {
//		return horizontalWallImage;
//	}
}
