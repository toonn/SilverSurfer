package simulator;

import gui.SilverSurferGUI;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.*;

import datastructures.Bag;
import datastructures.Tuple;

import mapping.*;

public class SimulationJPanel extends JPanel implements Runnable,
        MouseMotionListener {

    private SilverSurferGUI SSG;
    private SimulationPilot simulationPilot;
    private MapGraph mapGraphConstructed;
    private double scalingfactor = 1;
    private int shiftToTheRight = 0;
    private int shiftDown = 0;

    // /**
    // * Images die de muur getekend worden (komende van de 8bit Pokemon games!)
    // */
    // private BufferedImage verticalWallImage;
    // private BufferedImage horizontalWallImage;
    /**
     * 2 driehoeken die elkaar afwisselen om afgebeeld te worden de ene wordt
     * afgebeeld terwijl de andere zijn nieuwe coordinaten berekend worden neem
     * Triangle(220,220,270) ipv (0,0,0) om lelijke dot op 0,0 te voorkomen.
     */
    private Triangle triangle1 = new Triangle(5 * getSizeTile() + getSizeTile()
            / 2, 5 * getSizeTile() + getSizeTile() / 2, 270, scalingfactor);
    private Triangle triangle2 = new Triangle(5 * getSizeTile() + getSizeTile()
            / 2, 5 * getSizeTile() + getSizeTile() / 2, 270, scalingfactor);
    /**
     * geeft het getal van de driehoek die afgebeeld wordt
     */
    private int isVisibleTriangle = 1;
    /**
     * is true als de coordinaten van de driehoek die niet afgebeeld wordt,
     * berekend zijn.
     */
    private boolean isUpdatedTriangle = false;

    private Arc2D sonarArc = new Arc2D.Double();
    private Ellipse2D undergroundCircle = new Ellipse2D.Double();

    private Vector<Shape> shapes1 = new Vector<Shape>();
    private Vector<Shape> shapes2 = new Vector<Shape>();

    private boolean isUpdatedShapes = false;
    private boolean isUpdatedWalls = false;
    private boolean isUpdatedBarcodes = false;

    private int isVisibleObjects = 1;

    /**
     * Houdt een map bij met coordinaten die verwijzen naar de muur die erop
     * staat, de positie van de muur ten opzichte van de coordinaten staat
     * uitgelegd in de klasse wall
     */
    private HashMap<Point2D, Wall> walls1 = new HashMap<Point2D, Wall>();
    private HashMap<Point2D, Wall> walls2 = new HashMap<Point2D, Wall>();

    /**
     * A bag doesn't have any ordering, just used for iterating. The placing of
     * these 'barcodes' is specified in the seperate rectangles.
     */
    private Bag<Tuple<Barcode, Rectangle2D[]>> barcodes1 = new Bag<Tuple<Barcode, Rectangle2D[]>>();
    private Bag<Tuple<Barcode, Rectangle2D[]>> barcodes2 = new Bag<Tuple<Barcode, Rectangle2D[]>>();

    /**
     * The rectangle that Highlights the checktile on the gui.
     */
    private Rectangle2D checkHighlight;
    /**
     * The rectangle that Highlights the endtile on the gui.
     */
    private Rectangle2D endHighlight;

    public SimulationJPanel() {
        addMouseMotionListener(this);

        mapGraphConstructed = new MapGraph();

        shapes1.add(triangle1);
        shapes1.add(triangle2);

        this.simulationPilot = new SimulationPilot(this);
    }

    public void run() {
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ee) {
            	
            }
            this.repaint();
        }
    }

    public void addCircle(double x, double y, double degrees) {
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
            // add smaller red circles to indicate the path of the robot
            else {
                double diam = scalingfactor * 2;
                Shape path = new Ellipse2D.Double(x - (diam / 2), y
                        - (diam / 2), diam, diam);
                getVisibleShapes().add(path);
            }
        }

        // add a big triangle, indicating the position of the robot and its
        // orientation
        getNotVisibleTriangle().setGravityCenterX(x);
        getNotVisibleTriangle().setGravityCenterY(y);
        getNotVisibleTriangle().setAlpha(degrees);
        setUpdated(true);
    }

    public void setOtherTriangleVisible() {
        if (isVisibleTriangle == 1)
            isVisibleTriangle = 2;
        else
            isVisibleTriangle = 1;
    }

    public Triangle getVisibleTriangle() {
        if (isVisibleTriangle == 1) {
            return triangle1;
        } else
            return triangle2;
    }

    public Triangle getNotVisibleTriangle() {
        if (isVisibleTriangle == 2) {
            return triangle1;
        } else
            return triangle2;
    }

    public boolean waitingTriangleIsUpdated() {
        return isUpdatedTriangle;
    }

    public void setOtherObjectsVisible() {
        if (isVisibleObjects == 1) {
            isVisibleObjects = 2;
        } else {
            isVisibleObjects = 1;
        }
    }

    public HashMap<Point2D, Wall> getVisibleWalls() {
        if (isVisibleObjects == 1) {
            return walls1;
        } else
            return walls2;
    }

    public Vector<Shape> getVisibleShapes() {
        if (isVisibleObjects == 1) {
            return shapes1;
        } else
            return shapes2;
    }

    public Bag<Tuple<Barcode, Rectangle2D[]>> getVisibleBarcode() {
        if (isVisibleObjects == 1) {
            return barcodes1;
        } else
            return barcodes2;
    }

    public Vector<Shape> getNotVisibleShapes() {
        if (isVisibleObjects == 2) {
            return shapes1;
        } else
            return shapes2;
    }

    public HashMap<Point2D, Wall> getNotVisibleWalls() {
        if (isVisibleObjects == 2) {
            return walls1;
        } else
            return walls2;
    }

    public Bag<Tuple<Barcode, Rectangle2D[]>> getNotVisibleBarcode() {
        if (isVisibleObjects == 2) {
            return barcodes1;
        } else
            return barcodes2;
    }

    public boolean waitingObjectsAreUpdated() {
        return isUpdatedShapes && isUpdatedWalls && isUpdatedBarcodes;
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

    public void setUpdated(boolean isUpdated) {
        this.isUpdatedTriangle = isUpdated;
    }

    public void updateUndergroundCircle(double robotX, double robotY,
            double LSValue) {

        double diam = scalingfactor * 7;
        undergroundCircle = new Ellipse2D.Double(robotX - (diam / 2), robotY
                - (diam / 2), diam, diam);
    }

    /**
     * Methode die alle paint methodes samenvoegd en uitvoert in het JPanel
     */
    @Override
    protected void paintComponent(Graphics graph) {

        paintPathComponent(graph);
        // paintGridComponent(graph);
        paintWallComponent(graph);
        paintUndergroundComponent(graph);
        paintBeamComponent(graph);
        paintHighLightComponents(graph);

        // paintGridComponent(graph);
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
     * tekent de muren op het JPanel paneel
     * 
     * @param graph
     */
    private void paintWallComponent(Graphics graph) {

        ((Graphics2D) graph).setColor(Color.BLACK);

        for (Wall wall : getVisibleWalls().values()) {
            ((Graphics2D) graph).fill(wall);

            // graph.drawImage(getVerticalWallImage(), wall.x, wall.y, null);
            // else graph.drawImage(getHorizontalWallImage(), wall.x, wall.y,
            // null);
        }
    }

    // /**
    // * Tekent het grid (rooster) op de achtergrond van de mapping
    // * @param graph
    // */
    // private void paintGridComponent(Graphics graph) {
    //
    // int count = 50;
    // int size = (int) getSizeTile();
    //
    // ((Graphics2D) graph).setColor(Color.lightGray);
    //
    // for( int i = 0; i < count; i ++)
    // for( int j = 0; j < count; j++)
    // {
    // Rectangle grid = new Rectangle( i * size,j * size, size, size);
    // ((Graphics2D) graph).draw(grid);
    // }
    // }

    /**
     * Tekent het pad van de robot en de robot zelf met daarachter het grid.
     * 
     * @param graph
     */
    private void paintPathComponent(Graphics graph) {
        super.paintComponent(graph);
        paintBarcodeComponent(graph);

        Vector<Shape> shapesx = new Vector<Shape>();
        shapesx.addAll(getVisibleShapes());

        ((Graphics2D) graph).setColor(Color.red);

        if (isUpdatedTriangle) {
            setOtherTriangleVisible();
            setUpdated(false);
        }

        int count = 50;
        int size = (int) getSizeTile();

        ((Graphics2D) graph).setColor(Color.lightGray);

        for (int i = 0; i < count; i++)
            for (int j = 0; j < count; j++) {
                Rectangle grid = new Rectangle(i * size - getShiftToTheRight(),
                        j * size - getShiftDown(), size, size);
                ((Graphics2D) graph).draw(grid);
            }

        ((Graphics2D) graph).setColor(Color.red);
        for (Shape s : shapesx)

        {

            int x;
            int y;

            if (s instanceof Triangle) {
                if (s.equals(getVisibleTriangle()))
                    ((Graphics2D) graph).fill(s);
                x = (int) ((Triangle) s).getGravityCenterX();
                y = (int) ((Triangle) s).getGravityCenterY();

                if (simulationPilot != null)
                    getSSG().updateCoordinates(
                            "Simulator ("
                                    + simulationPilot
                                            .getCurrentPositionAbsoluteX()
                                    + " , "
                                    + simulationPilot
                                            .getCurrentPositionAbsoluteY()
                                    + " , " + simulationPilot.getAlpha()
                                    + "\u00B0, Map: "
                                    + simulationPilot.getMapString() + ")");
                else
                    getSSG().updateCoordinates(
                            "Simulator (" + (x) + " , " + (y) + ")");

            } else {
                ((Graphics2D) graph).fill(s);
            }
        }

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

    private void paintBarcodeComponent(Graphics graph) {

        // teken alle rectangles van alle barcodes
        for (Tuple t : getVisibleBarcode()) {
            String rep = ((Barcode) t.getItem1()).toString();
            Rectangle2D[] bc = (Rectangle2D[]) t.getItem2();
            for (int i = 0; i < 8; i++) {
                if (rep.charAt(i) == '0')
                    ((Graphics2D) graph).setColor(Color.black);
                else
                    ((Graphics2D) graph).setColor(Color.white);

                ((Graphics2D) graph).fill(bc[i]);

            }
        }

    }

    private void paintHighLightComponents(Graphics graph) {
        if (checkHighlight != null) {
            ((Graphics2D) graph).setColor(Color.ORANGE);
            ((Graphics2D) graph).fill(checkHighlight);
        }
        if (endHighlight != null) {
            ((Graphics2D) graph).setColor(Color.ORANGE);
            ((Graphics2D) graph).fill(endHighlight);
        }
    }

    public void setCheckHighlight() {
        checkHighlight = new Rectangle(
                ((Double) (simulationPilot.getCenterAbsoluteCurrentTile()[0] - 20 * scalingfactor)).intValue()
                        - getShiftToTheRight(),
                ((Double) (simulationPilot.getCenterAbsoluteCurrentTile()[1] - 20 * scalingfactor))
                        .intValue() - getShiftDown(), (int) getSizeTile(),
                (int) getSizeTile());
    }

    public void setEndHighlight() {
        endHighlight = new Rectangle(
                ((Double) (simulationPilot.getCenterAbsoluteCurrentTile()[0] - 20 * scalingfactor)).intValue()
                        - getShiftToTheRight(),
                ((Double) (simulationPilot.getCenterAbsoluteCurrentTile()[1] - 20 * scalingfactor))
                        .intValue() - getShiftDown(), (int) getSizeTile(),
                (int) getSizeTile());
    }

    public void setRobotLocation(double x, double y, double degrees) {
        x = x * scalingfactor - getShiftToTheRight();
        y = y * scalingfactor - getShiftDown();
        // System.out.println("xy = " + x + " " + y);
        this.addCircle(x * 1, y * 1, degrees);
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
     * Deletes the former path of the robot
     */
    public void resetPath() {

        getVisibleShapes().removeAllElements();
        getNotVisibleShapes().removeAllElements();
        shiftToTheRight = 0;
        shiftDown = 0;
        scalingfactor = 1;
        triangle1 = new Triangle(5 * getSizeTile() + getSizeTile() / 2, 5
                * getSizeTile() + getSizeTile() / 2, 270, scalingfactor);
        triangle2 = new Triangle(5 * getSizeTile() + getSizeTile() / 2, 5
                * getSizeTile() + getSizeTile() / 2, 270, scalingfactor);
        shapes1.add(triangle1);
        shapes1.add(triangle2);
        isVisibleObjects = 1;
    }

    /**
     * Removes all Wall-objects this panel is keeping track of.
     */
    public void removeWalls() {
        getVisibleWalls().clear();
        getNotVisibleWalls().clear();
    }

    /**
     * Removes all Barcodes this panel is keeping track of.
     */
    public void removeBarCodes() {
        barcodes1 = new Bag<Tuple<Barcode, Rectangle2D[]>>();
        barcodes2 = new Bag<Tuple<Barcode, Rectangle2D[]>>();
    }

    /**
     * Deletes the former path of the robot and all the walls that have been
     * explored as yet.
     */
    public void clearTotal() {
        endHighlight = null;
        checkHighlight = null;
        resetPath();
        removeWalls();
        removeBarCodes();
    }

    /**
     * Deletes the constructed mapGraph and sets up a new one. Also calls
     * clearTotal() and resets the triangles. (angle 270);
     */
    public void resetMap() {
        clearTotal();
        SSG.getStatusInfoBuffer().resetBuffer();
        mapGraphConstructed = new MapGraph();
        getSimulationPilot().reset();

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

    public double getSizeTile() {
        return 40 * scalingfactor;
    }

    public double getScalingfactor() {
        return scalingfactor;
    }

    public void setScalingfactor(double scalingfactor) {
        scaleWalls(scalingfactor);
        scaleShapes(scalingfactor);
        scaleBarcodes(scalingfactor);
        scaleEndAndCheckTile(scalingfactor);

        if (waitingObjectsAreUpdated()) {
            setOtherObjectsVisible();
        }

        isUpdatedShapes = false;
        isUpdatedWalls = false;
        isUpdatedBarcodes = false;

        getNotVisibleBarcode().empty();
        getNotVisibleWalls().clear();
        getNotVisibleShapes().removeAllElements();

        this.scalingfactor = scalingfactor;

    }

    private void scaleEndAndCheckTile(double scalingfactor) {
        if (endHighlight != null) {
            endHighlight = new Rectangle(
                    (int) ((endHighlight.getX() + getShiftToTheRight())
                            * scalingfactor / getScalingfactor() - getShiftToTheRight()),
                    (int) ((endHighlight.getY() + getShiftDown())
                            * scalingfactor / getScalingfactor() - getShiftDown()),
                    (int) (endHighlight.getHeight() * scalingfactor / getScalingfactor()),
                    (int) (endHighlight.getWidth() * scalingfactor / getScalingfactor()));
        }
        if (checkHighlight != null) {
            checkHighlight = new Rectangle(
                    (int) ((checkHighlight.getX() + getShiftToTheRight())
                            * scalingfactor / getScalingfactor() - getShiftToTheRight()),
                    (int) ((checkHighlight.getY() + getShiftDown())
                            * scalingfactor / getScalingfactor() - getShiftDown()),
                    (int) (checkHighlight.getHeight() * scalingfactor / getScalingfactor()),
                    (int) (checkHighlight.getWidth() * scalingfactor / getScalingfactor()));
        }
    }

    /**
     * Updates the width/height/coordinates of the barcodes.
     */
    private void scaleBarcodes(double scalingfactor) {

        Bag<Tuple<Barcode, Rectangle2D[]>> storeBarcodes = new Bag<Tuple<Barcode, Rectangle2D[]>>();

        for (Tuple t : getVisibleBarcode()) {
            storeBarcodes.add(t);
        }
        // TODO
        // if(checkHighlight != null){
        // Rectangle2D temp = new
        // Rectangle(((Double)(checkHighlight.getX()*getScalingfactor())).intValue(),((Double)(checkHighlight.getY()*getScalingfactor())).intValue(),((Double)(checkHighlight.getWidth()*getScalingfactor())).intValue(),((Double)(checkHighlight.getHeight()*getScalingfactor())).intValue());
        // checkHighlight = null;
        // checkHighlight = temp;
        // }
        //
        // if(endHighlight != null){
        // Rectangle2D temp = new
        // Rectangle(((Double)(endHighlight.getX()*getScalingfactor())).intValue(),((Double)(endHighlight.getY()*getScalingfactor())).intValue(),((Double)(endHighlight.getWidth()*getScalingfactor())).intValue(),((Double)(endHighlight.getHeight()*getScalingfactor())).intValue());
        // endHighlight = null;
        // endHighlight = temp;
        // }

        for (Tuple t : storeBarcodes) {
            Barcode barcode = (Barcode) t.getItem1();
            int newX = (int) ((barcode.getDrawnCenterX() + getShiftToTheRight())
                    * scalingfactor / getScalingfactor() - getShiftToTheRight());
            int newY = (int) ((barcode.getDrawnCenterY() + getShiftDown())
                    * scalingfactor / getScalingfactor() - getShiftDown());
            Rectangle2D[] rect = Barcode.createVisualBarCode(barcode, newX,
                    newY, 40 * scalingfactor);
            getNotVisibleBarcode().add(
                    new Tuple<Barcode, Rectangle2D[]>(barcode, rect));
        }

        isUpdatedBarcodes = true;

    }

    private void scaleShapes(double scalingfactor) {

        for (Shape shape : getVisibleShapes()) {
            if (shape instanceof Triangle) {

                double newGravityCenterX = (((Triangle) shape)
                        .getGravityCenterX() + getShiftToTheRight())
                        * scalingfactor
                        / this.getScalingfactor()
                        - getShiftToTheRight();
                double newGravityCenterY = (((Triangle) shape)
                        .getGravityCenterY() + getShiftDown())
                        * scalingfactor
                        / this.getScalingfactor() - getShiftDown();

                ((Triangle) shape).setGravityCenterX(newGravityCenterX);
                ((Triangle) shape).setGravityCenterY(newGravityCenterY);
                ((Triangle) shape).setScalingfactor(scalingfactor);

                getNotVisibleShapes().add(shape);

            }

            else {
                double oldDiam = ((Ellipse2D.Double) shape).getHeight();
                double OldXCoordinate = ((Ellipse2D.Double) shape).getX()
                        + oldDiam / 2;
                double OldYCoordinate = ((Ellipse2D.Double) shape).getY()
                        + oldDiam / 2;

                double newDiam = oldDiam * scalingfactor
                        / this.getScalingfactor();
                double newXCoordinate = ((OldXCoordinate - (newDiam / 2)) + getShiftToTheRight())
                        * scalingfactor
                        / this.getScalingfactor()
                        - getShiftToTheRight();
                double newYCoordinate = ((OldYCoordinate - (newDiam / 2)) + getShiftDown())
                        * scalingfactor
                        / this.getScalingfactor()
                        - getShiftDown();

                Ellipse2D.Double circle = new Ellipse2D.Double(newXCoordinate,
                        newYCoordinate, newDiam, newDiam);

                getNotVisibleShapes().add(circle);
            }
        }
        isUpdatedShapes = true;
    }

    private void scaleWalls(double scalingfactor) {

        Vector oldPoints = new Vector<Point2D>();
        for (Point2D point : getVisibleWalls().keySet()) {
            oldPoints.add(point);
        }

        for (Object point : oldPoints) {

            Wall wall = getVisibleWalls().get(point);

            double pointWithoutShiftX = ((Point2D) point).getX()
                    + getShiftToTheRight();
            double pointWithoutShiftY = ((Point2D) point).getY()
                    + getShiftDown();

            int whichXTile = (int) Math.round(pointWithoutShiftX
                    / getSizeTile());
            int whichYTile = (int) Math.round(pointWithoutShiftY
                    / getSizeTile());

            double newXCoordinate = whichXTile * (40 * scalingfactor)
                    - getShiftToTheRight();
            double newYCoordinate = whichYTile * (40 * scalingfactor)
                    - getShiftDown();

            int newWidth = (int) Math
                    .round((wall.getWidth() * scalingfactor / this
                            .getScalingfactor()));
            int newHeight = (int) Math
                    .round((wall.getHeight() * scalingfactor / this
                            .getScalingfactor()));

            if (wall.getState() == State.HORIZONTAL) {
                newYCoordinate = newYCoordinate
                        - (Wall.getStandardWidth() * scalingfactor) / 2;
            }

            else {
                newXCoordinate = newXCoordinate
                        - (Wall.getStandardWidth() * scalingfactor) / 2;
            }

            Wall newWall = new Wall(wall.getState(), newXCoordinate,
                    newYCoordinate, newWidth, newHeight);

            getNotVisibleWalls()
                    .put(new Point2D.Double(newXCoordinate, newYCoordinate),
                            newWall);

        }

        isUpdatedWalls = true;

    }

    public int getShiftToTheRight() {
        return shiftToTheRight;
    }

    public void setShiftToTheRight(int shiftRight) {
        this.shiftToTheRight = this.shiftToTheRight + shiftRight;
        shiftBarcodes(true, shiftRight);
        shiftWalls(true, shiftRight);
        shiftShapes(true, shiftRight);
        shiftEndAndCheckTile(true, shiftRight);

        if (waitingObjectsAreUpdated()) {
            setOtherObjectsVisible();
        }

        isUpdatedShapes = false;
        isUpdatedWalls = false;
        isUpdatedBarcodes = false;

        getNotVisibleBarcode().empty();
        getNotVisibleWalls().clear();
        getNotVisibleShapes().removeAllElements();

    }

    public int getShiftDown() {
        return shiftDown;
    }

    public void setShiftDown(int shiftDown) {
        this.shiftDown = this.shiftDown + shiftDown;
        shiftWalls(false, shiftDown);
        shiftShapes(false, shiftDown);
        shiftBarcodes(false, shiftDown);
        shiftEndAndCheckTile(false, shiftDown);

        setOtherObjectsVisible();

        isUpdatedShapes = false;
        isUpdatedWalls = false;
        isUpdatedBarcodes = false;

        getNotVisibleBarcode().empty();
        getNotVisibleWalls().clear();
        getNotVisibleShapes().removeAllElements();
    }

    private void shiftEndAndCheckTile(boolean shiftHorizontal, int shift) {
        if (shiftHorizontal) {
            if (endHighlight != null) {
                endHighlight = new Rectangle(
                        (int) (endHighlight.getX() - shift),
                        (int) endHighlight.getY(),
                        (int) endHighlight.getHeight(),
                        (int) endHighlight.getWidth());
            }
            if (checkHighlight != null) {
                checkHighlight = new Rectangle(
                        (int) (checkHighlight.getX() - shift),
                        (int) checkHighlight.getY(),
                        (int) checkHighlight.getHeight(),
                        (int) checkHighlight.getWidth());
            }
        } else {
            if (endHighlight != null) {
                endHighlight = new Rectangle((int) endHighlight.getX(),
                        (int) (endHighlight.getY() - shift),
                        (int) endHighlight.getHeight(),
                        (int) endHighlight.getWidth());
            }
            if (checkHighlight != null) {
                checkHighlight = new Rectangle((int) checkHighlight.getX(),
                        (int) (checkHighlight.getY() - shift),
                        (int) checkHighlight.getHeight(),
                        (int) checkHighlight.getWidth());
            }
        }
    }

    private void shiftBarcodes(boolean shiftHorizontal, int shift) {

        Bag<Tuple<Barcode, Rectangle2D[]>> storeBarcodes = new Bag<Tuple<Barcode, Rectangle2D[]>>();

        for (Tuple t : getVisibleBarcode()) {
            storeBarcodes.add(t);
        }

        for (Tuple t : storeBarcodes) {
            Barcode barcode = (Barcode) t.getItem1();

            int newX = (int) (barcode.getDrawnCenterX());
            int newY = (int) (barcode.getDrawnCenterY());

            if (shiftHorizontal) {
                newX = newX - shift;
            } else {
                newY = newY - shift;
            }

            Rectangle2D[] rect = Barcode.createVisualBarCode(barcode, newX,
                    newY, getSizeTile());
            getNotVisibleBarcode().add(
                    new Tuple<Barcode, Rectangle2D[]>(barcode, rect));
        }

        isUpdatedBarcodes = true;

    }

    private void shiftWalls(boolean shiftHorizontal, int shift) {
        Vector oldPoints = new Vector<Point2D>();
        for (Point2D point : getVisibleWalls().keySet()) {
            oldPoints.add(point);
        }

        for (Object point : oldPoints) {

            Wall wall = getVisibleWalls().get(point);

            double newXCoordinate = ((Point2D.Double) point).getX();
            double newYCoordinate = ((Point2D.Double) point).getY();

            if (shiftHorizontal) {
                newXCoordinate = wall.getX() - shift;
            } else {
                newYCoordinate = wall.getY() - shift;
            }
            wall.setLocation((int) newXCoordinate, (int) newYCoordinate);

            getNotVisibleWalls().put(
                    new Point2D.Double(newXCoordinate, newYCoordinate), wall);
        }

        isUpdatedWalls = true;
    }

    private void shiftShapes(boolean shiftHorizontal, int shift) {

        Vector<Shape> newShapes = new Vector<Shape>();

        for (Shape shape : getVisibleShapes()) {
            if (shape instanceof Triangle) {

                double newGravityCenterX = ((Triangle) shape)
                        .getGravityCenterX();
                double newGravityCenterY = ((Triangle) shape)
                        .getGravityCenterY();

                if (shiftHorizontal) {
                    newGravityCenterX = newGravityCenterX - shift;
                } else {
                    newGravityCenterY = newGravityCenterY - shift;
                }

                ((Triangle) shape).setGravityCenterX(newGravityCenterX);
                ((Triangle) shape).setGravityCenterY(newGravityCenterY);

                // Triangle triangle = new Triangle(newGravityCenterX,
                // newGravityCenterY, ((Triangle) shape).getAlpha(), ((Triangle)
                // shape).getScalingfactor());

                getNotVisibleShapes().add(shape);
            }

            else {
                double diam = ((Ellipse2D.Double) shape).getHeight();
                double newXCoordinate = ((Ellipse2D.Double) shape).getX();
                double newYCoordinate = ((Ellipse2D.Double) shape).getY();

                if (shiftHorizontal) {
                    newXCoordinate = newXCoordinate - shift;
                } else {
                    newYCoordinate = newYCoordinate - shift;
                }

                Ellipse2D.Double circle = new Ellipse2D.Double(newXCoordinate,
                        newYCoordinate, diam, diam);

                getNotVisibleShapes().add(circle);
            }
        }

        isUpdatedShapes = true;

    }

    //
    // /**
    // * verwijdert de muur als er een muur staat,
    // * als er geen muur staat, return
    // */
    // public void addWhiteLine(Orientation orientation, double x, double y)
    // {
    // Point2D point = calculateWallPoint(orientation, x, y);
    // if(!walls.containsKey(point))
    // return;
    // removeWallFrom(point);
    // }

    /**
     * voegt een muur bij aan hashmap muur is afhankelijk van de orientatie
     */
    public void addWall(Orientation orientation, double x, double y) {
        Point2D point = calculateWallPoint(orientation, x, y);
        point.setLocation(point.getX() - getShiftToTheRight(), point.getY()
                - getShiftDown());
        Wall wall;
        if (orientation.equals(Orientation.NORTH)
                || orientation.equals(Orientation.SOUTH)) {
            wall = new Wall(State.HORIZONTAL, (double) point.getX(),
                    (double) point.getY(), scalingfactor);
        } else {
            wall = new Wall(State.VERTICAL, (double) point.getX(),
                    (double) point.getY(), scalingfactor);
        }
        setWall(point, wall);
    }

    /**
     * Voegt een barcode toe aan de bag met barcodes.
     * 
     * @pre De posities van de rectangles etc moeten nu al helemaal ingevuld
     *      zijn.
     */
    public void addBarcode(Barcode barcode, Rectangle2D[] visual) {
        getVisibleBarcode().add(
                new Tuple<Barcode, Rectangle2D[]>(barcode, visual));
    }

    public void setBarcode(Barcode barcode) {
        if (!barcode.isDrawn()) {
            Rectangle2D[] rect = Barcode.createVisualBarCode(barcode,
                    getSimulationPilot().getCenterAbsoluteCurrentTile()[0]
                            - getShiftToTheRight(),
                    getSimulationPilot().getCenterAbsoluteCurrentTile()[1]
                            - getShiftDown(), getSizeTile());
            addBarcode(barcode, rect);
            barcode.setDrawn(true);
        }
    }

    public void setWall(Point2D point, Wall wall) {
        getVisibleWalls().put(point, wall);
    }

    public void removeWallFrom(Point2D point) {
        getVisibleWalls().remove(point);
    }

    public void setTile(int x, int y) {
        getMapGraphConstructed().setTileXY(x, y, new Tile());
    }

    public void setWallOnTile(int x, int y, Orientation orientation) {
        if (getMapGraphConstructed().getTileWithCoordinates(x, y) == null)
            throw new IllegalArgumentException(
                    "in simulationPanel bij methode SetWallOnTile "
                            + "zijn coordinaten meegegeven die de mapgraph niet bevat nl"
                            + x + " en " + y);
        getMapGraphConstructed().getTileWithCoordinates(x, y)
                .getEdge(orientation).setObstruction(Obstruction.WALL);
    }

    public void removeWallFromTile(int x, int y, Orientation orientation) {
        if (getMapGraphConstructed().getTileWithCoordinates(x, y) == null)
            throw new IllegalArgumentException(
                    "in simulationPanel bij methode removeWallFromTile "
                            + "zijn coordinaten meegegeven die de mapgraph niet bevat");
        getMapGraphConstructed().getTileWithCoordinates(x, y)
                .getEdge(orientation).setObstruction(null);
    }

    /**
     * Deze methode berekent afhankelijk van de orientatie en het punt dat wordt
     * meegegeven een punt dat wordt gebruikt om de wall te positioneren! voor
     * noord en west is dit dus het linkerhoekbovenpunt van het tile voor zuid
     * het rechterbovenhoekpunt en voor zuid het linkeronderhoekpunt (aan wall
     * wordt altijd het midden van de linker (als wall horizontaal ligt) of
     * boven(als wall rechtopstaat) breedte meegegeven (ik heb het midden gepakt
     * en laten meegeven waarna het in wall verwerkt wordt tot het
     * linkerbovenhoekpunt van de 'rectangle omdat dit makkelijk is om af te
     * ronden tot op een veelvoud van 40)
     */
    private Point2D calculateWallPoint(Orientation orientation, double x,
            double y) {

        double xCoordinate = (Math.floor(x / 40) * getSizeTile());
        double yCoordinate = (Math.floor(y / 40) * getSizeTile());

        if (orientation.equals(Orientation.NORTH)) {
            yCoordinate = yCoordinate
                    - (Wall.getStandardWidth() * scalingfactor) / 2;
        }

        else if (orientation.equals(Orientation.SOUTH)) {
            yCoordinate = yCoordinate + getSizeTile()
                    - (Wall.getStandardWidth() * scalingfactor) / 2;
        } else if (orientation.equals(Orientation.EAST)) {
            xCoordinate = xCoordinate + getSizeTile()
                    - (Wall.getStandardWidth() * scalingfactor) / 2;
        } else {
            xCoordinate = xCoordinate
                    - (Wall.getStandardWidth() * scalingfactor) / 2;
        }

        Point2D point = new Point2D.Double(xCoordinate, yCoordinate);
        return point;
    }

    private int lastMousePositionX;
    private int lastMousePositionY;

    @Override
    public void mouseDragged(MouseEvent e) {
        setShiftDown(lastMousePositionY - (int) e.getPoint().getY());
        setShiftToTheRight(lastMousePositionX - (int) e.getPoint().getX());
        lastMousePositionX = (int) e.getPoint().getX();
        lastMousePositionY = (int) e.getPoint().getY();

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        lastMousePositionX = (int) e.getPoint().getX();
        lastMousePositionY = (int) e.getPoint().getY();
    }

}

// public BufferedImage getVerticalWallImage() {
// return verticalWallImage;
// }
// public BufferedImage getHorizontalWallImage() {
// return horizontalWallImage;
// }

