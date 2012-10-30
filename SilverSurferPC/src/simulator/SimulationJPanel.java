package simulator;
import gui.SilverSurferGUI;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RectangularShape;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JPanel;


public class SimulationJPanel extends JPanel {
	
	private SilverSurferGUI SSG;
	private SimulationPilot simulatorPilot;
	
	private Vector<Shape> shapes = new Vector<Shape>();
		
	public void addCircle(float x, float y, float degrees){
		// remove the last triangle and draw little circles to indicate the path
		if(shapes.size()>0)
		{ 			
			shapes.remove(shapes.size()-1);
			
			// add a bigger circle where the robot starts
			if(shapes.size()==0)
			{
				Shape bigCircle = new Ellipse2D.Float((float) (((RectangularShape) shapes.get(0)).getX()),
						  							  (float) (((RectangularShape) shapes.get(0)).getY()),
						  							   5,5); 
				shapes.add(bigCircle);
			}
			// add smaller red circles to indicate the path of the robot
			else
			{
				Shape path = new Ellipse2D.Float((float) (((RectangularShape) shapes.get(shapes.size()-1)).getX()+4),
											 (float) (((RectangularShape) shapes.get(shapes.size()-1)).getY()+4),
											  2,2);
				shapes.add(path); 
			}						
		}
		
		// add a big triangle, indicating the position of the robot and its orientation
		Shape triangle = new Triangle(x,y,degrees);
		shapes.add(triangle);
	}
	
	@Override
	protected void paintComponent(Graphics graph) {
		// paints the path of the robot
		super.paintComponent(graph);
		Vector<Shape> shapesx = new Vector<Shape>();
		shapesx.addAll(shapes);
		((Graphics2D) graph).setColor(Color.red);
		for(Shape s : shapesx){
			((Graphics2D) graph).fill(s);
			((RectangularShape) s).getX();
			if(simulatorPilot!= null)
				getSSG().updateCoordinates("Simulator ("+(((Double)(((RectangularShape) s).getX())).intValue()+5)+","+(((Double)((RectangularShape) s).getY()).intValue()+5)+") angle: " + simulatorPilot.getAlpha());
			else
				getSSG().updateCoordinates("Simulator ("+(((Double)(((RectangularShape) s).getX())).intValue()+5)+","+(((Double)((RectangularShape) s).getY()).intValue()+5)+")");
			
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
		((Graphics2D) graph).setColor(Color.red);

		repaint();
	}
	
	public void setRobotLocation(float x, float y, float degrees){
		this.addCircle(x*1, y*1, degrees);
	}

	
	public void clear(){
		
		Shape triagle = new Triangle((float) (((Triangle) shapes.get(shapes.size()-1)).getX()),
									 (float) (((Triangle) shapes.get(shapes.size()-1)).getY()),
									 (float) (((Triangle) shapes.get(shapes.size()-1)).getDegrees()));
//		Shape circleOld = new Ellipse2D.Float((float) (((RectangularShape) shapes.get(shapes.size()-1)).getX()),(float) (((RectangularShape) shapes.get(shapes.size()-1)).getY()),10,10); 			
		shapes.removeAllElements();
//		shapes.add(circleOld);
		shapes.add(triagle);
		
	}
	
	public void setSSG(SilverSurferGUI sSG) {
		SSG = sSG;
	}
	
	public SilverSurferGUI getSSG() {
		return SSG;
	}
	
	public void setSimulatorPilot(SimulationPilot simulatorP) {
		simulatorPilot = simulatorP;
	}
} 