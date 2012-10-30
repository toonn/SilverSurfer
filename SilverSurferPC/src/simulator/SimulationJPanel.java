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

import javax.swing.JPanel;


public class SimulationJPanel extends JPanel {
	
	private SilverSurferGUI SSG;
	private SimulationPilot simulatorPilot;
	
	private Vector<Shape> shapes = new Vector<Shape>();
	
	public void addCircle(float x, float y){
		Shape circle = new Ellipse2D.Float(x-5, y-5, 10, 10);
		if(shapes.size()>0) { 			
			Shape circleOld = new Ellipse2D.Float((float) (((RectangularShape) shapes.get(shapes.size()-1)).getX()+4),(float) (((RectangularShape) shapes.get(shapes.size()-1)).getY()+4),2,2); 			
			shapes.remove(shapes.size()-1); 			
			shapes.add(circleOld); 			
		}
		shapes.add(circle);
	}
	
	@Override
	protected void paintComponent(Graphics graph) {
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

		//Paint grid-wallpaper.
		int count = 50;
		int size = 40;
		
		((Graphics2D) graph).setColor(Color.gray);
		for( int i = 0; i < count; i ++)
			for( int j = 0; j < count; j++)
				{
					Rectangle grid = new Rectangle( i * size,j * size, size, size);	
					g2.draw(grid);
				}
		((Graphics2D) graph).setColor(Color.red);

		repaint();
	}
	
	public void setRobotLocation(float x, float y){
		this.addCircle( x*1, y*1);
	}	
	
	public void clear(){
		Shape circleOld = new Ellipse2D.Float((float) (((RectangularShape) shapes.get(shapes.size()-1)).getX()),(float) (((RectangularShape) shapes.get(shapes.size()-1)).getY()),10,10); 			
		shapes.removeAllElements();
		shapes.add(circleOld);
		
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