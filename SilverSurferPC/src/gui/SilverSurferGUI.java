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
    private static JButton zoomInButton, zoomOutButton, turnLeftButton, turnRightButton, moveButton;
    private static JLabel infoLabel1, infoLabel2, infoLabel3, infoLabel4, infoLabel5, infoLabel6, infoLabel7, infoLabel8, infoLabel9, infoLabel10, infoLabel11;
    private static JPanel scalePanel, directionPanel, infoPanel;
    private static SimulatorPanel simulatorPanel;
    private SensorGraph sensorPanel;
    private GUIMenuBar menuBar;
    private int updateStatusFPS = 3;
    private ActionListener updateStatus = new ActionListener() {

        @Override
        public void actionPerformed(final ActionEvent arg0) {
            updateStatus();
        }
    };

    public static void main(final String[] args) {
        final SilverSurferGUI SSG = new SilverSurferGUI();
        SSG.initializePanels();
        SSG.simulatorPanel();
        new Timer(1000 / SSG.updateStatusFPS, SSG.updateStatus).start();
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
    	simulatorPanel.connect();
    	System.out.println("[CONNECTION] Connection established.");
    	//TODO: clear screen? reset map?
    }

    protected static void disconnectBluetooth() {
    	simulatorPanel.disconnect();
        System.out.println("[CONNECTION] Connection succesfully closed. Entered simulator mode.");
    	//TODO: clear screen? reset map?
    }

    public void updateStatus() {
    	AbstractPilot pilot = simulatorPanel.getPrincipalPilot();
    	boolean robotConnected = pilot instanceof RobotPilot;
    	int ultraSensorValue = pilot.getUltraSensorValue();
    	int lightSensorValue = pilot.getLightSensorValue();
    	
    	sensorPanel.addSensorValues(ultraSensorValue, lightSensorValue);
        
        infoLabel1.setText("Bluetooth: " + robotConnected);
        infoLabel2.setText("Speed level: " + simulatorPanel.getSpeed());
        infoLabel3.setText(simulatorPanel.getMapName());
        infoLabel4.setText("-------------------");
        infoLabel5.setText("Ultrasonicsensor: " + ultraSensorValue);
        infoLabel6.setText("Lightsensor: " + lightSensorValue);
        infoLabel7.setText("-------------------");
        infoLabel8.setText("Coordinates: (" + pilot.getPosition().getX() + ", " +  pilot.getPosition().getY() + ")");
        infoLabel9.setText("Angle: " + pilot.getAngle());
        if(robotConnected) {
            infoLabel10.setText("-------------------");
        	infoLabel11.setText("Busy: " + ((RobotPilot)pilot).getBusy());
        }
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

    private void initializePanels() {
        scalePanel();
        directionPanel();
        infoPanel();
        sensorPanel();
        
        menuBar = new GUIMenuBar(this);

        addListeners();
    }

    private void scalePanel() {
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

        scalePanel = new JPanel();
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
    }

    private void directionPanel() {
        final JLabel angleLabel = new JLabel("Angle (degrees)",
                SwingConstants.CENTER);

        final SpinnerNumberModel angleModel = new SpinnerNumberModel(90, 0,
                1080, 1);
        angle = new JSpinner(angleModel);
        
        final ImageIcon turnLeftIcon = new ImageIcon("resources/direction_arrows/turnleft.png", "A leftward turning arrow");
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

        directionPanel = new JPanel();
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
                .addComponent(infoLabel11));
        outputLayout.setVerticalGroup(outputLayout.createSequentialGroup()
                .addComponent(infoLabel1).addComponent(infoLabel2)
                .addComponent(infoLabel3).addComponent(infoLabel4)
                .addComponent(infoLabel5).addComponent(infoLabel6)
                .addComponent(infoLabel7).addComponent(infoLabel8)
                .addComponent(infoLabel9).addComponent(infoLabel10)
                .addComponent(infoLabel11));
    }

    private void sensorPanel() {
        sensorPanel = new SensorGraph(this);
        sensorPanel.setOpaque(false);
    }
    
    private void addListeners() {
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

    private void simulatorPanel() {
        simulatorPanel = new SimulatorPanel();
        
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
    }
}