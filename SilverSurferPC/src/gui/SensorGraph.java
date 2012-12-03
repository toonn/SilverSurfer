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

        int wLS = w / 2;
        int wUS = w;

        int LSWhite = 54;
        int LSBlack = 34;
        int LSMax = 57;
        int LSMin = 31;
        int LSScale = LSMax - LSMin;
        int USMax = 150;
        int USWall = 28;

        // LightSensor white and black boundary
        g2.draw(new Line2D.Double(0, h - (LSWhite - LSMin) * h / LSScale, wLS,
                h - (LSWhite - LSMin) * h / LSScale));
        g2.draw(new Line2D.Double(0, h - (LSBlack - LSMin) * h / LSScale, wLS,
                h - (LSBlack - LSMin) * h / LSScale));

        // USensor maximum useful value
        g2.setColor(Color.red);
        g2.draw(new Line2D.Double(wLS, h - USMax, wUS, h - USMax));
        // USensor approximate wall values
        g2.setColor(Color.black);
        for (int wall = USWall; wall < USMax; wall += 40) {
            g2.draw(new Line2D.Double(wLS, h - wall, wUS, h - wall));
        }

        for (int i = 0; i < US.size() - 1; i++) {
            g2.setColor(Color.orange);
            g2.draw(new Line2D.Double(i * w / 200, h - (LS.get(i) - LSMin) * h
                    / LSScale, (i + 1) * w / 200, h - (LS.get(i + 1) - LSMin)
                    * h / LSScale));

            if (US.get(i) <= 150 && US.get(i + 1) <= 150) {
                g2.setColor(Color.blue);
                g2.draw(new Line2D.Double(wLS + i * w / 200, h - US.get(i), wLS
                        + (i + 1) * w / 200, h - US.get(i + 1)));
            } else {
                g2.setColor(Color.lightGray);
                g2.draw(new Line2D.Double(wLS + i * w / 200, h - 155, wLS
                        + (i + 1) * w / 200, h - 155));
            }
        }

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
