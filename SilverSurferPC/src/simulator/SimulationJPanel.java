package simulator;

import gui.SilverSurferGUI;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.*;


import mapping.*;

public class SimulationJPanel extends JPanel {

	private SilverSurferGUI SSG;
	private SimulationPilot simulationPilot;

	/**
	 * Images die de muur getekend worden (komende van de 8bit Pokemon games!)
	 */
	private BufferedImage verticalWallImage;
	private BufferedImage horizontalWallImage;
	/**
	 * 2 driehoeken die elkaar afwisselen om afgebeeld te worden
	 * de ene wordt afgebeeld terwijl de andere zijn nieuwe coordinaten berekend worden
	 */
	private Triangle triangle1 = new Triangle(0,0,0);
	private Triangle triangle2 = new Triangle(0,0,0);
	/**
	 * geeft het getal van de driehoek die afgebeeld wordt
	 */
	private int isVisible = 1;
	/**
	 * is true als de coordinaten van de driehoek die niet afgebeeld wordt, berekend zijn.
	 */
	private boolean isUpdated = false;
	
	private Arc2D sonarArc = new Arc2D.Double();

	private Vector<Shape> shapes = new Vector<Shape>();

	/**
	 * Houdt een map bij met coordinaten die verwijzen naar de muur die erop staat
	 * de positie van de muur ten opzichte van de coordinaten staat uitgelegd in de klasse
	 * wall
	 */
	private HashMap<Point2D, Wall> walls = new HashMap<Point2D, Wall>();

	public SimulationJPanel()
	{
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

	public void addCircle(double x, double y, double degrees) {
		// remove the last triangle and draw little circles to indicate the path
		if(shapes.size()>0)
		{ 	
			double oldX = this.getVisibleTriangle().getGravityCenterX();
			double oldY = this.getVisibleTriangle().getGravityCenterY();

			// add a bigger circle where the robot starts
			if(shapes.size()<=3)
			{
				double diam = 5;
				Shape bigCircle = new Ellipse2D.Double(oldX - (diam/2), oldY - (diam/2), diam, diam); 
				shapes.add(bigCircle);
			}
			// add smaller red circles to indicate the path of the robot
			else
			{
				double diam = 2;
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
		if (simulationPilot.isRealRobot())
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

	/**
	 * Methode die alle paint methodes samenvoegd en uitvoert in het JPanel
	 */
	@Override
	protected void paintComponent(Graphics graph) {

		paintPathComponent(graph);
//		paintGridComponent(graph);
		paintWallComponent(graph);
		paintBeamComponent(graph);



	}
/**
 * Tekent de beam die de ultrasone sensor voorstelt op de JPanel
 * @param graph
 */
	private void paintBeamComponent(Graphics graph) {
		Graphics2D g = (Graphics2D) graph;
		if(simulationPilot != null && simulationPilot.isRealRobot())
		{
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                    0.4f));
			g.setColor(new Color(12,24,244));
			g.fill(sonarArc);
		}
	}

/**
 * tekent de muren op het JPanel paneel
 * @param graph
 */
	private void paintWallComponent(Graphics graph) {
		Graphics2D g = (Graphics2D) graph;
		((Graphics2D) graph).setColor(Color.BLACK);

		for(Wall wall : walls.values()){
			if(wall.getState() == State.VERTICAL)
				g.drawImage(getVerticalWallImage(), wall.x, wall.y, null);
			else g.drawImage(getHorizontalWallImage(), wall.x, wall.y, null);
		}
	}

/**
 * Tekent het grid (rooster) op de achtergrond van de mapping
 * @param graph
 */
	private void paintGridComponent(Graphics graph) {
		Graphics2D g = (Graphics2D) graph;

		int count = 50;
		int size = 40;

		((Graphics2D) graph).setColor(Color.lightGray);

		for( int i = 0; i < count; i ++)
			for( int j = 0; j < count; j++)
			{
				Rectangle grid = new Rectangle( i * size,j * size, size, size);	
				g.draw(grid);
			}
	}

/**
 * Tekent het pad van de robot en de robot zelf met daarachter het grid.
 * @param graph
 */
	private void paintPathComponent(Graphics graph){
	super.paintComponent(graph);
	Vector<Shape> shapesx = new Vector<Shape>();
	shapesx.addAll(shapes);


	((Graphics2D) graph).setColor(Color.red);

	if(isUpdated){
		setOtherTriangleVisible();
		setUpdated(false);
		}
	
	Graphics2D g = (Graphics2D) graph;

	int count = 50;
	int size = 40;

	((Graphics2D) graph).setColor(Color.lightGray);

	for( int i = 0; i < count; i ++)
		for( int j = 0; j < count; j++)
		{
			Rectangle grid = new Rectangle( i * size,j * size, size, size);	
			g.draw(grid);
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
			getSSG().updateCoordinates("Simulator (" + (x) + " , " + (y )+ " , " + simulationPilot.getAlpha() + "�, Map: " + simulationPilot.getMapString() + ")");
		else
			getSSG().updateCoordinates("Simulator (" + (x) + " , " + (y) + ")");
		
		}
		else
		{	
			((Graphics2D) graph).fill(s);
		}

	}

	Graphics2D g3 = (Graphics2D) graph;
	if(simulationPilot.isRealRobot()){
		g3.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                0.4f));
		g3.setColor(new Color(12,24,244));
		g3.fill(sonarArc);
	}



	}


//	private void paintPathComponent(Graphics graph) {
//		super.paintComponent(graph);
//		Vector<Shape> shapesx = new Vector<Shape>();
//		shapesx.addAll(shapes);
//
//		((Graphics2D) graph).setColor(Color.red);
//		for(Shape s : shapesx)
//		{
//			int x;
//			int y;
//
//			if(s instanceof Triangle)
//			{
//				if(s.equals(getVisibleTriangle()))
//				{
//					((Graphics2D) graph).fill(s);
//				}
//				x = (int) ((Triangle) s).getGravityCenterX();
//				y = (int) ((Triangle) s).getGravityCenterY();
//
//				if(simulationPilot!= null)
//				{
//					getSSG().updateCoordinates("Simulator (" + x + " , " + y + " , " + (int) simulationPilot.getAlpha() + "�, Map: " + simulationPilot.getMapString() + ")");
//				}
//				else
//				{
//					getSSG().updateCoordinates("Simulator (" + x + " , " + y + ")");
//				}
//			}
//			else
//			{	
//				((Graphics2D) graph).fill(s);
//			}
//		}
//
//		if(isUpdated){
//			setOtherTriangleVisible();
//			setUpdated(false);
//		}
//	}

	public void setRobotLocation(double x, double y, double degrees){
		this.addCircle(x*1, y*1, degrees);
	}

	/**
	 * Deletes the former path of the robot
	 */
	public void clear() {

		Shape triangle = getVisibleTriangle();
		Shape triangletwo = getNotVisibleTriangle();
		shapes.removeAllElements();		

		shapes.add(triangle);
		shapes.add(triangletwo);
	}
	
	/**
	 * Deletes the former path of the robot and all the walls that have been explored as yet.
	 */
	public void clearTotal() {
		this.clear();
		walls.clear();
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

	/**
	 * verwijdert de muur als er een muur staat,
	 * als er geen muur staat, return
	 */
	public void addWhiteLine(Orientation orientation, double x, double y)
	{
		Point2D point = ExtMath.calculateWallPoint(orientation, x, y);
		if(!walls.containsKey(point))
			return;
		removeWallFrom(point);
	}

	/**
	 * voegt een muur bij aan hashmap
	 * muur is afhankelijk van de orientatie
	 */
	public void addWall(Orientation orientation, double x, double y)
	{	
		Point2D point = ExtMath.calculateWallPoint(orientation, x, y);
		
//		double XOther = point.getX() + Orientation.getOtherPointLine(orientation)[0];
//		double YOther =	point.getY() + Orientation.getOtherPointLine(orientation)[1];
//		
//		if(Line2D.ptSegDist(point.getX(), point.getY(), XOther, YOther, x, y) > 21 ){
//			return;
//		}
		
//		if(point.distance((double) x, (double) y) > 40){
//			return;
//		}
		
		Wall wall;
		if(orientation.equals(Orientation.NORTH) || orientation.equals(Orientation.SOUTH)){
			wall = new Wall(State.HORIZONTAL, (double) point.getX(), (double) point.getY());
		}
		else{
			wall = new Wall(State.VERTICAL, (double) point.getX(), (double) point.getY());
		}
		setWall(point, wall);
	}


	public void setWall(Point2D point, Wall wall){
		walls.put(point, wall);
	}

	public void removeWallFrom(Point2D point){
		walls.remove(point);
	}

	//TODO dit snap ik niet goed wat die doet want wordt nergens anders opgeroepen buiten 
	//de mouseclickthread, ik denk dat nele ze hier gezet heeft, maar dat is niet de methode
	//die gebruikt wordt! miss werkt het als de juiste methode in die mouseclickthread staat!
	public void checkForObstructions()
	{
		simulationPilot.checkForObstructions();
	}
	
	public BufferedImage getVerticalWallImage() {
		return verticalWallImage;
	}
	public BufferedImage getHorizontalWallImage() {
		return horizontalWallImage;
	}
}
