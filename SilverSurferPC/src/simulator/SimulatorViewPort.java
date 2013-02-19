package simulator;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;

public class SimulatorViewPort extends ViewPort {
    private Arc2D sonarArc = new Arc2D.Double();
    private Ellipse2D undergroundCircle = new Ellipse2D.Double();
    private List<Point> pathCoordinates;

    public SimulatorViewPort() {
        super();
        pathCoordinates = new ArrayList<Point>();

        this.simulationPilot = new SimulationPilot(this);
    }

    public void updateArc(double robotX, double robotY, double robotAngle,
            double USDistance) {
        double correctedUSDistance = USDistance;
        correctedUSDistance = correctedUSDistance - 5.5;
        double arcUpperLeftX = robotX - correctedUSDistance;
        double arcUpperLeftY = robotY - correctedUSDistance;
        double arcStart = 360 - robotAngle - 15;
        double arcExtent = 30;

        double side = 2 * correctedUSDistance;
        this.sonarArc = new Arc2D.Double(arcUpperLeftX, arcUpperLeftY, side,
                side, arcStart, arcExtent, Arc2D.PIE);
    }

    public void updateUndergroundCircle(double robotX, double robotY,
            double LSValue) {

        double diam = scalingfactor * 7;
        undergroundCircle = new Ellipse2D.Double(robotX - (diam / 2), robotY
                - (diam / 2), diam, diam);
    }

    /**
     * The arc is painted light blue when the measurement is not to be trusted
     * (>250). Otherwise, it is painted in a darker blue.
     */

    private void paintBeamComponent(Graphics graph) {

        this.updateArc(this.getSimulationPilot().getUltrasonicSensorPositionX()
                * scalingfactor - getShiftToTheRight(), this
                .getSimulationPilot().getUltrasonicSensorPositionY()
                * scalingfactor - getShiftDown(), this.getSimulationPilot()
                .getAlpha(), this.getSimulationPilot().getUltraSensorValue()
                * getScalingfactor());

        if (simulationPilot != null) {
            ((Graphics2D) graph).setComposite(AlphaComposite.getInstance(
                    AlphaComposite.SRC_OVER, 0.4f));
            if (this.getSimulationPilot().getUltraSensorValue() > 200
                    || this.getSimulationPilot().getUltraSensorValue() < 20) {
                graph.setColor(new Color(12, 168, 244));
            } else {
                graph.setColor(new Color(12, 24, 244));
            }
            ((Graphics2D) graph).fill(sonarArc);
        }
    }

    /**
     * Tekent het pad van de robot en de robot zelf met daarachter het grid.
     * 
     * @param graph
     */
    private void paintPathComponent(Graphics graph) {
        Graphics2D g2 = ((Graphics2D) graph);
        g2.setColor(Color.RED);
        Stroke stroke = g2.getStroke();
        g2.setStroke(new BasicStroke(3));
        for (int i = 0; i < pathCoordinates.size() - 1; i++) {
            Line2D line = new Line2D.Double(pathCoordinates.get(i),
                    pathCoordinates.get(i + 1));
            g2.draw(line);
        }
        g2.setStroke(stroke);
    }

    /**
     * Draws a dot in the color of the underground
     */
    private void paintUndergroundComponent(Graphics graph) {

        this.updateUndergroundCircle(this.getSimulationPilot()
                .getLightsensorPositionX()
                * scalingfactor
                - getShiftToTheRight(), this.getSimulationPilot()
                .getLightsensorPositionY() * scalingfactor - getShiftDown(),
                this.getSimulationPilot().getLightSensorValue());
        if (this.getSimulationPilot().getLightSensorValue() < 45)
            ((Graphics2D) graph).setColor(Color.black);
        else if (this.getSimulationPilot().getLightSensorValue() > 53)
            ((Graphics2D) graph).setColor(Color.white);
        else
            ((Graphics2D) graph).setColor(new Color(252, 221, 138));

        ((Graphics2D) graph).fill(undergroundCircle);

    }

    public void moveRobot(double x, double y, double degrees) {
        x = x * scalingfactor - getShiftToTheRight();
        y = y * scalingfactor - getShiftDown();
        // System.out.println("xy = " + x + " " + y);
        pathCoordinates.get(pathCoordinates.size() - 1).setLocation(x, y);

        // remove the last triangle and draw little circles to indicate the path
        if (getVisibleShapes().size() > 0) {
            double oldX = this.getVisibleTriangle().getGravityCenterX();
            double oldY = this.getVisibleTriangle().getGravityCenterY();

            // add a bigger circle where the robot starts
            if (getVisibleShapes().size() < 3) {
                double diam = scalingfactor * 5;
                Shape bigCircle = new Ellipse2D.Double(oldX - (diam / 2), oldY
                        - (diam / 2), diam, diam);
                getVisibleShapes().add(bigCircle);
            }

        }

        // add a big triangle, indicating the position of the robot and its
        // orientation
        getNotVisibleTriangle().setGravityCenterX(x);
        getNotVisibleTriangle().setGravityCenterY(y);
        getNotVisibleTriangle().setAlpha(degrees);
        setUpdated(true);

        repaint();
    }

    public void clearPath() {
        triangle1 = new Triangle(triangle1.getGravityCenterX(),
                triangle1.getGravityCenterY(), triangle1.getAlpha(),
                scalingfactor);
        triangle2 = new Triangle(triangle2.getGravityCenterX(),
                triangle2.getGravityCenterY(), triangle2.getAlpha(),
                scalingfactor);
        getVisibleShapes().removeAllElements();
        getNotVisibleShapes().removeAllElements();
        getVisibleShapes().add(triangle1);
        getVisibleShapes().add(triangle2);
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

    public void addPathPoint(double x, double y) {
        Point point = new Point();
        point.setLocation(x, y);
        pathCoordinates.add(point);
        repaint();
    }

    /**
     * Methode die alle paint methodes samenvoegd en uitvoert in het JPanel
     */
    @Override
    protected void paintComponent(Graphics graph) {
        super.paintComponent(graph);
        paintPathComponent(graph);
        // paintGridComponent(graph);
        paintUndergroundComponent(graph);
        paintBeamComponent(graph);
    }
}
