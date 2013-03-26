package gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import javax.swing.JPanel;
import javax.swing.Timer;

@SuppressWarnings("serial")
public class SensorGraph extends JPanel {

    private Timer timer;
    private boolean pause = false;
    private final int numberOfValuesToPlotLS = 100;
    private final int numberOfValuesToPlotUS = 100;
    private int repaintPeriodInms = 10;
    private final Queue<Integer> LS = new ArrayBlockingQueue<Integer>(
            numberOfValuesToPlotLS);
    private final Queue<Integer> US = new ArrayBlockingQueue<Integer>(
            numberOfValuesToPlotUS);
    private ActionListener repaintSensorGraph = new ActionListener() {

        @Override
        public void actionPerformed(final ActionEvent arg0) {
            repaint();
        }
    };

    public SensorGraph() {
        for (int i = 0; i < numberOfValuesToPlotLS; i++) {
            LS.offer(0);
        }
        for (int i = 0; i < numberOfValuesToPlotUS; i++) {
            US.offer(250);
        }
        timer = new Timer(repaintPeriodInms, repaintSensorGraph);
        timer.start();
    }

    public void addSensorValues(final int USValue, final int LSValue) {
        LS.poll();
        LS.offer(LSValue);
        US.poll();
        US.offer(USValue);
    }

    @Override
    protected void paintComponent(final Graphics g) {
        super.paintComponent(g);
        final Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        final int w = getWidth();
        final int h = getHeight();

        final int graphWidth = w;
        final int graphHeight = h + 10;

        final int wLS = graphWidth / 2;
        final int wUS = graphWidth;

        final int LSWhite = 54;
        final int LSBlack = 34;
        final int LSMax = 57;
        final int LSMin = 31;
        final int LSScale = LSMax - LSMin;
        final int USMax = 150;
        final int USWall = 28;

        // Labels on the graph
        g2.setColor(Color.gray);
        g2.drawString("LightSensor", w / 2 / 5, 10);
        g2.drawString("White", 15, graphHeight - (LSWhite - LSMin)
                * graphHeight / LSScale);
        g2.drawString("Black", 15, graphHeight - (LSBlack - LSMin)
                * graphHeight / LSScale);
        // Lightsensor scale
        for (int lsv = 30; lsv < 60; lsv += 5) {
            g2.drawString("" + lsv, 0, graphHeight - (lsv - LSMin)
                    * graphHeight / LSScale);
            g2.draw(new Line2D.Double(0, graphHeight - (lsv - LSMin)
                    * graphHeight / LSScale, wLS, graphHeight - (lsv - LSMin)
                    * graphHeight / LSScale));
        }

        g2.drawString("UltrasonicSensor", w / 2 / 5 + w / 2, 10);
        for (int usv = 0; usv < 160; usv += 30) {
            g2.drawString("" + usv, wLS + 4, graphHeight - usv);
            g2.draw(new Line2D.Double(wLS, graphHeight - usv, wUS, graphHeight
                    - usv));
        }

        // LightSensor white and black threshold
        g2.setColor(Color.black);
        g2.draw(new Line2D.Double(0, graphHeight - (LSWhite - LSMin)
                * graphHeight / LSScale, wLS, graphHeight - (LSWhite - LSMin)
                * graphHeight / LSScale));
        g2.draw(new Line2D.Double(0, graphHeight - (LSBlack - LSMin)
                * graphHeight / LSScale, wLS, graphHeight - (LSBlack - LSMin)
                * graphHeight / LSScale));

        // USensor maximum useful value
        g2.setColor(Color.red);
        g2.draw(new Line2D.Double(wLS, graphHeight - USMax, wUS, graphHeight
                - USMax));
        // USensor approximate wall values
        g2.setColor(Color.black);
        for (int wall = USWall; wall < USMax; wall += 40) {
            g2.draw(new Line2D.Double(wLS, graphHeight - wall, wUS, graphHeight
                    - wall));
        }

        // Sensor values
        final int lineThickness = 3; // in pixels
        g2.setStroke(new BasicStroke(lineThickness));

        int LSIndex = 0;
        int LSValOld = -1;
        int LSValNew = -1;
        for (final int LSValue : LS) {
            LSValOld = LSValNew;
            LSValNew = LSValue;

            if (LSValOld == -1) {
                continue;
            }

            g2.setColor(Color.orange);
            g2.draw(new Line2D.Double(LSIndex * graphWidth / (2 * LS.size()),
                    graphHeight - (LSValOld - LSMin) * graphHeight / LSScale,
                    (LSIndex + 1) * graphWidth / (2 * LS.size()), graphHeight
                            - (LSValNew - LSMin) * graphHeight / LSScale));

            LSIndex++;
        }

        int USIndex = 0;
        int USValOld = -1;
        int USValNew = -1;
        for (final int USValue : US) {
            USValOld = USValNew;
            USValNew = USValue;

            if (USValOld <= 150 && USValNew <= 150) {
                g2.setColor(Color.blue);
                g2.draw(new Line2D.Double(wLS + USIndex * graphWidth
                        / (2 * US.size()), graphHeight - USValOld, wLS
                        + (USIndex + 1) * graphWidth / (2 * US.size()),
                        graphHeight - USValNew));
            } else {
                g2.setColor(Color.lightGray);
                g2.draw(new Line2D.Double(wLS + USIndex * graphWidth / 200,
                        graphHeight - 155, wLS + (USIndex + 1) * graphWidth
                                / 200, graphHeight - 155));
            }

            USIndex++;
        }
        g2.setStroke(new BasicStroke(1));

        // Dividing line between sensors
        g2.setColor(Color.gray);
        g2.fill(new Rectangle(wLS - 3, 0, 6, h));
    }

    public void togglePause() {
        pause = !pause;
        if (pause) {
            repaintPeriodInms = 10000000;
        } else {
            repaintPeriodInms = 10;
        }
        timer.stop();
        timer = new Timer(repaintPeriodInms, repaintSensorGraph);
        timer.start();
    }
}