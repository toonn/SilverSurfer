package simulator;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RectangularShape;
import java.util.Vector;

import javax.swing.JPanel;


public class SimulationJPanel extends JPanel {
	
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
		for(Shape s : shapesx) {
			((Graphics2D) graph).setColor(Color.red);
			((Graphics2D) graph).fill(s);
		}
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
} 