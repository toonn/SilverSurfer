package simulator.viewport;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import simulator.pilot.AbstractPilot;
import simulator.pilot.PilotInterface;

@SuppressWarnings("serial")
public class UnitViewPort extends DummyViewPort {
	
    private final Arc2D sonarArc = new Arc2D.Double();
    private final Ellipse2D undergroundCircle = new Ellipse2D.Double();
    private List<Point> pathCoordinates;
    private final Set<AbstractPilot> pilots;

    public UnitViewPort(final Set<AbstractPilot> pilotSet) {
        super(pilotSet);
        pilots = new HashSet<AbstractPilot>(pilotSet);
        resetPath();
    }

    private void addPathPoint(final double x, final double y) {
        final Point point = new Point();
        point.setLocation(x, y);
        pathCoordinates.add(point);
        repaint();
    }

    public void resetPath() {
        pathCoordinates = new ArrayList<Point>();
        for (final PilotInterface pilot : pilots)
            addPathPoint(pilot.getPosition().getX(), pilot.getPosition().getY());
    }

    /*public void moveRobot(double x, double y, final double degrees) {
        x = x * scalingfactor;
        y = y * scalingfactor;
        pathCoordinates.get(pathCoordinates.size() - 1).setLocation(x, y);

        repaint();
    }*/

    /**
     * Methode die alle paint methodes samenvoegd en uitvoert in het JPanel
     */
    @Override
    protected void paintComponent(final Graphics graph) {
        super.paintComponent(graph);
        updatePathComponent();
        paintPathComponent(graph);
        //paintUndergroundComponent(graph);
        paintBeamComponent(graph);
    }
    
    private void updatePathComponent() {
        for (final PilotInterface pilot : pilots)
        	if(pilot.getPosition().getX() != pathCoordinates.get(pathCoordinates.size()-1).getX() || pilot.getPosition().getY() != pathCoordinates.get(pathCoordinates.size()-1).getY())
            	addPathPoint(pilot.getPosition().getX(), pilot.getPosition().getY());
    }

    /**
     * Tekent het pad van de robot en de robot zelf met daarachter het grid.
     */
    private void paintPathComponent(final Graphics graph) {
        final Graphics2D g2 = ((Graphics2D) graph);
        g2.setColor(Color.RED);
        final Stroke originalStroke = g2.getStroke();
        g2.setStroke(new BasicStroke(3));
        for (int i = 0; i < pathCoordinates.size() - 1; i++)
        	g2.draw(new Line2D.Double(pathCoordinates.get(i), pathCoordinates.get(i + 1)));
        final double radius = 3;
        final double diameter = 2 * radius;
        final double upperleftCornerX = pathCoordinates.get(0).x - radius;
        final double upperleftCornerY = pathCoordinates.get(0).y - radius;
        new Ellipse2D.Double(upperleftCornerX, upperleftCornerY, diameter, diameter); //What is this??
        g2.setStroke(originalStroke);
    }

    /**
     * Draws a dot in the color of the underground (black, white, ...)
     */
    private void paintUndergroundComponent(final Graphics graph) {
        for (AbstractPilot pilot : pilots) {
            
             //TOON lightsensor tekenen herschrijven niet in viewport
             //(overallviewport, dummyviewport...)
             
            //updateUndergroundCircle(pilot.getLightSensorCoordinates()[0] * scalingfactor, pilot.getLightSensorCoordinates()[1] * scalingfactor, pilot.getLightSensorValue());
        	updateUndergroundCircle();
            if (pilot.getLightSensorValue() < 45)
                ((Graphics2D) graph).setColor(Color.black);
            else if (pilot.getLightSensorValue() > 53)
                ((Graphics2D) graph).setColor(Color.white);
            else
                ((Graphics2D) graph).setColor(new Color(252, 221, 138));

            ((Graphics2D) graph).fill(undergroundCircle);
        }
    }

    private void updateUndergroundCircle() {
        final AbstractPilot pilot = pilots.iterator().next();
        final double diam = scalingfactor * 7;
        undergroundCircle.setFrame(pilot.getPosition().getX() - (diam / 2), pilot.getPosition().getY() - (diam / 2), diam, diam);
    }

    /**
     * The arc is painted light blue when the measurement is not to be trusted
     * (>200 || <20). Otherwise, it is painted in a darker blue.
     */
    private void paintBeamComponent(final Graphics graph) {
        for (AbstractPilot pilot : pilots) {
            //TOON updateArc herschrijven hoort niet in viewport (overallviewport, dummyviewport...) --- WAAROM?
        	updateArc(pilot.getPosition().getX(), pilot.getPosition().getY(), pilot.getAngle(), pilot.getUltraSensorValue());
            ((Graphics2D) graph).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
            if (pilot.getUltraSensorValue() > 200 || pilot.getUltraSensorValue() < 20)
                graph.setColor(new Color(12, 168, 244));
            else
                graph.setColor(new Color(12, 24, 244));
            ((Graphics2D) graph).fill(sonarArc);
        }
    }

    private void updateArc(final double robotX, final double robotY, final double robotAngle, final double USDistance) {
        double correctedUSDistance = USDistance - 5.5;
        final double arcUpperLeftX = robotX - correctedUSDistance;
        final double arcUpperLeftY = robotY - correctedUSDistance;
        final double arcStart = 360 - robotAngle - 15;
        final double arcExtent = 30;

        final double side = 2 * correctedUSDistance;
        sonarArc.setArc(arcUpperLeftX, arcUpperLeftY, side, side, arcStart, arcExtent, Arc2D.PIE);
    }
}
