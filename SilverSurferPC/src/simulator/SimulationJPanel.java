package simulator;

import gui.SilverSurferGUI;

import java.awt.*;
import java.awt.geom.*;
import java.util.HashMap;
import java.util.Vector;
import javax.swing.*;


import mapping.*;

public class SimulationJPanel extends JPanel {

	private SilverSurferGUI SSG;
	private SimulationPilot simulatorPilot;
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

	private Vector<Shape> shapes = new Vector<Shape>();
	
	/**
	 * Houdt een map bij met coordinaten die verwijzen naar de muur die erop staat
	 * de positie van de muur ten opzichte van de coordinaten staat uitgelegd in de klasse
	 * wall
	 */
	private HashMap<Point2D, Wall> walls = new HashMap<Point2D, Wall>();

	public SimulationJPanel(){
		shapes.add(triangle1);
		shapes.add(triangle2);
	}

	public void addCircle(float x, float y, float degrees) {
		// remove the last triangle and draw little circles to indicate the path
		if(shapes.size()>0)
		{ 	
			float oldX = this.getVisibleTriangle().getGravityCenterX();
			float oldY = this.getVisibleTriangle().getGravityCenterY();

			// add a bigger circle where the robot starts
			if(shapes.size()==0)
			{
				float diam = 5;
				Shape bigCircle = new Ellipse2D.Float(oldX - (diam/2), oldY - (diam/2), diam, diam); 
				shapes.add(bigCircle);
			}
			// add smaller red circles to indicate the path of the robot
			else
			{
				float diam = 3;
				Shape path = new Ellipse2D.Float(x - (diam/2), y - (diam/2), diam, diam);
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

	public void setUpdated(boolean isUpdated){
		this.isUpdated = isUpdated;
	}

	@Override
	protected void paintComponent(Graphics graph) {
		// paints the path of the robot
		super.paintComponent(graph);
		Vector<Shape> shapesx = new Vector<Shape>();
		shapesx.addAll(shapes);


		((Graphics2D) graph).setColor(Color.red);

		if(isUpdated){
			setOtherTriangleVisible();
			setUpdated(false);
		}

		Graphics2D g2 = (Graphics2D) graph;

		// paints the grid on the panel
		int count = 50;
		int size = 40;

		((Graphics2D) graph).setColor(Color.lightGray);


		for( int i = 0; i < count; i ++)
			for( int j = 0; j < count; j++)
			{
				Rectangle grid = new Rectangle( i * size,j * size, size, size);	
				g2.draw(grid);
			}
		
		Graphics2D g3 = (Graphics2D) graph;
		
		// paint the walls on the panel
		//TODO momenteel is de hashmap leeg dus tekent die niks omdat addwall en 
		//addwhiteline getoggled zijn
		
		((Graphics2D) graph).setColor(Color.BLACK);
		
		for(Wall wall : walls.values()){
			g3.fill(wall);
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

			}
			else
			{	
				((Graphics2D) graph).fill(s);
				x = (int) ((RectangularShape) s).getX();
				y = (int) ((RectangularShape) s).getY();
			}

			if(simulatorPilot!= null)
				getSSG().updateCoordinates("Simulator (" + (x+5) + " , " + (y+5 )+ " , " + simulatorPilot.getAlpha() + ")");
			else
				getSSG().updateCoordinates("Simulator (" + (x+5) + " , " + (y+5) + ")");
		}





	}

	public void setRobotLocation(float x, float y, float degrees){
		this.addCircle(x*1, y*1, degrees);
	}

	public void clear() {
	
		Shape triangle = getVisibleTriangle();
		Shape triangletwo = getNotVisibleTriangle();
		shapes.removeAllElements();

		shapes.add(triangle);
		shapes.add(triangletwo);
	}

	public void setSSG(SilverSurferGUI SSG) {
		this.SSG = SSG;
	}

	public SilverSurferGUI getSSG() {
		return SSG;
	}

	public void setSimulatorPilot(SimulationPilot simulatorPilot) {
		this.simulatorPilot = simulatorPilot;
	}

	/**
	 * verwijdert de muur als er een muur staat,
	 * als er geen muur staat, return
	 */
	public void addWhiteLine(Orientation orientation, float x, float y)
	{
		Point2D point = ExtMath.calculateWallPoint(orientation, x, y);
		if(!walls.containsKey(point))
			return;
		removeWallFrom(point);
		System.out.println(orientation + "witte lijn toevoegen");
		
	}
	
	/**
	 * voegt een muur bij aan hashmap
	 * muur is afhankelijk van de orientatie
	 */
	public void addWall(Orientation orientation, float x, float y)
	{	
		System.out.println(orientation + "muur toevoegen");
		Point2D point = ExtMath.calculateWallPoint(orientation, x, y);
		Wall wall;
		if(orientation.equals(Orientation.NORTH) || orientation.equals(Orientation.SOUTH)){
			wall = new Wall(State.HORIZONTAL, (float) point.getX(), (float) point.getY());
		}
		else{
			wall = new Wall(State.VERTICAL, (float) point.getX(), (float) point.getY());
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
		simulatorPilot.checkForObstructions();
	}
}
