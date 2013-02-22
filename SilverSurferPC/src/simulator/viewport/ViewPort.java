package simulator.viewport;

import gui.SilverSurferGUI;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.JPanel;

import mapping.Barcode;
import mapping.Orientation;
import simulator.State;
import simulator.Wall;
import simulator.pilot.AbstractPilot;
import datastructures.Bag;
import datastructures.Tuple;

public abstract class ViewPort extends JPanel implements MouseMotionListener {

    protected Set<AbstractPilot> pilots;
    protected double scalingfactor = 1;
    private int shiftToTheRight = 0;
    private int shiftDown = 0;

    private int isVisibleObjects = 1;

    /**
     * Houdt een map bij met coordinaten die verwijzen naar de muur die erop
     * staat, de positie van de muur ten opzichte van de coordinaten staat
     * uitgelegd in de klasse wall
     */
    private final HashMap<Point2D, Wall> walls1 = new HashMap<Point2D, Wall>();
    private final HashMap<Point2D, Wall> walls2 = new HashMap<Point2D, Wall>();

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

    // boolean[] voor zwart of wit
    private Map<Rectangle2D[], boolean[]> barcodeRectangles;

    private int lastMousePositionX;

    private int lastMousePositionY;

    public ViewPort() {
        barcodeRectangles = new HashMap<Rectangle2D[], boolean[]>();
        addMouseMotionListener(this);
    }

    /**
     * voegt een muur bij aan hashmap muur is afhankelijk van de orientatie
     */
    public void addWall(final Orientation orientation, final double x,
            final double y) {
        final Point2D point = calculateWallPoint(orientation, x, y);
        point.setLocation(point.getX() - getShiftToTheRight(), point.getY()
                - getShiftDown());
        Wall wall;
        if (orientation.equals(Orientation.NORTH)
                || orientation.equals(Orientation.SOUTH)) {
            wall = new Wall(State.HORIZONTAL, point.getX(), point.getY(),
                    scalingfactor);
        } else {
            wall = new Wall(State.VERTICAL, point.getX(), point.getY(),
                    scalingfactor);
        }
        setWall(point, wall);
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
    private Point2D calculateWallPoint(final Orientation orientation,
            final double x, final double y) {

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

        final Point2D point = new Point2D.Double(xCoordinate, yCoordinate);
        return point;
    }

    /**
     * Deletes the former path of the robot and all the walls that have been
     * explored as yet.
     */
    public void clearTotal() {
        endHighlight = null;
        checkHighlight = null;
        removeWalls();
        removeBarCodes();
    }

    public Bag<Tuple<Barcode, Rectangle2D[]>> getNotVisibleBarcode() {
        if (isVisibleObjects == 2) {
            return barcodes1;
        } else {
            return barcodes2;
        }
    }

    public HashMap<Point2D, Wall> getNotVisibleWalls() {
        if (isVisibleObjects == 2) {
            return walls1;
        } else {
            return walls2;
        }
    }

    public double getScalingfactor() {
        return scalingfactor;
    }

    public int getShiftDown() {
        return shiftDown;
    }

    public int getShiftToTheRight() {
        return shiftToTheRight;
    }

    public double getSizeTile() {
        return 40 * scalingfactor;
    }

    public Bag<Tuple<Barcode, Rectangle2D[]>> getVisibleBarcode() {
        if (isVisibleObjects == 1) {
            return barcodes1;
        } else {
            return barcodes2;
        }
    }

    public HashMap<Point2D, Wall> getVisibleWalls() {
        if (isVisibleObjects == 1) {
            return walls1;
        } else {
            return walls2;
        }
    }

    @Override
    public void mouseDragged(final MouseEvent e) {
        setShiftDown(lastMousePositionY - (int) e.getPoint().getY());
        setShiftToTheRight(lastMousePositionX - (int) e.getPoint().getX());
        lastMousePositionX = (int) e.getPoint().getX();
        lastMousePositionY = (int) e.getPoint().getY();

    }

    @Override
    public void mouseMoved(final MouseEvent e) {
        lastMousePositionX = (int) e.getPoint().getX();
        lastMousePositionY = (int) e.getPoint().getY();
    }

    public void moveRobot(final double x, final double y, final double degrees) {
    }

    private void paintBarcodeComponent(final Graphics graph) {
        // TODO Werkt niet als er meer dan 1 pilot in pilots zit.
        for (final AbstractPilot pilot : pilots) {
            if (pilot.getBarcodes().size() != barcodeRectangles.size()) {
                barcodeRectangles = new HashMap<Rectangle2D[], boolean[]>();
                for (final Barcode barcode : pilot.getBarcodes()) {
                    barcodeRectangles.put(
                            barcode.createVisualBarCode(getSizeTile()),
                            barcode.getBoolRep());
                }
            }
        }

        final Graphics2D g2 = ((Graphics2D) graph);
        for (final Rectangle2D[] barcodeRectangle : barcodeRectangles.keySet()) {
            final boolean[] boolRep = barcodeRectangles.get(barcodeRectangle);
            for (int i = 0; i < 8; i++) {
                if (boolRep[i]) {
                    g2.setColor(Color.BLACK);
                } else {
                    g2.setColor(Color.WHITE);
                }
                g2.fill(barcodeRectangle[i]);
            }
        }

        //
        // // teken alle rectangles van alle barcodes
        // for (Tuple t : getVisibleBarcode()) {
        // String rep = ((Barcode) t.getItem1()).toString();
        // Rectangle2D[] bc = (Rectangle2D[]) t.getItem2();
        // for (int i = 0; i < 8; i++) {
        // if (rep.charAt(i) == '0')
        // ((Graphics2D) graph).setColor(Color.black);
        // else
        // ((Graphics2D) graph).setColor(Color.white);
        //
        // ((Graphics2D) graph).fill(bc[i]);
        //
        // }
        // }

    }

    /**
     * Methode die alle paint methodes samenvoegd en uitvoert in het JPanel
     */
    @Override
    protected void paintComponent(final Graphics graph) {
        super.paintComponent(graph);
        paintBarcodeComponent(graph);
        paintRobotComponent(graph);
        // paintGridComponent(graph);
        paintWallComponent(graph);
        paintHighLightComponents(graph);
    }

    private void paintHighLightComponents(final Graphics graph) {
        if (checkHighlight != null) {
            ((Graphics2D) graph).setColor(Color.ORANGE);
            ((Graphics2D) graph).fill(checkHighlight);
        }
        if (endHighlight != null) {
            ((Graphics2D) graph).setColor(Color.ORANGE);
            ((Graphics2D) graph).fill(endHighlight);
        }
    }

    /**
     * Tekent de robot zelf met daarachter het grid.
     * 
     * @param graph
     */
    private void paintRobotComponent(final Graphics graph) {
        // Vector<Shape> shapesx = new Vector<Shape>();
        // shapesx.add(getVisibleShapes());
        //
        // ((Graphics2D) graph).setColor(Color.red);
        //
        // if (isUpdatedTriangle) {
        // setOtherTriangleVisible();
        // setUpdated(false);
        // }
        //
        // int count = 50;
        // int size = (int) getSizeTile();
        //
        // ((Graphics2D) graph).setColor(Color.lightGray);
        //
        // for (int i = 0; i < count; i++)
        // for (int j = 0; j < count; j++) {
        // Rectangle grid = new Rectangle(i * size - getShiftToTheRight(),
        // j * size - getShiftDown(), size, size);
        // ((Graphics2D) graph).draw(grid);
        // }
        //
        // ((Graphics2D) graph).setColor(Color.red);
        // for (Shape s : shapesx)
        //
        // {
        //
        // int x;
        // int y;
        //
        // if (s instanceof Triangle) {
        // if (s.equals(getVisibleTriangle()))
        // ((Graphics2D) graph).fill(s);
        // x = (int) ((Triangle) s).getGravityCenterX();
        // y = (int) ((Triangle) s).getGravityCenterY();
        // } else {
        // ((Graphics2D) graph).fill(s);
        // }
        // }

    }

    /**
     * tekent de muren op het JPanel paneel
     * 
     * @param graph
     */
    private void paintWallComponent(final Graphics graph) {

        ((Graphics2D) graph).setColor(Color.BLACK);

        for (final Wall wall : getVisibleWalls().values()) {
            ((Graphics2D) graph).fill(wall);
        }
    }

    /**
     * Removes all Barcodes this panel is keeping track of.
     */
    public void removeBarCodes() {
        barcodes1 = new Bag<Tuple<Barcode, Rectangle2D[]>>();
        barcodes2 = new Bag<Tuple<Barcode, Rectangle2D[]>>();
    }

    public void removeWallFrom(final Point2D point) {
        getVisibleWalls().remove(point);
    }

    public void removeWallFromTile(final int x, final int y,
            final Orientation orientation) {
        // TODO in SimulationPilot removeWall

        // if
        // (simulationPilot.getMapGraphConstructed().getTileWithCoordinates(x,
        // y) == null)
        // throw new IllegalArgumentException(
        // "in simulationPanel bij methode removeWallFromTile "
        // + "zijn coordinaten meegegeven die de mapgraph niet bevat");
        // simulationPilot.getMapGraphConstructed().getTileWithCoordinates(x, y)
        // .getEdge(orientation).setObstruction(null);
    }

    /**
     * Removes all Wall-objects this panel is keeping track of.
     */
    public void removeWalls() {
        getVisibleWalls().clear();
        getNotVisibleWalls().clear();
    }

    /**
     * Deletes the constructed mapGraph and sets up a new one. Also calls
     * clearTotal() and resets the triangles. (angle 270);
     */
    public void resetMap() {
        clearTotal();
        SilverSurferGUI.getStatusInfoBuffer().resetBuffer();
        // mapGraphConstructed in pilot moet nog gereset worden.
        // mapGraphConstructed = new MapGraph();
        // getSimulationPilot().reset();

    }

    /**
     * Updates the width/height/coordinates of the barcodes.
     */
    private void scaleBarcodes(final double scalingfactor) {

        final Bag<Tuple<Barcode, Rectangle2D[]>> storeBarcodes = new Bag<Tuple<Barcode, Rectangle2D[]>>();

        for (final Tuple t : getVisibleBarcode()) {
            storeBarcodes.add(t);
        }

        for (final Tuple t : storeBarcodes) {
            final Barcode barcode = (Barcode) t.getItem1();
            final Rectangle2D[] rect = barcode
                    .createVisualBarCode(40 * scalingfactor);
            getNotVisibleBarcode().add(
                    new Tuple<Barcode, Rectangle2D[]>(barcode, rect));
        }

    }

    private void scaleEndAndCheckTile(final double scalingfactor) {
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

    private void scaleShapes(final double scalingfactor) {
        // TODO scale(Shape shape)

        // double newGravityCenterX = ((triangle1).getGravityCenterX() +
        // getShiftToTheRight())
        // * scalingfactor
        // / this.getScalingfactor()
        // - getShiftToTheRight();
        // double newGravityCenterY = ((triangle1).getGravityCenterY() +
        // getShiftDown())
        // * scalingfactor / this.getScalingfactor() - getShiftDown();
        //
        // (triangle1).setGravityCenterX(newGravityCenterX);
        // (triangle1).setGravityCenterY(newGravityCenterY);
        // (triangle1).setScalingfactor(scalingfactor);
        //
        // // getNotVisibleShapes().add(shape);
        // isUpdatedShapes = true;
    }

    private void scaleWalls(final double scalingfactor) {

        final Vector oldPoints = new Vector<Point2D>();
        for (final Point2D point : getVisibleWalls().keySet()) {
            oldPoints.add(point);
        }

        for (final Object point : oldPoints) {

            final Wall wall = getVisibleWalls().get(point);

            final double pointWithoutShiftX = ((Point2D) point).getX()
                    + getShiftToTheRight();
            final double pointWithoutShiftY = ((Point2D) point).getY()
                    + getShiftDown();

            final int whichXTile = (int) Math.round(pointWithoutShiftX
                    / getSizeTile());
            final int whichYTile = (int) Math.round(pointWithoutShiftY
                    / getSizeTile());

            double newXCoordinate = whichXTile * (40 * scalingfactor)
                    - getShiftToTheRight();
            double newYCoordinate = whichYTile * (40 * scalingfactor)
                    - getShiftDown();

            final int newWidth = (int) Math.round((wall.getWidth()
                    * scalingfactor / getScalingfactor()));
            final int newHeight = (int) Math.round((wall.getHeight()
                    * scalingfactor / getScalingfactor()));

            if (wall.getState() == State.HORIZONTAL) {
                newYCoordinate = newYCoordinate
                        - (Wall.getStandardWidth() * scalingfactor) / 2;
            }

            else {
                newXCoordinate = newXCoordinate
                        - (Wall.getStandardWidth() * scalingfactor) / 2;
            }

            final Wall newWall = new Wall(wall.getState(), newXCoordinate,
                    newYCoordinate, newWidth, newHeight);

            getNotVisibleWalls()
                    .put(new Point2D.Double(newXCoordinate, newYCoordinate),
                            newWall);

        }

    }

    public void setBarcode(final Barcode barcode, final double absoluteX,
            final double absoluteY) {
        // TODO drawBarcodes, drawBarcode

        // if (!barcode.isDrawn()) {
        // Rectangle2D[] rect = Barcode.createVisualBarCode(barcode, absoluteX
        // - getShiftToTheRight(), absoluteY - getShiftDown(),
        // getSizeTile());
        // simulationPilot.addBarcode(this, barcode, rect);
        // barcode.setDrawn(true);
        // }
    }

    public void setHighlight(final double absoluteX, final double absoluteY) {
        checkHighlight = new Rectangle(
                ((Double) (absoluteX - 20 * scalingfactor)).intValue()
                        - getShiftToTheRight(),
                ((Double) (absoluteY - 20 * scalingfactor)).intValue()
                        - getShiftDown(), (int) getSizeTile(),
                (int) getSizeTile());
    }

    public void setOtherObjectsVisible() {
        if (isVisibleObjects == 1) {
            isVisibleObjects = 2;
        } else {
            isVisibleObjects = 1;
        }
    }

    public void setScalingfactor(final double scalingfactor) {
        scaleWalls(scalingfactor);
        scaleShapes(scalingfactor);
        scaleBarcodes(scalingfactor);
        scaleEndAndCheckTile(scalingfactor);

        getNotVisibleBarcode().empty();
        getNotVisibleWalls().clear();

        this.scalingfactor = scalingfactor;

    }

    public void setShiftDown(final int shiftDown) {
        this.shiftDown = this.shiftDown + shiftDown;
        shiftWalls(false, shiftDown);
        shiftShapes(false, shiftDown);
        shiftBarcodes(false, shiftDown);
        shiftEndAndCheckTile(false, shiftDown);

        getNotVisibleBarcode().empty();
        getNotVisibleWalls().clear();
    }

    public void setShiftToTheRight(final int shiftRight) {
        shiftToTheRight = shiftToTheRight + shiftRight;
        shiftBarcodes(true, shiftRight);
        shiftWalls(true, shiftRight);
        shiftShapes(true, shiftRight);
        shiftEndAndCheckTile(true, shiftRight);

        getNotVisibleBarcode().empty();
        getNotVisibleWalls().clear();
    }

    public void setWall(final Point2D point, final Wall wall) {
        getVisibleWalls().put(point, wall);
    }

    private void shiftBarcodes(final boolean shiftHorizontal, final int shift) {

        final Bag<Tuple<Barcode, Rectangle2D[]>> storeBarcodes = new Bag<Tuple<Barcode, Rectangle2D[]>>();

        for (final Tuple t : getVisibleBarcode()) {
            storeBarcodes.add(t);
        }

        for (final Tuple t : storeBarcodes) {
            final Barcode barcode = (Barcode) t.getItem1();

            final Rectangle2D[] rect = barcode
                    .createVisualBarCode(getSizeTile());
            getNotVisibleBarcode().add(
                    new Tuple<Barcode, Rectangle2D[]>(barcode, rect));
        }

    }

    private void shiftEndAndCheckTile(final boolean shiftHorizontal,
            final int shift) {
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

    private void shiftShapes(final boolean shiftHorizontal, final int shift) {
        // TODO shiftShapes(Shape shape)

        // double newGravityCenterX = (triangle1).getGravityCenterX();
        // double newGravityCenterY = (triangle1).getGravityCenterY();
        //
        // if (shiftHorizontal) {
        // newGravityCenterX = newGravityCenterX - shift;
        // } else {
        // newGravityCenterY = newGravityCenterY - shift;
        // }
        //
        // (triangle1).setGravityCenterX(newGravityCenterX);
        // (triangle1).setGravityCenterY(newGravityCenterY);
        //
        // isUpdatedShapes = true;

    }

    private void shiftWalls(final boolean shiftHorizontal, final int shift) {
        final Vector oldPoints = new Vector<Point2D>();
        for (final Point2D point : getVisibleWalls().keySet()) {
            oldPoints.add(point);
        }

        for (final Object point : oldPoints) {

            final Wall wall = getVisibleWalls().get(point);

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
    }

}