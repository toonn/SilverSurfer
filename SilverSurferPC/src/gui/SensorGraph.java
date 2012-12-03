package gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.*;
import java.awt.geom.*;

import javax.swing.*;

import java.util.ArrayList;
import java.util.List;

public class SensorGraph extends JPanel {
    private List<Integer> US = new ArrayList<Integer>();
    private List<Integer> LS = new ArrayList<Integer>();

    public SensorGraph() {
        for (int i = 0; i < 100; i++) {
            US.add(0);
            LS.add(0);
        }
        new Timer(100, repaintSensorGraph).start();
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
        g2.drawString("White", 15, graphHeight - (LSWhite - LSMin) * graphHeight / LSScale);
        g2.drawString("Black", 15, graphHeight - (LSBlack - LSMin) * graphHeight / LSScale);
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
        for (int i = 0; i < US.size() - 1; i++) {
            g2.setColor(Color.orange);
            g2.draw(new Line2D.Double(i * graphWidth / 200, graphHeight
                    - (LS.get(i) - LSMin) * graphHeight / LSScale, (i + 1)
                    * graphWidth / 200, graphHeight - (LS.get(i + 1) - LSMin)
                    * graphHeight / LSScale));

            if (US.get(i) <= 150 && US.get(i + 1) <= 150) {
                g2.setColor(Color.blue);
                g2.draw(new Line2D.Double(wLS + i * graphWidth / 200,
                        graphHeight - US.get(i), wLS + (i + 1) * graphWidth
                                / 200, graphHeight - US.get(i + 1)));
            } else {
                g2.setColor(Color.lightGray);
                g2.draw(new Line2D.Double(wLS + i * graphWidth / 200,
                        graphHeight - 155, wLS + (i + 1) * graphWidth / 200,
                        graphHeight - 155));
            }
        }
        g2.setStroke(new BasicStroke(1));

        // Dividing line between sensors
        g2.setColor(Color.gray);
        g2.fill(new Rectangle(wLS - 3, 0, 6, h));
    }

    public void addSensorValues(int USValue, int LSValue) {
        US.add(USValue);
        US.remove(0);
        LS.add(LSValue);
        LS.remove(0);
    }

    ActionListener repaintSensorGraph = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent arg0) {
            repaint();
        }
    };

    public static void main(String[] args) {
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(new SensorGraph());
        f.setSize(400, 400);
        f.setLocation(200, 200);
        f.setVisible(true);
    }
}
