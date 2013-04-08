package gui;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;

import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

@SuppressWarnings("serial")
public class SidePanel extends JPanel {
	
    protected static JLabel viewLabel, mapName, view, infoLabel, connection, speed, playerNumber, playerName;
    protected static JLabel teamMemberName, teamNumber, positionLabel, position, angle, sensorLabel, ultrasonic, light;
    protected static JLabel infrared, infoLabel18, infoLabel19, infoLabel20, infoLabel21, infoLabel22, infoLabel23, infoLabel24;
    private int repaintFPS = 1;
    private ActionListener repaintViewPort = new ActionListener() {

        @Override
        public void actionPerformed(final ActionEvent arg0) {
            repaint();
        }
    };

    public SidePanel() {
    	initialize();
        new Timer(1000 / repaintFPS, repaintViewPort).start();
    }
    
	public void initialize() {
        viewLabel = new JLabel("", SwingConstants.LEFT);
        mapName = new JLabel("", SwingConstants.LEFT);
        view = new JLabel("", SwingConstants.LEFT);
        infoLabel = new JLabel("", SwingConstants.LEFT);
        connection = new JLabel("", SwingConstants.LEFT);
        speed = new JLabel("", SwingConstants.LEFT);
        playerNumber = new JLabel("", SwingConstants.LEFT);
        playerName = new JLabel("", SwingConstants.LEFT);
        teamMemberName = new JLabel("", SwingConstants.LEFT);
        teamNumber = new JLabel("", SwingConstants.LEFT);
        positionLabel = new JLabel("", SwingConstants.LEFT);
        position = new JLabel("", SwingConstants.LEFT);
        angle = new JLabel("", SwingConstants.LEFT);
        sensorLabel = new JLabel("", SwingConstants.LEFT);
        ultrasonic = new JLabel("", SwingConstants.LEFT);
        light = new JLabel("", SwingConstants.LEFT);
        infrared = new JLabel("", SwingConstants.LEFT);
        infoLabel18 = new JLabel("", SwingConstants.LEFT);
        infoLabel19 = new JLabel("", SwingConstants.LEFT);
        infoLabel20 = new JLabel("", SwingConstants.LEFT);
        infoLabel21 = new JLabel("", SwingConstants.LEFT);
        infoLabel22 = new JLabel("", SwingConstants.LEFT);
        infoLabel23 = new JLabel("", SwingConstants.LEFT);
        infoLabel24 = new JLabel("", SwingConstants.LEFT);
        
        setOpaque(false);
        
        final GroupLayout outputLayout = new GroupLayout(this);
        setLayout(outputLayout);
        outputLayout.setAutoCreateGaps(true);
        outputLayout.setAutoCreateContainerGaps(true);
        outputLayout.setHorizontalGroup(outputLayout
                .createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(mapName, 160, 160, 160)
                .addComponent(view, 160, 160, 160).addComponent(infoLabel, 160, 160, 160)
                .addComponent(connection, 160, 160, 160).addComponent(speed, 160, 160, 160)
                .addComponent(playerNumber, 160, 160, 160).addComponent(playerName, 160, 160, 160)
                .addComponent(teamMemberName, 160, 160, 160).addComponent(teamNumber, 160, 160, 160)
                .addComponent(positionLabel, 160, 160, 160).addComponent(position, 160, 160, 160)
                .addComponent(angle, 160, 160, 160).addComponent(sensorLabel, 160, 160, 160)
                .addComponent(ultrasonic, 160, 160, 160).addComponent(light, 160, 160, 160)
                .addComponent(infrared, 160, 160, 160).addComponent(infoLabel18)
                .addComponent(infoLabel19).addComponent(infoLabel20)
                .addComponent(infoLabel21).addComponent(infoLabel22)
                .addComponent(infoLabel23).addComponent(infoLabel24));
        outputLayout.setVerticalGroup(outputLayout.createSequentialGroup()
                .addComponent(mapName)
                .addComponent(view).addComponent(infoLabel)
                .addComponent(connection).addComponent(speed)
                .addComponent(playerNumber).addComponent(playerName)
                .addComponent(teamMemberName).addComponent(teamNumber)
                .addComponent(positionLabel).addComponent(position)
                .addComponent(angle).addComponent(sensorLabel)
                .addComponent(ultrasonic).addComponent(light)
                .addComponent(infrared).addComponent(infoLabel18)
                .addComponent(infoLabel19).addComponent(infoLabel20)
                .addComponent(infoLabel21).addComponent(infoLabel22)
                .addComponent(infoLabel23).addComponent(infoLabel24));

        setVisible(true);
	}

    @Override
    protected void paintComponent(final Graphics graph) {
        super.paintComponent(graph);
        paintFrame(graph);
    }

    private void paintFrame(final Graphics graph) {
        Graphics2D g2 = (Graphics2D) graph;
        Stroke originalStroke = g2.getStroke();
        g2.setStroke(new BasicStroke(4, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
        g2.draw(new Rectangle2D.Double(2, 7, getWidth() - 4, 50));
        g2.draw(new Rectangle2D.Double(2, 57, getWidth() - 4, 137));
        g2.draw(new Rectangle2D.Double(2, 194, getWidth() - 4, 50));
        g2.draw(new Rectangle2D.Double(2, 244, getWidth() - 4, 72));
        g2.setStroke(originalStroke);
    }
}