package simulator.viewport;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.Timer;

import mapping.Barcode;
import mapping.MapGraph;

import simulator.pilot.AbstractPilot;

public abstract class AbstractViewPort extends JPanel {

    protected Set<AbstractPilot> pilots;
    protected double scalingfactor = 1;

    private Map<Rectangle2D[], boolean[]> barcodeRectangles;

    private int repaintFrequency = 30;
    private ActionListener repaintViewPort = new ActionListener() {

        @Override
        public void actionPerformed(final ActionEvent arg0) {
            repaint();
        }
    };

    public AbstractViewPort() {
        new Timer(repaintFrequency, repaintViewPort);
        barcodeRectangles = new HashMap<Rectangle2D[], boolean[]>();
    }

    /**
     * Deletes the former path of the robot and all the walls that have been
     * explored as yet.
     */
    public void clearTotal() {
        removeBarCodes();
    }

    public double getSizeTile() {
        return 40 * scalingfactor;
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
    }

    /**
     * Methode die alle paint methodes samenvoegd en uitvoert in het JPanel
     */
    @Override
    protected void paintComponent(final Graphics graph) {
        super.paintComponent(graph);
        paintGridComponent(graph);
        paintMapGraph(graph);
        paintRobotComponent(graph);
    }

    @SuppressWarnings("unused")
    private void paintHighLightComponents(final Graphics graph) {
    }

    private void paintGridComponent(final Graphics graph) {
        Graphics2D g2 = (Graphics2D) graph;

        g2.setColor(Color.LIGHT_GRAY);

        int mapShiftHor = (getWidth() - pilots.iterator().next()
                .getMapGraphConstructed().getMapSize().x) / 2;
        int mapShiftVer = (getHeight() - pilots.iterator().next()
                .getMapGraphConstructed().getMapSize().y) / 2;

        int minShiftHor = mapShiftHor - getWidth() * 2;
        int maxShiftHor = mapShiftHor + getWidth() * 2;
        int minShiftVer = mapShiftVer - getHeight() * 2;
        int maxShiftVer = mapShiftVer + getHeight() * 2;

        for (int x = minShiftHor; x < maxShiftHor; x += getSizeTile()) {
            g2.draw(new Line2D.Double(x, minShiftVer, x, maxShiftVer));
        }
        for (int y = minShiftVer; y < maxShiftVer; y += getSizeTile()) {
            g2.draw(new Line2D.Double(minShiftHor, y, maxShiftHor, y));
        }
    }

    /**
     * Tekent de robot zelf.
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

    protected void paintMapGraph(Graphics graph) {
        paintBarcodeComponent(graph);
    }

    /**
     * Removes all Barcodes this panel is keeping track of.
     */
    public void removeBarCodes() {
        barcodeRectangles = new HashMap<Rectangle2D[], boolean[]>();
    }
}