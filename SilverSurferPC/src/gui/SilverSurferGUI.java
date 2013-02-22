package gui;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

import simulator.pilot.SimulationPilot;
import simulator.viewport.ViewPort;

import communication.Communicator;
import communication.StatusInfoBuffer;

public class SilverSurferGUI {

    private static JFrame frame;
    private static StatusInfoBuffer statusInfoBuffer;
    private static ViewPort simulationPanel;
    private static SimulationPilot simulationPilot;
    private static Communicator communicator;

    private static JButton ZoomInButton;
    private static JButton ZoomOutButton;

    private static JSpinner angle;
    private static JButton turnLeftButton;
    private static JButton turnRightButton;
    private static JSpinner length;
    private static JButton moveButton;

    private static JLabel infoLabel1;
    private static JLabel infoLabel2;
    private static JLabel infoLabel3;
    private static JLabel infoLabel4;
    private static JLabel infoLabel5;
    private static JLabel infoLabel6;
    private static JLabel infoLabel7;

    private static JPanel mappingPanel;

    public static void changeSpeed(final int value) {
        communicator.setSpeed(value);
        infoLabel2.setText("Speed level: " + value);
        System.out.println(communicator.getConsoleTag()
                + " Current Speed Level: " + value + ".");
    }

    protected static void clearScreen() {
        System.out.println("[GUI] Screen cleared.");
        simulationPanel.resetMap();
    }

    protected static void connectBluetooth() {
        try {
            communicator.setRobotConnected(true);
            simulationPanel.resetMap();
            // TOON Juiste pilot aanmaken
            // simulationPilot.setRobotControllable(true);
            simulationPilot.setTile(0, 0);
            // TOON robotrelativeposition(0,0)
            // statusInfoBuffer.setXCoordinateRelative(0);
            // statusInfoBuffer.setYCoordinateRelative(0);
            System.out.println("[CONNECTION] Connection established.");
            changeSpeed(2);
        } catch (final Exception e) {
            System.out
                    .println("[CONNECTION] Oops! Something went wrong connecting! \n[CONNECTION] Please make sure your robot and bluetooth are turned on.");
        }
    }

    private static javax.swing.border.Border createBorder() {
        return BorderFactory.createEtchedBorder(1);
    }

    protected static void disconnectBluetooth() {
        try {
            communicator.setRobotConnected(false);
            simulationPanel.resetMap();
            // TOON pilot vervangen door simpilot i.p.v. robotpilot?
            // simulationPilot.setRobotControllable(true);
            System.out
                    .println("[CONNECTION] Connection succesfully closed. Entered simulator mode.");
            changeSpeed(2);
        } catch (final Exception e) {
            System.out
                    .println("[CONNECTION] Oops! Something went wrong disconnecting!");
        }
    }

    public static StatusInfoBuffer getStatusInfoBuffer() {
        return statusInfoBuffer;
    }

    public static void main(final String[] args) {
        final SilverSurferGUI SSG = new SilverSurferGUI();
        SSG.createAndShowGUI();
    }

    // private static JPanel consolePanel;
    // private static Console console;
    private SensorGraph sensorGraph;

    private void addListeners() {
        ZoomInButton.addMouseListener(new MouseListener() {
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
        ZoomOutButton.addMouseListener(new MouseListener() {
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
                final MoveTurnThread MTT = new MoveTurnThread("MTT",
                        communicator, 0,
                        -1 * Integer.parseInt(angle.getValue().toString()), 0,
                        0);
                MTT.start();
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
                final MoveTurnThread MTT = new MoveTurnThread("MTT",
                        communicator, 0, Integer.parseInt(angle.getValue()
                                .toString()), 0, 0);
                MTT.start();
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
                final MoveTurnThread MTT = new MoveTurnThread("MTT",
                        communicator, Integer.parseInt(length.getValue()
                                .toString()), 0, 0, 0);
                MTT.start();
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

    private void createAndShowGUI() {
        frame = new JFrame("Silver Surfer Command Center");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setBackground(new Color(221, 230, 231));

        final JPanel scalePanel = scalePanel();
        final JPanel directionPanel = directionPanel();
        final JPanel infoPanel = infoPanel();
        mappingPanel = mappingPanel();
        // JPanel consolePanel = consolePanel();
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
                                .addComponent(mappingPanel)
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
                                .addComponent(mappingPanel)
                                .addComponent(sensorPanel, 170, 170, 170)));
        frameLayout.linkSize(SwingConstants.HORIZONTAL, directionPanel);
        frameLayout.linkSize(SwingConstants.VERTICAL, directionPanel);

        frame.pack();
        frame.setSize(1000, 800);
        frame.setVisible(true);

        statusInfoBuffer = new StatusInfoBuffer();
        communicator = new Communicator(simulationPilot, statusInfoBuffer);
        statusInfoBuffer.setCommunicator(communicator);
        System.out.println("[CONNECTION] Entered simulator mode.");

        addListeners();

        changeSpeed(2);
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
        // directionPanel.setBorder(BorderFactory.createTitledBorder(
        // createBorder(), "Turn and Move"));
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

    public Communicator getCommunicator() {
        return communicator;
    }

    public JFrame getFrame() {
        return frame;
    }

    /*
     * Tijdelijk niet nodig private. JPanel consolePanel() { console = new
     * Console(); JScrollPane scrollPane = new JScrollPane(console);
     * console.setScroll(scrollPane);
     * 
     * consolePanel = new JPanel(); consolePanel.setOpaque(false);
     * 
     * GroupLayout consolelayout = new GroupLayout(consolePanel);
     * consolePanel.setLayout(consolelayout);
     * consolelayout.setAutoCreateGaps(true);
     * consolelayout.setAutoCreateContainerGaps(true);
     * consolelayout.setHorizontalGroup(consolelayout.createSequentialGroup()
     * .addComponent(scrollPane));
     * consolelayout.setVerticalGroup(consolelayout.createSequentialGroup()
     * .addComponent(scrollPane));
     * 
     * return consolePanel; }
     */

    public ViewPort getSimulationPanel() {
        return simulationPanel;
    }

    public SimulationPilot getSimulationPilot() {
        return simulationPilot;
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
        outputPanel.setBorder(BorderFactory.createTitledBorder(createBorder(),
                "Output"));
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

    private JPanel mappingPanel() {
        // TODO vervangen door simulatorpanel
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
        return new JPanel();
    }

    private JPanel scalePanel() {
        final ImageIcon MagnifyIcon = new ImageIcon(
                "resources/magnifiers/Magnify.png", "A magnifier");
        ZoomInButton = new JButton(MagnifyIcon);
        ZoomInButton.setOpaque(false);
        ZoomInButton.setContentAreaFilled(false);
        ZoomInButton.setBorderPainted(false);
        final ImageIcon DeMagnifyIcon = new ImageIcon(
                "resources/magnifiers/Demagnify.png", "A demagnifier");
        ZoomOutButton = new JButton(DeMagnifyIcon);
        ZoomOutButton.setOpaque(false);
        ZoomOutButton.setContentAreaFilled(false);
        ZoomOutButton.setBorderPainted(false);

        final JPanel scalePanel = new JPanel();
        // scalePanel.setBorder(BorderFactory.createTitledBorder(createBorder(),
        // "Zoom"));
        scalePanel.setOpaque(false);

        final GroupLayout scaleLayout = new GroupLayout(scalePanel);
        scalePanel.setLayout(scaleLayout);
        scaleLayout.setAutoCreateGaps(true);
        scaleLayout.setAutoCreateContainerGaps(true);
        scaleLayout.setHorizontalGroup(scaleLayout.createSequentialGroup()
                .addComponent(ZoomInButton).addComponent(ZoomOutButton));
        scaleLayout.setVerticalGroup(scaleLayout
                .createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(ZoomInButton).addComponent(ZoomOutButton));

        return scalePanel;
    }

    private JPanel sensorPanel() {
        sensorGraph = new SensorGraph(this);
        sensorGraph.setOpaque(false);

        return sensorGraph;
    }

    public void updateCoordinates(final String s) {
        mappingPanel.setBorder(BorderFactory.createTitledBorder(createBorder(),
                s));
    }

    public void updateStatus() {
        try {
            int ultrasonicSensorValue;
            int lightSensorValue;

            if (!getCommunicator().getRobotConnected()) {
                ultrasonicSensorValue = getSimulationPilot()
                        .getUltraSensorValue();
                lightSensorValue = getSimulationPilot().getLightSensorValue();
            } else {
                ultrasonicSensorValue = getStatusInfoBuffer()
                        .getLatestUltraSensorInfo();
                lightSensorValue = getStatusInfoBuffer()
                        .getLatestLightSensorInfo();
            }
            sensorGraph
                    .addSensorValues(ultrasonicSensorValue, lightSensorValue);

            infoLabel1
                    .setText("Bluetooth: " + communicator.getRobotConnected());
            infoLabel3.setText("Ultrasonicsensor: " + ultrasonicSensorValue);
            infoLabel4.setText("Lightsensor: " + lightSensorValue);
            infoLabel5.setText("Left Motor: "
                    + statusInfoBuffer.getLeftMotorMoving() + " "
                    + statusInfoBuffer.getLeftMotorSpeed());
            infoLabel6.setText("Right Motor: "
                    + statusInfoBuffer.getRightMotorMoving() + " "
                    + statusInfoBuffer.getRightMotorSpeed());
            infoLabel7.setText("Busy: " + statusInfoBuffer.getBusy());
        } catch (final NullPointerException e) {

        }
    }

    public void zoomIn() {
        final ZoomThread ZT = new ZoomThread("ZT", simulationPanel, true);
        ZT.start();
    }

    public void zoomOut() {
        final ZoomThread ZT = new ZoomThread("ZT", simulationPanel, false);
        ZT.start();
    }
}