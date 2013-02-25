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
import java.util.List;

import simulator.pilot.AbstractPilot;

public class SimulationViewPort extends ViewPort {
    private Arc2D sonarArc = new Arc2D.Double();
    private Ellipse2D undergroundCircle = new Ellipse2D.Double();
    private List<Point> pathCoordinates;

    public SimulationViewPort(final AbstractPilot pilot) {
        super();
        pathCoordinates = new ArrayList<Point>();
        addPathPoint(pilot.getStartPositionAbsoluteX(),
                pilot.getStartPositionAbsoluteY());
        pilots.add(pilot);
    }

    public void addPathPoint(final double x, final double y) {
        final Point point = new Point();
        point.setLocation(x, y);
        pathCoordinates.add(point);
        repaint();
    }

    public void clearPath() {
        pathCoordinates = new ArrayList<Point>();
        for (final AbstractPilot pilot : pilots) {
            addPathPoint(pilot.getCurrentPositionAbsoluteX(),
                    pilot.getCurrentPositionAbsoluteY());
        }
    }

    /**
     * Deletes the former path of the robot and all the walls that have been
     * explored as yet.
     */
    @Override
    public void clearTotal() {
        super.clearTotal();
        clearPath();

    }

    @Override
    public void moveRobot(double x, double y, final double degrees) {
        x = x * scalingfactor - getShiftToTheRight();
        y = y * scalingfactor - getShiftDown();
        // System.out.println("xy = " + x + " " + y);
        pathCoordinates.get(pathCoordinates.size() - 1).setLocation(x, y);

        repaint();
    }

    /**
     * The arc is painted light blue when the measurement is not to be trusted
     * (>250). Otherwise, it is painted in a darker blue.
     */

    private void paintBeamComponent(final Graphics graph) {
        for (AbstractPilot pilot : pilots) {
            /* TODO updateArc herschrijven */
            // updateArc(pilot.getUltrasonicSensorPositionX() * scalingfactor
            // - getShiftToTheRight(),
            // pilot.getUltrasonicSensorPositionY() * scalingfactor
            // - getShiftDown(), pilot.getAlpha(),
            // pilot.getUltraSensorValue() * getScalingfactor());

            ((Graphics2D) graph).setComposite(AlphaComposite.getInstance(
                    AlphaComposite.SRC_OVER, 0.4f));
            if (pilot.getUltraSensorValue() > 200
                    || pilot.getUltraSensorValue() < 20) {
                graph.setColor(new Color(12, 168, 244));
            } else {
                graph.setColor(new Color(12, 24, 244));
            }
            ((Graphics2D) graph).fill(sonarArc);
        }
    }

    /**
     * Methode die alle paint methodes samenvoegd en uitvoert in het JPanel
     */
    @Override
    protected void paintComponent(final Graphics graph) {
        super.paintComponent(graph);
        paintPathComponent(graph);
        // paintGridComponent(graph);
        paintUndergroundComponent(graph);
        paintBeamComponent(graph);
    }

    /**
     * Tekent het pad van de robot en de robot zelf met daarachter het grid.
     * 
     * @param graph
     */
    private void paintPathComponent(final Graphics graph) {
        final Graphics2D g2 = ((Graphics2D) graph);
        g2.setColor(Color.RED);
        final Stroke stroke = g2.getStroke();
        g2.setStroke(new BasicStroke(3));
        for (int i = 0; i < pathCoordinates.size() - 1; i++) {
            final Line2D line = new Line2D.Double(pathCoordinates.get(i),
                    pathCoordinates.get(i + 1));
            g2.draw(line);
        }
        final double radius = 3;
        final double diameter = 2 * radius;
        final double upperleftCornerX = pathCoordinates.get(0).x - radius;
        final double upperleftCornerY = pathCoordinates.get(0).y - radius;
        new Ellipse2D.Double(upperleftCornerX, upperleftCornerY, diameter,
                diameter);
        g2.setStroke(stroke);
    }

    /**
     * Draws a dot in the color of the underground
     */
    private void paintUndergroundComponent(final Graphics graph) {
        for (AbstractPilot pilot : pilots) {
            /* TODO lightsensor tekenen herschrijven */
            // updateUndergroundCircle(pilot.getLightsensorPositionX()
            // * scalingfactor - getShiftToTheRight(),
            // pilot.getLightsensorPositionY() * scalingfactor
            // - getShiftDown(), pilot.getLightSensorValue());
            if (pilot.getLightSensorValue() < 45) {
                ((Graphics2D) graph).setColor(Color.black);
            } else if (pilot.getLightSensorValue() > 53) {
                ((Graphics2D) graph).setColor(Color.white);
            } else {
                ((Graphics2D) graph).setColor(new Color(252, 221, 138));
            }

            ((Graphics2D) graph).fill(undergroundCircle);
        }
    }

    public void updateArc(final double robotX, final double robotY,
            final double robotAngle, final double USDistance) {
        double correctedUSDistance = USDistance;
        correctedUSDistance = correctedUSDistance - 5.5;
        final double arcUpperLeftX = robotX - correctedUSDistance;
        final double arcUpperLeftY = robotY - correctedUSDistance;
        final double arcStart = 360 - robotAngle - 15;
        final double arcExtent = 30;

        final double side = 2 * correctedUSDistance;
        sonarArc = new Arc2D.Double(arcUpperLeftX, arcUpperLeftY, side, side,
                arcStart, arcExtent, Arc2D.PIE);
    }

    public void updateUndergroundCircle(final double robotX,
            final double robotY, final double LSValue) {

        final double diam = scalingfactor * 7;
        undergroundCircle = new Ellipse2D.Double(robotX - (diam / 2), robotY
                - (diam / 2), diam, diam);
    }
}
