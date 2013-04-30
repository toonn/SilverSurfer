package gui;

import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import simulator.pilot.AbstractPilot;
import simulator.pilot.RobotPilot;
import simulator.viewport.SimulatorPanel;

public class SilverSurferGUI {

    private static JFrame frame = new JFrame("Silver Surfer Command Center");
    private static JSpinner angle, length;
    private static JButton turnLeftButton, turnRightButton, moveButton;
    private static JLabel infoLabel1, infoLabel2, infoLabel3, infoLabel4, infoLabel5, infoLabel6, infoLabel7, infoLabel8;
    private static JLabel infoLabel9, infoLabel10, infoLabel11, infoLabel12, infoLabel13, infoLabel14, infoLabel15, infoLabel16;
    private static JLabel infoLabel17, infoLabel18;
    private static JLabel legendLabel1, legendLabel2, legendLabel3, legendLabel4, legendLabel5, legendLabel6, legendLabel7;
    private static JPanel legendPanel, inputPanel, sidePanel;
    private static SimulatorPanel simulatorPanel;
    private static SensorGraph sensorPanel;
    private GUIMenuBar menuBar;
    private int updateStatusFPS = 10;
    private ActionListener updateStatus = new ActionListener() {

        @Override
        public void actionPerformed(final ActionEvent arg0) {
            updateStatus();
        }
    };
    //Yellow, Orange, Green, Blue, Purple, Red, Gray
    private static Color[] teamColors = new Color[] {new Color(243, 220, 27), new Color(242, 150, 60), new Color(86, 145, 150), new Color(114, 182, 77), new Color(134, 46, 250), new Color(255, 63, 72), new Color(139, 137, 137)};
    
    public static void main(final String[] args) {
        final SilverSurferGUI SSG = new SilverSurferGUI();
        SSG.initializePanels();
        SSG.setupView();
        new Timer(1000 / SSG.updateStatusFPS, SSG.updateStatus).start();
    }

    public static SimulatorPanel getSimulatorPanel() {
        return simulatorPanel;
    }
    
    public void toggleAll() {
    	if (sidePanel.isVisible()) {
            legendPanel.setVisible(false);
            sidePanel.setVisible(false);
            sensorPanel.setVisible(false);
    		simulatorPanel.toggleAll(false);
    	} else {
            legendPanel.setVisible(true);
            sidePanel.setVisible(true);
            sensorPanel.setVisible(true);
    		simulatorPanel.toggleAll(true);
    	}
    }

    public void toggleSidePanel() {
        if (sidePanel.isVisible()) {
            legendPanel.setVisible(false);
            sidePanel.setVisible(false);
        } else {
            legendPanel.setVisible(true);
            sidePanel.setVisible(true);
        }
    }

    public void toggleSensorPanel() {
        if (sensorPanel.isVisible())
            sensorPanel.setVisible(false);
        else
            sensorPanel.setVisible(true);
    }

    public void pauseSensorPanel() {
        sensorPanel.togglePause();
    }

    public void updateStatus() {
        AbstractPilot pilot = simulatorPanel.getPrincipalPilot();
        int ultraSensorValue = pilot.getUltraSensorValue();
        int lightSensorValue = pilot.getLightSensorValue();
        int infraSensorValue = pilot.getInfraRedSensorValue();
        
        sensorPanel.addSensorValues(ultraSensorValue, lightSensorValue, infraSensorValue);

        infoLabel1.setText("----- VIEW -----");
        if(simulatorPanel.getMapName() != "/")
        	infoLabel2.setText(simulatorPanel.getMapName());
        else
        	infoLabel2.setText("No Map loaded");
        infoLabel3.setText(simulatorPanel.getView());
        infoLabel4.setText("----- INFO -----");
        infoLabel5.setText("Bluetooth: " + (pilot instanceof RobotPilot));
        infoLabel6.setText("Speed level: " + simulatorPanel.getSpeed());
        infoLabel7.setText("Player number: " + pilot.getPlayerNumber());
        infoLabel8.setText("Player: " + pilot.getPlayerName());
        infoLabel9.setText("Team member: " + pilot.getTeamMemberName());
        if(pilot.getTeamNumber() != -1)
        	infoLabel10.setText("Team: " + pilot.getTeamNumber());
        else
        	infoLabel10.setText("Team: none");
        infoLabel11.setText("----- POSITION -----");
        infoLabel12.setText("Coordinates: (" + pilot.getPosition().getX() + ", " + pilot.getPosition().getY() + ")");
        infoLabel13.setText("Angle: " + pilot.getAngle());
        infoLabel14.setText("----- SENSORS -----");
        infoLabel15.setText("Ultrasonic: " + ultraSensorValue);
        infoLabel16.setText("Light: " + lightSensorValue);
        infoLabel17.setText("Infrared: " + infraSensorValue);
    }

    private void initializePanels() {
        menuBar = new GUIMenuBar(this, frame);
        legendPanel();
        sidePanel();
        sensorPanel();
        simulatorPanel = new SimulatorPanel(teamColors);
    }

    private void inputPanel() {
        final JLabel angleLabel = new JLabel("Angle (degrees)", SwingConstants.CENTER);
        final SpinnerNumberModel angleModel = new SpinnerNumberModel(90, 0, 1080, 1);
        angle = new JSpinner(angleModel);
        final ImageIcon turnLeftIcon = new ImageIcon("resources/direction_arrows/turnleft.png", "A leftward turning arrow");
        turnLeftButton = new JButton(turnLeftIcon);
        turnLeftButton.setOpaque(false);
        turnLeftButton.setContentAreaFilled(false);
        turnLeftButton.setBorderPainted(false);
        final ImageIcon turnRightIcon = new ImageIcon("resources/direction_arrows/turnright.png", "A rightward turning arrow");
        turnRightButton = new JButton(turnRightIcon);
        turnRightButton.setOpaque(false);
        turnRightButton.setContentAreaFilled(false);
        turnRightButton.setBorderPainted(false);
        
        final JLabel lengthLabel = new JLabel("Length (centimeters)", SwingConstants.CENTER);
        final SpinnerNumberModel lenghtModel = new SpinnerNumberModel(40, -1000, 1000, 1);
        length = new JSpinner(lenghtModel);
        final ImageIcon moveIcon = new ImageIcon("resources/direction_arrows/move.png", "A straightahead arrow");
        moveButton = new JButton(moveIcon);
        moveButton.setOpaque(false);
        moveButton.setContentAreaFilled(false);
        moveButton.setBorderPainted(false);

        inputPanel = new JPanel();
        inputPanel.setOpaque(false);

        final GroupLayout directionLayout = new GroupLayout(inputPanel);
        inputPanel.setLayout(directionLayout);
        directionLayout.setAutoCreateGaps(true);
        directionLayout.setAutoCreateContainerGaps(true);
        directionLayout
                .setHorizontalGroup(directionLayout
                        .createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(angleLabel)
                        .addGroup(
                                directionLayout.createSequentialGroup()
                                        .addComponent(angle)
                                        .addComponent(turnLeftButton)
                                        .addComponent(turnRightButton))
                        .addComponent(lengthLabel)
                        .addGroup(
                                directionLayout.createSequentialGroup()
                                        .addComponent(length)
                                        .addComponent(moveButton)));
        directionLayout
                .setVerticalGroup(directionLayout
                        .createSequentialGroup()
                        .addComponent(angleLabel)
                        .addGroup(
                                directionLayout
                                        .createParallelGroup(
                                                GroupLayout.Alignment.CENTER)
                                        .addComponent(angle)
                                        .addComponent(turnLeftButton)
                                        .addComponent(turnRightButton))
                        .addComponent(lengthLabel)
                        .addGroup(
                                directionLayout
                                        .createParallelGroup(
                                                GroupLayout.Alignment.CENTER)
                                        .addComponent(length)
                                        .addComponent(moveButton)));

        inputPanel.setVisible(false);
    }

    private void legendPanel() {
   
       

        legendLabel1 = new JLabel("", SwingConstants.CENTER);
        legendLabel1.setForeground(teamColors[0]);
        legendLabel1.setText("Player 0");
        legendLabel2 = new JLabel("", SwingConstants.CENTER);
        legendLabel2.setForeground(teamColors[1]);
        legendLabel2.setText("Player 1");
        legendLabel3 = new JLabel("", SwingConstants.CENTER);
        legendLabel3.setForeground(teamColors[2]);
        legendLabel3.setText("Player 2");
        legendLabel4 = new JLabel("", SwingConstants.CENTER);
        legendLabel4.setForeground(teamColors[3]);
        legendLabel4.setText("Player 3");
        legendLabel5 = new JLabel("", SwingConstants.CENTER);
        legendLabel5.setForeground(teamColors[4]);
        legendLabel5.setText("Team 1");
        legendLabel6 = new JLabel("", SwingConstants.CENTER);
        legendLabel6.setForeground(teamColors[5]);
        legendLabel6.setText("Team 2");
        legendLabel7 = new JLabel("", SwingConstants.CENTER);
        legendLabel7.setForeground(teamColors[6]);
        legendLabel7.setText("Invalid");

        legendPanel = new JPanel();
        legendPanel.setOpaque(false);

        final GroupLayout legendLayout = new GroupLayout(legendPanel);
       
        JLabel picLabel = new JLabel(new ImageIcon("resources/colors+label.png"));
        legendPanel.setLayout(legendLayout);
        legendLayout.setAutoCreateGaps(true);
        legendLayout.setAutoCreateContainerGaps(true);
        legendLayout.setHorizontalGroup(legendLayout
                .createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(picLabel));
        legendLayout.setVerticalGroup(legendLayout.createSequentialGroup()
                .addComponent(picLabel));

        legendPanel.setVisible(false);
    }

    private void sidePanel() {
        infoLabel1 = new JLabel("", SwingConstants.CENTER);
        infoLabel2 = new JLabel("", SwingConstants.CENTER);
        infoLabel3 = new JLabel("", SwingConstants.CENTER);
        infoLabel4 = new JLabel("", SwingConstants.CENTER);
        infoLabel5 = new JLabel("", SwingConstants.CENTER);
        infoLabel6 = new JLabel("", SwingConstants.CENTER);
        infoLabel7 = new JLabel("", SwingConstants.CENTER);
        infoLabel8 = new JLabel("", SwingConstants.CENTER);
        infoLabel9 = new JLabel("", SwingConstants.CENTER);
        infoLabel10 = new JLabel("", SwingConstants.CENTER);
        infoLabel11 = new JLabel("", SwingConstants.CENTER);
        infoLabel12 = new JLabel("", SwingConstants.CENTER);
        infoLabel13 = new JLabel("", SwingConstants.CENTER);
        infoLabel14 = new JLabel("", SwingConstants.CENTER);
        infoLabel15 = new JLabel("", SwingConstants.CENTER);
        infoLabel16 = new JLabel("", SwingConstants.CENTER);
        infoLabel17 = new JLabel("", SwingConstants.CENTER);
        infoLabel18 = new JLabel("", SwingConstants.CENTER);
        JLabel mapPicLabel = new JLabel(new ImageIcon("resources/map.png"));
        JLabel sensorLabel = new JLabel(new ImageIcon("resources/sensors.png"));

        sidePanel = new JPanel();
        sidePanel.setOpaque(false);

        final GroupLayout outputLayout = new GroupLayout(sidePanel);
        sidePanel.setLayout(outputLayout);
        outputLayout.setAutoCreateGaps(true);
        outputLayout.setAutoCreateContainerGaps(true);
        outputLayout.setHorizontalGroup(outputLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(infoLabel1).addComponent(infoLabel2)
                .addComponent(infoLabel3).addComponent(infoLabel4)
                .addComponent(infoLabel5).addComponent(infoLabel6)
                .addComponent(infoLabel7).addComponent(infoLabel8)
                .addComponent(infoLabel9).addComponent(infoLabel10)
                .addComponent(infoLabel11).addComponent(infoLabel12)
                .addComponent(infoLabel13).addComponent(infoLabel14)
                .addComponent(infoLabel15).addComponent(infoLabel16)
                .addComponent(infoLabel17).addComponent(infoLabel18));
        outputLayout.setVerticalGroup(outputLayout.createSequentialGroup()
                .addComponent(infoLabel1).addComponent(infoLabel2)
                .addComponent(infoLabel3).addComponent(infoLabel4)
                .addComponent(infoLabel5).addComponent(infoLabel6)
                .addComponent(infoLabel7).addComponent(infoLabel8)
                .addComponent(infoLabel9).addComponent(infoLabel10)
                .addComponent(infoLabel11).addComponent(infoLabel12)
                .addComponent(infoLabel13).addComponent(infoLabel14)
                .addComponent(infoLabel15).addComponent(infoLabel16)
                .addComponent(infoLabel17).addComponent(infoLabel18));

        sidePanel.setVisible(false);
    }

    private void sensorPanel() {
        sensorPanel = new SensorGraph();
        sensorPanel.setOpaque(false);
        sensorPanel.setVisible(false);
    }

    private void setupView() {
        frame = new JFrame("Silver Surfer Command Center");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setBackground(new Color(221, 230, 231));
        //new Color(221, 230, 231) -- blueish
        //new Color(238, 238, 238) -- same white as simulatorpanel

        frame.setJMenuBar(menuBar);

        final GroupLayout frameLayout = new GroupLayout(frame.getContentPane());
        frame.getContentPane().setLayout(frameLayout);
        frameLayout.setHorizontalGroup(frameLayout
                .createSequentialGroup()
                .addGroup(
                        frameLayout
                                .createParallelGroup(
                                        GroupLayout.Alignment.CENTER)
                                .addComponent(legendPanel)
                                .addComponent(sidePanel))
                .addGroup(
                        frameLayout
                                .createParallelGroup(
                                        GroupLayout.Alignment.CENTER)
                                .addComponent(simulatorPanel)
                                .addComponent(sensorPanel)));
        frameLayout.setVerticalGroup(frameLayout
                .createParallelGroup(GroupLayout.Alignment.CENTER)
                .addGroup(
                        frameLayout.createSequentialGroup()
                                .addComponent(legendPanel)
                                .addComponent(sidePanel))
                .addGroup(
                        frameLayout.createSequentialGroup()
                                .addComponent(simulatorPanel)
                                .addComponent(sensorPanel, 170, 170, 170)));
        
        frame.pack();
        frame.setSize(1000, 800);
        frame.setVisible(true);

        System.out.println("[CONNECTION] Entered simulator mode.");
    }
}