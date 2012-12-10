package gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.*;

import javax.swing.*;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class SensorGraph extends JPanel {
    private final int numberOfValuesToPlotLS = 100;
    private final int numberOfValuesToPlotUS = 100;
    private final int repaintPeriodInms = 10;
    private final Queue<Integer> LS = new ArrayBlockingQueue<Integer>(
            numberOfValuesToPlotLS);
    private final Queue<Integer> US = new ArrayBlockingQueue<Integer>(
            numberOfValuesToPlotUS);

    private final SilverSurferGUI gui;
    private final int updateValuesPeriodInms = 100;

    public SensorGraph(SilverSurferGUI gui) {
        this.gui = gui;
        new Timer(updateValuesPeriodInms, updateTimerAction).start();

        for (int i = 0; i < numberOfValuesToPlotLS; i++) {
            LS.offer(0);
        }
        for (int i = 0; i < numberOfValuesToPlotUS; i++) {
            US.offer(250);
        }
        new Timer(repaintPeriodInms, repaintSensorGraph).start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        int graphWidth = w;
        int graphHeight = h + 10;

        int wLS = graphWidth / 2;
        int wUS = graphWidth;

        int LSWhite = 54;
        int LSBlack = 34;
        int LSMax = 57;
        int LSMin = 31;
        int LSScale = LSMax - LSMin;
        int USMax = 150;
        int USWall = 28;

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
        int lineThickness = 3; // in pixels
        g2.setStroke(new BasicStroke(lineThickness));

        int LSIndex = 0;
        int LSValOld = -1;
        int LSValNew = -1;
        for (int LSValue : LS) {
            LSValOld = LSValNew;
            LSValNew = LSValue;

            if (LSValOld == -1)
                continue;

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
        for (int USValue : US) {
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

    public void addSensorValues(int USValue, int LSValue) {
        LS.poll();
        LS.offer(LSValue);
        US.poll();
        US.offer(USValue);
    }

    ActionListener repaintSensorGraph = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent arg0) {
            repaint();
        }
    };

    ActionListener updateTimerAction = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            gui.updateStatus();
        }
    };
}
