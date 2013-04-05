package gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

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
    private static JPanel legendPanel, inputPanel, infoPanel;
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
    private static Color[] teamColors = new Color[] {new Color(249, 244, 99), new Color(242, 150, 60), new Color(145, 254, 126), new Color(114, 225, 246), new Color(134, 46, 250), new Color(255, 63, 72), new Color(139, 137, 137)};
    
    public static void main(final String[] args) {
        final SilverSurferGUI SSG = new SilverSurferGUI();
        SSG.initializePanels();
        SSG.setupView();
        new Timer(1000 / SSG.updateStatusFPS, SSG.updateStatus).start();
    }

    public static SimulatorPanel getSimulatorPanel() {
        return simulatorPanel;
    }

    public void toggleInputPanel() {
        if (inputPanel.isVisible())
            inputPanel.setVisible(false);
        else
            inputPanel.setVisible(true);
    }

    public void toggleInfoPanel() {
        if (infoPanel.isVisible()) {
            legendPanel.setVisible(false);
            infoPanel.setVisible(false);
        } else {
            legendPanel.setVisible(true);
            infoPanel.setVisible(true);
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
        
        sensorPanel.addSensorValues(ultraSensorValue, lightSensorValue);

        infoLabel1.setText("----- VIEW -----");
        infoLabel2.setText(simulatorPanel.getMapName());
        infoLabel3.setText(simulatorPanel.getView());
        infoLabel4.setText("----- INFO -----");
        infoLabel5.setText("Bluetooth: " + (pilot instanceof RobotPilot));
        infoLabel6.setText("Speed level: " + simulatorPanel.getSpeed());
        infoLabel7.setText("Player number: " + pilot.getPlayerNumber());
        infoLabel8.setText("Player name: " + pilot.getPlayerName());
        infoLabel9.setText("Team member name: " + pilot.getTeamMemberName());
        infoLabel10.setText("Team number: " + pilot.getTeamNumber());
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
        inputPanel();
        legendPanel();
        infoPanel();
        sensorPanel();
        simulatorPanel = new SimulatorPanel(teamColors);
        addListeners();
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
        legendLabel1.setText("Player 1");
        legendLabel2 = new JLabel("", SwingConstants.CENTER);
        legendLabel2.setForeground(teamColors[1]);
        legendLabel2.setText("Player 2");
        legendLabel3 = new JLabel("", SwingConstants.CENTER);
        legendLabel3.setForeground(teamColors[2]);
        legendLabel3.setText("Player 3");
        legendLabel4 = new JLabel("", SwingConstants.CENTER);
        legendLabel4.setForeground(teamColors[3]);
        legendLabel4.setText("Player 4");
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
        legendPanel.setLayout(legendLayout);
        legendLayout.setAutoCreateGaps(true);
        legendLayout.setAutoCreateContainerGaps(true);
        legendLayout.setHorizontalGroup(legendLayout
                .createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(legendLabel1).addComponent(legendLabel2)
                .addComponent(legendLabel3).addComponent(legendLabel4)
                .addComponent(legendLabel5).addComponent(legendLabel6)
                .addComponent(legendLabel7));
        legendLayout.setVerticalGroup(legendLayout.createSequentialGroup()
                .addComponent(legendLabel1).addComponent(legendLabel2)
                .addComponent(legendLabel3).addComponent(legendLabel4)
                .addComponent(legendLabel5).addComponent(legendLabel6)
                .addComponent(legendLabel7));

        legendPanel.setVisible(false);
    }

    private void infoPanel() {
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

        infoPanel = new JPanel();
        infoPanel.setOpaque(false);

        final GroupLayout outputLayout = new GroupLayout(infoPanel);
        infoPanel.setLayout(outputLayout);
        outputLayout.setAutoCreateGaps(true);
        outputLayout.setAutoCreateContainerGaps(true);
        outputLayout.setHorizontalGroup(outputLayout
                .createParallelGroup(GroupLayout.Alignment.CENTER)
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

        infoPanel.setVisible(false);
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

        frame.setJMenuBar(menuBar);

        final GroupLayout frameLayout = new GroupLayout(frame.getContentPane());
        frame.getContentPane().setLayout(frameLayout);
        frameLayout.setHorizontalGroup(frameLayout
                .createSequentialGroup()
                .addGroup(
                        frameLayout
                                .createParallelGroup(
                                        GroupLayout.Alignment.CENTER)
                                .addComponent(inputPanel)
                                .addComponent(legendPanel)
                                .addComponent(infoPanel))
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
                                .addComponent(inputPanel)
                                .addComponent(legendPanel)
                                .addComponent(infoPanel))
                .addGroup(
                        frameLayout.createSequentialGroup()
                                .addComponent(simulatorPanel)
                                .addComponent(sensorPanel, 170, 170, 170)));
        frameLayout.linkSize(SwingConstants.HORIZONTAL, inputPanel);
        frameLayout.linkSize(SwingConstants.VERTICAL, inputPanel);

        frame.pack();
        frame.setSize(1000, 800);
        frame.setVisible(true);

        System.out.println("[CONNECTION] Entered simulator mode.");
    }

    private void addListeners() {
    	turnLeftButton.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(final MouseEvent arg0) {
                simulatorPanel.turnLeftPrincipalPilot(Integer.parseInt(angle.getValue().toString()));
            }

            @Override
            public void mouseEntered(final MouseEvent arg0) {
            }

            @Override
            public void mouseExited(final MouseEvent arg0) {
            }

            @Override
            public void mousePressed(final MouseEvent arg0) {
            }

            @Override
            public void mouseReleased(final MouseEvent arg0) {
            }
        });
        turnRightButton.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(final MouseEvent arg0) {
                simulatorPanel.turnRightPrincipalPilot(Integer.parseInt(angle.getValue().toString()));
            }

            @Override
            public void mouseEntered(final MouseEvent arg0) {
            }

            @Override
            public void mouseExited(final MouseEvent arg0) {
            }

            @Override
            public void mousePressed(final MouseEvent arg0) {
            }

            @Override
            public void mouseReleased(final MouseEvent arg0) {
            }
        });
        moveButton.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(final MouseEvent arg0) {
                simulatorPanel.travelPrincipalPilot(Integer.parseInt(length.getValue().toString()));
            }

            @Override
            public void mouseEntered(final MouseEvent arg0) {
            }

            @Override
            public void mouseExited(final MouseEvent arg0) {
            }

            @Override
            public void mousePressed(final MouseEvent arg0) {
            }

            @Override
            public void mouseReleased(final MouseEvent arg0) {
            }
        });
    }
}