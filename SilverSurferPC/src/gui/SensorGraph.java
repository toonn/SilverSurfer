package gui;

import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;

import javax.jws.soap.SOAPBinding.Use;
import javax.swing.*;

import java.util.ArrayList;
import java.util.List;

public class SensorGraph extends JPanel {
    private List<Integer> US = new ArrayList<Integer>();
    private List<Integer> LS = new ArrayList<Integer>();

    {
        for (int i = 0; i < 100; i++) {
            US.add(i);
            LS.add(i * i);
        }
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int xInc = w / 99;

        for (int i = 0; i < US.size() - 1; i++) {
            g2.setColor(Color.blue);
            g2.draw(new Line2D.Double(i * xInc, h - US.get(i), (i + 1) * xInc,
                    h - US.get(i + 1)));
            g2.setColor(Color.red);
            g2.draw(new Line2D.Double(i * xInc, h - LS.get(i), (i + 1) * xInc,
                    h - LS.get(i + 1)));
        }
    }

    public void addSensorValues(int USValue, int LSValue) {
        US.add(USValue);
        US.remove(0);
        LS.add(LSValue);
        LS.remove(0);

        repaint();
    }

    public static void main(String[] args) {
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(new SensorGraph());
        f.setSize(400, 400);
        f.setLocation(200, 200);
        f.setVisible(true);
    }
}
