package gui;

import java.awt.Color;
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

import simulator.viewport.SimulatorPanel;

public class SilverSurferGUI {

    private static JFrame frame;
    private static JSpinner angle, length;
    private static JButton zoomInButton, zoomOutButton, turnLeftButton, turnRightButton, moveButton;
    private static JLabel infoLabel1, infoLabel2, infoLabel3, infoLabel4, infoLabel5, infoLabel6, infoLabel7;
    private static SimulatorPanel simulatorPanel;
    private SensorGraph sensorGraph;

    public static void main(final String[] args) {
        final SilverSurferGUI SSG = new SilverSurferGUI();
        SSG.createAndShowGUI();
    }

    public JFrame getFrame() {
        return frame;
    }

    public static SimulatorPanel getSimulatorPanel() {
        return simulatorPanel;
    }

    protected void clearScreen() {
        // TODO clearScreen
    	// System.out.println("[GUI] Screen cleared.");
        // simulatorPanel.resetMap();
    }

    protected static void connectBluetooth() {
        // try {
        // //TODO connect
        // communicator.setRobotConnected(true);
        // simulatorPanel.resetMap();
        // // TOON Juiste pilot aanmaken
        // // simulationPilot.setRobotControllable(true);
        // simulationPilot.setTile(0, 0);
        // // TOON robotrelativeposition(0,0)
        // // statusInfoBuffer.setXCoordinateRelative(0);
        // // statusInfoBuffer.setYCoordinateRelative(0);
        // System.out.println("[CONNECTION] Connection established.");
        // changeSpeed(2);
        // } catch (final Exception e) {
        // System.out
        // .println("[CONNECTION] Oops! Something went wrong connecting! \n[CONNECTION] Please make sure your robot and bluetooth are turned on.");
        // }
    }

    protected static void disconnectBluetooth() {
        // try {
        // // TODO disconnect
        // communicator.setRobotConnected(false);
        // simulatorPanel.resetMap();
        // // TOON pilot vervangen door simpilot i.p.v. robotpilot?
        // // simulationPilot.setRobotControllable(true);
        // System.out
        // .println("[CONNECTION] Connection succesfully closed. Entered simulator mode.");
        // changeSpeed(2);
        // } catch (final Exception e) {
        // System.out
        // .println("[CONNECTION] Oops! Something went wrong disconnecting!");
        // }
    }

    public void updateCoordinates(final String s) {
        // TODO updatecoordinates
    	//simulatorPanel.setBorder(BorderFactory.createTitledBorder(createBorder(), s));
    }

    public void updateStatus() {
        // TODO updatestatus
        // try {
        // int ultrasonicSensorValue;
        // int lightSensorValue;
        //
        // if (!getCommunicator().getRobotConnected()) {
        // ultrasonicSensorValue = principalPilot.getUltraSensorValue();
        // lightSensorValue = principalPilot.getLightSensorValue();
        // } else {
        // ultrasonicSensorValue = getStatusInfoBuffer()
        // .getLatestUltraSensorInfo();
        // lightSensorValue = getStatusInfoBuffer()
        // .getLatestLightSensorInfo();
        // }
        // sensorGraph
        // .addSensorValues(ultrasonicSensorValue, lightSensorValue);
        //
        // infoLabel1
        // .setText("Bluetooth: " + communicator.getRobotConnected());
        // infoLabel2.setText("Speed level: " + simulatorPanel.getSpeed());
        // infoLabel3.setText("Ultrasonicsensor: " + ultrasonicSensorValue);
        // infoLabel4.setText("Lightsensor: " + lightSensorValue);
        // infoLabel5.setText("Left Motor: "
        // + statusInfoBuffer.getLeftMotorMoving() + " "
        // + statusInfoBuffer.getLeftMotorSpeed());
        // infoLabel6.setText("Right Motor: "
        // + statusInfoBuffer.getRightMotorMoving() + " "
        // + statusInfoBuffer.getRightMotorSpeed());
        // infoLabel7.setText("Busy: " + statusInfoBuffer.getBusy());
        // } catch (final NullPointerException e) {
        //
        // }
    }

    public void zoomIn() {
        // // TODO zoomIn implement
        // final ZoomThread ZT = new ZoomThread("ZT", simulatorPanel, true);
        // ZT.start();
    }

    public void zoomOut() {
        // // TODO zoomOut implement
        // final ZoomThread ZT = new ZoomThread("ZT", simulatorPanel, false);
        // ZT.start();
    }

    private void createAndShowGUI() {
        frame = new JFrame("Silver Surfer Command Center");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setBackground(new Color(221, 230, 231));

        final JPanel scalePanel = scalePanel();
        final JPanel directionPanel = directionPanel();
        final JPanel infoPanel = infoPanel();
        simulatorPanel = simulatorPanel();
        final JPanel sensorPanel = sensorPanel();

        final GUIMenuBar menuBar = new GUIMenuBar(this);
        frame.setJMenuBar(menuBar);

        final GroupLayout frameLayout = new GroupLayout(frame.getContentPane());
        frame.getContentPane().setLayout(frameLayout);
        frameLayout.setHorizontalGroup(frameLayout
                .createSequentialGroup()
                .addGroup(
                        frameLayout
                                .createParallelGroup(
                                        GroupLayout.Alignment.CENTER)
                                .addComponent(scalePanel)
                                .addComponent(directionPanel)
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
                                .addComponent(scalePanel)
                                .addComponent(directionPanel)
                                .addComponent(infoPanel))
                .addGroup(
                        frameLayout.createSequentialGroup()
                                .addComponent(simulatorPanel)
                                .addComponent(sensorPanel, 170, 170, 170)));
        frameLayout.linkSize(SwingConstants.HORIZONTAL, directionPanel);
        frameLayout.linkSize(SwingConstants.VERTICAL, directionPanel);

        frame.pack();
        frame.setSize(1000, 800);
        frame.setVisible(true);

        System.out.println("[CONNECTION] Entered simulator mode.");

        addListeners();
    }

    private JPanel scalePanel() {
        final ImageIcon MagnifyIcon = new ImageIcon(
                "resources/magnifiers/Magnify.png", "A magnifier");
        zoomInButton = new JButton(MagnifyIcon);
        zoomInButton.setOpaque(false);
        zoomInButton.setContentAreaFilled(false);
        zoomInButton.setBorderPainted(false);
        final ImageIcon DeMagnifyIcon = new ImageIcon(
                "resources/magnifiers/Demagnify.png", "A demagnifier");
        zoomOutButton = new JButton(DeMagnifyIcon);
        zoomOutButton.setOpaque(false);
        zoomOutButton.setContentAreaFilled(false);
        zoomOutButton.setBorderPainted(false);

        final JPanel scalePanel = new JPanel();
        scalePanel.setOpaque(false);

        final GroupLayout scaleLayout = new GroupLayout(scalePanel);
        scalePanel.setLayout(scaleLayout);
        scaleLayout.setAutoCreateGaps(true);
        scaleLayout.setAutoCreateContainerGaps(true);
        scaleLayout.setHorizontalGroup(scaleLayout.createSequentialGroup()
                .addComponent(zoomInButton).addComponent(zoomOutButton));
        scaleLayout.setVerticalGroup(scaleLayout
                .createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(zoomInButton).addComponent(zoomOutButton));

        return scalePanel;
    }

    private JPanel directionPanel() {
        final JLabel angleLabel = new JLabel("Angle (degrees)",
                SwingConstants.CENTER);

        final SpinnerNumberModel angleModel = new SpinnerNumberModel(90, 0,
                1080, 1);
        angle = new JSpinner(angleModel);

        final ImageIcon turnLeftIcon = new ImageIcon(
                "resources/direction_arrows/turnleft.png",
                "A leftward turning arrow");
        turnLeftButton = new JButton(turnLeftIcon);
        turnLeftButton.setOpaque(false);
        turnLeftButton.setContentAreaFilled(false);
        turnLeftButton.setBorderPainted(false);
        final ImageIcon turnRightIcon = new ImageIcon(
                "resources/direction_arrows/turnright.png",
                "A rightward turning arrow");
        turnRightButton = new JButton(turnRightIcon);
        turnRightButton.setOpaque(false);
        turnRightButton.setContentAreaFilled(false);
        turnRightButton.setBorderPainted(false);
        final JLabel lengthLabel = new JLabel("Length (centimeters)",
                SwingConstants.CENTER);

        final SpinnerNumberModel lenghtModel = new SpinnerNumberModel(24, 0,
                1000, 1);
        length = new JSpinner(lenghtModel);

        final ImageIcon moveIcon = new ImageIcon(
                "resources/direction_arrows/move.png", "A straightahead arrow");
        moveButton = new JButton(moveIcon);
        moveButton.setOpaque(false);
        moveButton.setContentAreaFilled(false);
        moveButton.setBorderPainted(false);

        final JPanel directionPanel = new JPanel();
        directionPanel.setOpaque(false);

        final GroupLayout directionLayout = new GroupLayout(directionPanel);
        directionPanel.setLayout(directionLayout);
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

        return directionPanel;
    }
    
    private JPanel infoPanel() {
        infoLabel1 = new JLabel("", SwingConstants.CENTER);
        infoLabel2 = new JLabel("", SwingConstants.CENTER);
        infoLabel3 = new JLabel("", SwingConstants.CENTER);
        infoLabel4 = new JLabel("", SwingConstants.CENTER);
        infoLabel5 = new JLabel("", SwingConstants.CENTER);
        infoLabel6 = new JLabel("", SwingConstants.CENTER);
        infoLabel7 = new JLabel("", SwingConstants.CENTER);

        final JPanel outputPanel = new JPanel();
        outputPanel.setOpaque(false);

        final GroupLayout outputLayout = new GroupLayout(outputPanel);
        outputPanel.setLayout(outputLayout);
        outputLayout.setAutoCreateGaps(true);
        outputLayout.setAutoCreateContainerGaps(true);
        outputLayout.setHorizontalGroup(outputLayout
                .createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(infoLabel1).addComponent(infoLabel2)
                .addComponent(infoLabel3).addComponent(infoLabel4)
                .addComponent(infoLabel5).addComponent(infoLabel6)
                .addComponent(infoLabel7));
        outputLayout.setVerticalGroup(outputLayout.createSequentialGroup()
                .addComponent(infoLabel1).addComponent(infoLabel2)
                .addComponent(infoLabel3).addComponent(infoLabel4)
                .addComponent(infoLabel5).addComponent(infoLabel6)
                .addComponent(infoLabel7));
        return outputPanel;
    }

    private SimulatorPanel simulatorPanel() {
        // simulatorPanel = new
        // TOON vervangen door simulatorpanel
        // simulationPanel = new SimulationViewPort();
        // simulationPanel.setSize(20000, 20000);
        // simulationPanel.setBackground(Color.WHITE);
        // simulationPanel.setBorder(createBorder());
        //
        // mappingPanel = new JPanel();
        // mappingPanel.setBorder(BorderFactory.createTitledBorder(createBorder(),
        // "Simulator"));
        // mappingPanel.setOpaque(false);
        //
        // GroupLayout mappingLayout = new GroupLayout(mappingPanel);
        // mappingPanel.setLayout(mappingLayout);
        // mappingLayout.setAutoCreateGaps(true);
        // mappingLayout.setAutoCreateContainerGaps(true);
        // mappingLayout.setHorizontalGroup(mappingLayout.createSequentialGroup()
        // .addComponent(simulationPanel));
        // mappingLayout.setVerticalGroup(mappingLayout.createSequentialGroup()
        // .addComponent(simulationPanel));
        //
        // return mappingPanel;
        return new SimulatorPanel();
    }

    private JPanel sensorPanel() {
        sensorGraph = new SensorGraph(this);
        sensorGraph.setOpaque(false);

        return sensorGraph;
    }
    
    private void addListeners() {
    	// TODO geen threads?
        zoomInButton.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(final MouseEvent arg0) {
                zoomIn();
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
        zoomOutButton.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(final MouseEvent arg0) {
                zoomOut();
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