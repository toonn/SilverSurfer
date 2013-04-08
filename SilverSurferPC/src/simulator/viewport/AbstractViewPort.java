package simulator.viewport;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
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
import mapping.Obstruction;
import mapping.Orientation;
import mapping.Seesaw;
import mapping.Tile;
import mapping.TreasureObject;
import simulator.pilot.AbstractPilot;
import simulator.pilot.DummyPilot;
import simulator.pilot.PilotInterface;

@SuppressWarnings("serial")
public abstract class AbstractViewPort extends JPanel {

    protected Set<PilotInterface> pilots;
    private Map<Barcode, Rectangle2D[]> barcodeRectangles;
    private HashMap<TreasureObject, Ellipse2D[]> treasureCircles;
    private ImageIcon robotSprite = new ImageIcon(
            "resources/robot/NXTrobotsmall.png");
    private int repaintFPS = 30;
    private Color[] teamColors;
    private Color mainColor;
    private ActionListener repaintViewPort = new ActionListener() {

        @Override
        public void actionPerformed(final ActionEvent arg0) {
            repaint();
        }
    };

    public AbstractViewPort(Set<? extends PilotInterface> pilotSet,
            Color[] teamColors, Color mainColor) {
        this.teamColors = teamColors;
        this.mainColor = mainColor;
        pilots = new HashSet<PilotInterface>(pilotSet);
        barcodeRectangles = new HashMap<Barcode, Rectangle2D[]>();
        treasureCircles = new HashMap<TreasureObject, Ellipse2D[]>();
        new Timer(1000 / repaintFPS, repaintViewPort).start();
    }
    
    public void setMainColor(Color mainColor) {
    	this.mainColor = mainColor;
    }

    private Rectangle2D[] createVisualBarCode(final Barcode barcode) {
        final Rectangle2D[] visualBarcode = new Rectangle2D[8];
        Point2D.Double barcodeLUCorner = new Point2D.Double(barcode
                .getPosition().getX() * getSizeTile(), barcode.getPosition()
                .getY() * getSizeTile());

        double width = getSizeTile();
        double height = getSizeTile();
        double extraX = 0;
        double extraY = 0;

        if (barcode.getDirection() == Orientation.NORTH
                || barcode.getDirection() == Orientation.SOUTH) {
            height = getSizeTile() / 20;
            extraY = getSizeTile() / 2 - 8;
        } else if (barcode.getDirection() == Orientation.EAST
                || barcode.getDirection() == Orientation.WEST) {
            width = getSizeTile() / 20;
            extraX = getSizeTile() / 2 - 8;
        }

        for (int i = 0; i < 8; i++) {
            visualBarcode[i] = new Rectangle2D.Double(barcodeLUCorner.getX()
                    + extraX, barcodeLUCorner.getY() + extraY, width, height);
            if (width < height) {
                barcodeLUCorner = new Point2D.Double(barcodeLUCorner.getX()
                        + width, barcodeLUCorner.getY());
            } else if (width > height) {
                barcodeLUCorner = new Point2D.Double(barcodeLUCorner.getX(),
                        barcodeLUCorner.getY() + height);
            }
        }

        return visualBarcode;
    }

    private Ellipse2D createVisualTreasure(final TreasureObject treasure,
            double diameter) {
        final Ellipse2D visualTreasure = new Ellipse2D.Double(treasure
                .getPosition().getX()
                * getSizeTile()
                + getSizeTile()
                / 2
                - diameter / 2, treasure.getPosition().getY() * getSizeTile()
                + getSizeTile() / 2 - diameter / 2, diameter, diameter);
        return visualTreasure;
    }

    protected abstract Set<MapGraph> getAllMapGraphs();

    public double getSizeTile() {
        return pilots.iterator().next().sizeTile();
    }

    private void paintBarcodes(final Graphics graph) {
        Set<Barcode> barcodes = new HashSet<Barcode>();
        try {
            for (MapGraph mapGraph : getAllMapGraphs()) {
                for (Tile tile : mapGraph.getTiles()) {
                    if (tile.getContent() instanceof Barcode) {
                        barcodes.add((Barcode) tile.getContent());
                    }
                }
            }
        } catch (java.util.ConcurrentModificationException e) {
            paintBarcodes(graph);
        }
        if (barcodeRectangles.size() != barcodes.size()) {
            // In geval van nieuw ontdekte barcode, alles weggooien en terug
            // toevoegen.
            barcodeRectangles = new HashMap<Barcode, Rectangle2D[]>();
            for (final Barcode barcode : barcodes) {
                if (!barcodeRectangles.containsKey(barcode)) {
                    barcodeRectangles
                            .put(barcode, createVisualBarCode(barcode));
                }
            }
        }

        final Graphics2D g2 = ((Graphics2D) graph);
        for (final Barcode barcode : barcodeRectangles.keySet()) {
            final Rectangle2D[] barcodeRectangle = barcodeRectangles
                    .get(barcode);
            boolean[] boolRep = barcode.getBoolRep();
            for (int i = 0; i < 8; i++) {
                if (boolRep[i]) {
                    g2.setColor(Color.WHITE);
                } else {
                    g2.setColor(Color.DARK_GRAY);
                }
                g2.fill(barcodeRectangle[i]);
            }
        }
    }

    @Override
    protected void paintComponent(final Graphics graph) {
        super.paintComponent(graph);
        paintFrame(graph);
        translate(graph);
        paintGrid(graph);
        if (pilots.size() > 0) {
            paintMapGraph(graph);
            paintRobotColor(graph);
            paintRobots(graph);
        }
    }

    private void paintFrame(final Graphics graph) {
        Graphics2D g2 = (Graphics2D) graph;

        g2.setColor(mainColor);
        Stroke originalStroke = g2.getStroke();
        g2.setStroke(new BasicStroke(5, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER));
        g2.draw(new Rectangle2D.Double(2, 2, getWidth() - 5, getHeight() - 5));
        g2.setStroke(originalStroke);
    }

    private void paintGrid(final Graphics graph) {
        Graphics2D g2 = (Graphics2D) graph;

        g2.setColor(Color.lightGray);

        int minShiftHor = -(getWidth() * 10) / (int) getSizeTile()
                * (int) getSizeTile();
        int maxShiftHor = getWidth() * 10;
        int minShiftVer = -(getHeight() * 10) / (int) getSizeTile()
                * (int) getSizeTile();
        int maxShiftVer = getHeight() * 10;

        for (int x = minShiftHor; x < maxShiftHor; x += getSizeTile()) {
            g2.draw(new Line2D.Double(x, minShiftVer, x, maxShiftVer));
        }
        for (int y = minShiftVer; y < maxShiftVer; y += getSizeTile()) {
            g2.draw(new Line2D.Double(minShiftHor, y, maxShiftHor, y));
        }
    }

    private void paintMapGraph(Graphics graph) {
        paintTreasures(graph);
        paintBarcodes(graph);
        paintSeesaws(graph);
        paintWalls(graph);
    }

    private void paintRobotColor(final Graphics graph) {
        int diam = 25;
        final Graphics2D g2 = (Graphics2D) graph;

        for (PilotInterface pilot : pilots) {
            if (!(pilot instanceof DummyPilot) || ((DummyPilot) pilot).isActive()) {
                AffineTransform oldTransform = g2.getTransform();
                if (!(pilot instanceof DummyPilot) && pilot.getTeamNumber() < 0)
                	g2.setColor(teamColors[((AbstractPilot)pilot).getPlayerNumber()]);
                else
                    g2.setColor(teamColors[4 + pilot.getTeamNumber()]);

                g2.fill(new Ellipse2D.Double(
                        (pilot.getPosition().getX() - (diam / 2)), (pilot
                                .getPosition().getY() - (diam / 2)), diam, diam));
                g2.setTransform(oldTransform);
            }
        }
    }

    private void paintRobots(final Graphics graph) {
        Graphics2D g2 = (Graphics2D) graph;
        for (PilotInterface pilot : pilots) {
            if (!(pilot instanceof DummyPilot) || ((DummyPilot) pilot).isActive()) {
                AffineTransform oldTransform = g2.getTransform();
                g2.rotate(Math.toRadians(pilot.getAngle()), pilot.getPosition().getX(), pilot.getPosition().getY());
                g2.drawImage(robotSprite.getImage(), (int) ((pilot.getPosition().getX() - robotSprite.getIconWidth() / 2)), (int) ((pilot.getPosition().getY() - robotSprite.getIconHeight() / 2)), null);
                g2.setTransform(oldTransform);
            }
        }
    }

    private void paintSeesaws(final Graphics graph) {
        // make a list of all the seesaw tiles and edges
        Set<Rectangle2D> seesawTiles = new HashSet<Rectangle2D>();
        Set<Point2D[]> seesawEdges = new HashSet<Point2D[]>();
        try {
            for (MapGraph mapGraph : getAllMapGraphs()) {
                for (Tile tile : mapGraph.getTiles()) {
                    if (tile.getContent() instanceof Seesaw) {
                        Rectangle2D seesawTile = new Rectangle2D.Double(tile
                                .getPosition().getX() * getSizeTile(), tile
                                .getPosition().getY() * getSizeTile(),
                                (int) getSizeTile(), (int) getSizeTile());
                        seesawTiles.add(seesawTile);
                        for (Edge seesawEdge : tile.getEdges()) {
                            if (seesawEdge.getObstruction() == Obstruction.SEESAW_UP) {
                                seesawEdges.add(seesawEdge.getEndPoints());
                            }
                        }
                    }
                }
            }
        } catch (java.util.ConcurrentModificationException e) {
            paintSeesaws(graph);
        }

        // draw the tiles
        ((Graphics2D) graph).setColor(Color.YELLOW);
        for (Rectangle2D seesawTile : seesawTiles) {
            ((Graphics2D) graph).fill(seesawTile);
        }

        // draw the edges
        final Graphics2D g2 = ((Graphics2D) graph);
        g2.setColor(Color.red);
        Stroke originalStroke = g2.getStroke();
        float strokeWidth = 5;
        g2.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_SQUARE,
                BasicStroke.JOIN_MITER));

        for (Point2D[] seesawEdge : seesawEdges) {
            for (Point2D point : seesawEdge) {
                point.setLocation(point.getX() * getSizeTile(), point.getY()
                        * getSizeTile());
            }
            g2.draw(new Line2D.Double(seesawEdge[0], seesawEdge[1]));
        }
        g2.setStroke(originalStroke);
    }

    private void paintTreasures(final Graphics graph) {
        Set<TreasureObject> treasures = new HashSet<TreasureObject>();
        try {
            for (MapGraph mapGraph : getAllMapGraphs()) {
                for (Tile tile : mapGraph.getTiles()) {
                    if (tile.getContent() instanceof TreasureObject) {
                        treasures.add((TreasureObject) tile.getContent());
                    }
                }
            }
        } catch (java.util.ConcurrentModificationException e) {
            paintTreasures(graph);
        }
        if (treasureCircles.size() != treasures.size()) {
            // In geval van nieuw ontdekte treasure, alles weggooien en terug
            // toevoegen.
            treasureCircles = new HashMap<TreasureObject, Ellipse2D[]>();
            for (final TreasureObject treasure : treasures) {
                if (!treasureCircles.containsKey(treasure)) {
                    treasureCircles.put(treasure, new Ellipse2D[] {
                            createVisualTreasure(treasure, 16),
                            createVisualTreasure(treasure, 15) });
                }
            }
        }

        // Eerst een grote zwarte cirkel en daarboven een kleinere gekleurde
        // cirkel.
        final Graphics2D g2 = ((Graphics2D) graph);
        for (final TreasureObject treasure : treasureCircles.keySet()) {
            g2.setColor(Color.BLACK);
            g2.fill(treasureCircles.get(treasure)[0]);
            g2.setColor(teamColors[treasure.getColor()]);
            g2.fill(treasureCircles.get(treasure)[1]);
        }
    }

    private void paintWalls(Graphics graph) {
        Set<Point2D[]> walls = new HashSet<Point2D[]>();
        try {
            for (MapGraph mapGraph : getAllMapGraphs()) {
                for (Tile tile : mapGraph.getTiles()) {
                    for (Edge wall : tile.getEdges()) {
                        if (wall.getObstruction() != null
                                && wall.getObstruction() == Obstruction.WALL) {
                            walls.add(wall.getEndPoints());
                        }
                    }
                }
            }
        } catch (java.util.ConcurrentModificationException e) {
            paintWalls(graph);
        }

        final Graphics2D g2 = ((Graphics2D) graph);
        g2.setColor(Color.BLACK);
        Stroke originalStroke = g2.getStroke();
        float strokeWidth = 5;
        g2.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_SQUARE,
                BasicStroke.JOIN_MITER));

        for (Point2D[] wall : walls) {
            for (Point2D point : wall) {
                point.setLocation(point.getX() * getSizeTile(), point.getY()
                        * getSizeTile());
            }
            g2.draw(new Line2D.Double(wall[0], wall[1]));
        }
        g2.setStroke(originalStroke);
    }

    private void translate(final Graphics graph) {
        Graphics2D g2 = (Graphics2D) graph;

        Point[] maxMapExpanse = getAllMapGraphs().iterator().next()
                .getMapExpanse();
        for (MapGraph map : getAllMapGraphs()) {
            Point[] mapExpanse = map.getMapExpanse();

            if (mapExpanse[0].x < maxMapExpanse[0].x) {
                maxMapExpanse[0] = new Point(mapExpanse[0].x,
                        maxMapExpanse[0].y);
            }
            if (mapExpanse[1].x > maxMapExpanse[1].x) {
                maxMapExpanse[1] = new Point(mapExpanse[1].x,
                        maxMapExpanse[1].y);
            }
            if (mapExpanse[0].y < maxMapExpanse[0].y) {
                maxMapExpanse[0] = new Point(maxMapExpanse[0].x,
                        mapExpanse[0].y);
            }
            if (mapExpanse[1].y > maxMapExpanse[1].y) {
                maxMapExpanse[1] = new Point(maxMapExpanse[1].x,
                        mapExpanse[1].y);
            }
        }
        Point maxMapSize = new Point(Math.abs(maxMapExpanse[0].x
                - maxMapExpanse[1].x) + 1, Math.abs(maxMapExpanse[0].y
                - maxMapExpanse[1].y) + 1);

        // Schaalaanpassing
        maxMapExpanse[0] = new Point(
                (int) (maxMapExpanse[0].x * getSizeTile()),
                (int) (maxMapExpanse[0].y * getSizeTile()));
        maxMapExpanse[1] = new Point(
                (int) (maxMapExpanse[1].x * getSizeTile()),
                (int) (maxMapExpanse[1].y * getSizeTile()));
        maxMapSize = new Point((int) (maxMapSize.x * getSizeTile()),
                (int) (maxMapSize.y * getSizeTile()));

        PilotInterface pilot = pilots.iterator().next();

        if (maxMapSize.x < getWidth() || pilots.size() > 1) {
            g2.translate(-(maxMapExpanse[0].x)
                    + ((getWidth() - maxMapSize.x) / 2), 0);
        } else {
            g2.translate((getWidth() / 2) - pilot.getPosition().x, 0);
        }
        if (maxMapSize.y < getHeight() || pilots.size() > 1) {
            g2.translate(0, -(maxMapExpanse[0].y)
                    + ((getHeight() - maxMapSize.y) / 2));
        } else {
            g2.translate(0, (getHeight() / 2) - pilot.getPosition().y);
        }
    }
}