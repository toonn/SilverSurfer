package simulator.viewport;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

import mapping.Barcode;
import mapping.Edge;
import mapping.MapGraph;
import mapping.Orientation;
import mapping.Tile;
import simulator.pilot.PilotInterface;

@SuppressWarnings("serial")
public abstract class AbstractViewPort extends JPanel {

    protected Set<PilotInterface> pilots;
    protected double scalingfactor = 1;
    private ImageIcon robotSprite = new ImageIcon(
            "resources/robot/NXTrobotsmall.png");;
    private Map<boolean[], Rectangle2D[]> barcodeRectangles;
    private int repaintFPS = 30;
    private ActionListener repaintViewPort = new ActionListener() {

        @Override
        public void actionPerformed(final ActionEvent arg0) {
            repaint();
        }
    };

    public AbstractViewPort(Set<? extends PilotInterface> pilotSet) {
        pilots = new HashSet<PilotInterface>(pilotSet);
        barcodeRectangles = new HashMap<boolean[], Rectangle2D[]>();

        // RepaintThread RT = new RepaintThread(this);
        // RT.start();
        new Timer(1000 / repaintFPS, repaintViewPort).start();
    }

    public double getSizeTile() {
        return pilots.iterator().next().sizeTile() * scalingfactor;
    }

    protected abstract Set<MapGraph> getAllMapGraphs();

    /**
     * Methode die alle paint methodes samenvoegd en uitvoert in het JPanel
     */
    @Override
    protected void paintComponent(final Graphics graph) {
        super.paintComponent(graph);
        if (pilots.size() > 0) {
            paintGrid(graph);
            paintMapGraph(graph);
            paintRobots(graph);
        }
    }

    private void paintGrid(final Graphics graph) {
        Graphics2D g2 = (Graphics2D) graph;

        g2.setColor(Color.lightGray);

        /*
         * int mapShiftHor = (getWidth() -
         * pilots.iterator().next().getMapGraphConstructed().getMapSize().x) /
         * 2; int mapShiftVer = (getHeight() -
         * pilots.iterator().next().getMapGraphConstructed().getMapSize().y) /
         * 2;
         * 
         * int minShiftHor = mapShiftHor - getWidth() * 2; int maxShiftHor =
         * mapShiftHor + getWidth() * 2; int minShiftVer = mapShiftVer -
         * getHeight() * 2; int maxShiftVer = mapShiftVer + getHeight() * 2;
         */

        int minShiftHor = 0;
        int maxShiftHor = getWidth() * 2;
        int minShiftVer = 0;
        int maxShiftVer = getHeight() * 2;

        for (int x = minShiftHor; x < maxShiftHor; x += getSizeTile())
            g2.draw(new Line2D.Double(x, minShiftVer, x, maxShiftVer));
        for (int y = minShiftVer; y < maxShiftVer; y += getSizeTile())
            g2.draw(new Line2D.Double(minShiftHor, y, maxShiftHor, y));
    }

    private void paintMapGraph(Graphics graph) {
        paintBarcodes(graph);
        paintWalls(graph);
    }

    private void paintBarcodes(final Graphics graph) {
        // TODO Werkt niet als er meer dan 1 pilot in pilots zit.
        // Omdat barcodeRectangles.size() dan ~nooit overeen komt met
        // pilot.getbarcodes().size()
        int totalBarcodes = 0;
        for (final PilotInterface pilot : pilots)
            totalBarcodes += pilot.getBarcodes().size();
        if (totalBarcodes != barcodeRectangles.size()) {
            // In geval van een verkeerd ingelezen barcode, alles weggooien.
            barcodeRectangles = new HashMap<boolean[], Rectangle2D[]>();
            for (final PilotInterface pilot : pilots)
                for (final Barcode barcode : pilot.getBarcodes())
                    if (barcodeRectangles.containsKey(barcode.getBoolRep())) // TODO:
                                                                             // Moet
                                                                             // dit
                                                                             // niet
                                                                             // !barcodeRectangles.contains...
                                                                             // zijn?
                        barcodeRectangles.put(barcode.getBoolRep(),
                                createVisualBarCode(barcode));
        }

        final Graphics2D g2 = ((Graphics2D) graph);
        for (final boolean[] boolRep : barcodeRectangles.keySet()) {
            final Rectangle2D[] barcodeRectangle = barcodeRectangles
                    .get(boolRep);
            for (int i = 0; i < 8; i++) {
                if (boolRep[i]) {
                    g2.setColor(Color.BLACK);
                } else {
                    g2.setColor(Color.WHITE);
                }
                g2.fill(barcodeRectangle[i]);
            }
        }
    }

    private Rectangle2D[] createVisualBarCode(final Barcode barcode) {
        final Rectangle2D[] visualBarcode = new Rectangle2D[8];
        final Point2D.Double barcodeLUCorner = new Point2D.Double(barcode
                .getPosition().getX(), barcode.getPosition().getY());
        double width = getSizeTile();
        double height = getSizeTile();

        // North or South oriented barcode
        if (barcode.getDirection() == Orientation.NORTH
                || barcode.getDirection() == Orientation.SOUTH)
            height = getSizeTile() / 20;
        else if (barcode.getDirection() == Orientation.EAST
                || barcode.getDirection() == Orientation.WEST)
            width = getSizeTile() / 20;
        for (int i = 0; i < 8; i++) {
            visualBarcode[i] = new Rectangle2D.Double(barcodeLUCorner.getX(),
                    barcodeLUCorner.getY(), barcodeLUCorner.getX() + width,
                    barcodeLUCorner.getY() + height);
            barcodeLUCorner.setLocation(barcodeLUCorner.getX() + width,
                    barcodeLUCorner.getY() + height);
        }

        return visualBarcode;
    }

    private void paintWalls(Graphics graph) {
        Set<Point2D[]> walls = new HashSet<Point2D[]>();
        for (MapGraph mapGraph : getAllMapGraphs())
            for (Tile tile : mapGraph.getTiles())
                for (Edge wall : tile.getEdges())
                    if (wall.getObstruction() != null
                            && !wall.getObstruction().isPasseble())
                        walls.add(wall.getEndPoints());

        final Graphics2D g2 = ((Graphics2D) graph);
        g2.setColor(Color.black);
        Stroke originalStroke = g2.getStroke();
        float strokeWidth = 5;
        g2.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER));

        for (Point2D[] wall : walls) {
            for (Point2D point : wall)
                point.setLocation(point.getX() * getSizeTile(), point.getY()
                        * getSizeTile());
            g2.draw(new Line2D.Double(wall[0], wall[1]));
        }
        g2.setStroke(originalStroke);
    }

    /**
     * Tekent de robot zelf.
     */
    protected void paintRobots(final Graphics graph) {
        Graphics2D g2 = (Graphics2D) graph;
        for (PilotInterface pilot : pilots) {
            AffineTransform oldTransform = g2.getTransform();
            g2.rotate(Math.toRadians(pilot.getAngle()), pilot.getPosition()
                    .getX(), pilot.getPosition().getY());
            g2.drawImage(robotSprite.getImage(), (int) ((pilot.getPosition()
                    .getX() - robotSprite.getIconWidth() / 2) * scalingfactor),
                    (int) ((pilot.getPosition().getY() - robotSprite
                            .getIconHeight() / 2) * scalingfactor), null);
            g2.setTransform(oldTransform);
        }
    }

}