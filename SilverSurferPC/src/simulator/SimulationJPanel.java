package simulator;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.util.Vector;

import javax.swing.JPanel;


public class SimulationJPanel extends JPanel {
	
	private Vector<Shape> shapes = new Vector<Shape>();
	
	public void addCircle(float x, float y){
		Shape circle = new Ellipse2D.Float(x-1, y+1, 2, 2);
		shapes.add(circle);
	}
	
	@Override
	protected void paintComponent(Graphics graph) {
		super.paintComponent(graph);
		Vector<Shape> shapesx = new Vector<Shape>();
		shapesx.addAll(shapes);
		for(Shape s : shapesx) {
			((Graphics2D) graph).fill(s);
		}
		repaint();
	}
	
	public void setRobotLocation(float x, float y){
		this.addCircle( x*1, y*1);
	}	
} 