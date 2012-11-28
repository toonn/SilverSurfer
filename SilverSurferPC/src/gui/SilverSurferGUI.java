package gui;

import commands.Command;
import communication.*;
import simulator.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class SilverSurferGUI {

    private static JFrame frame;
    private static SimulationJPanel simulationPanel;
    private static StatusInfoBuffer statusInfoBuffer;
    private static BarDecoder barDecoder;
    private static Communicator communicator;

    private static JSpinner angle;
    private static JButton turnLeftButton;
    private static JButton turnRightButton;
    private static JSpinner length;
    private static JButton moveButton;

    private static JButton lookAroundButton;
    private static JButton alignWhiteLineButton;
    private static JButton alignWall;

    private static JLabel infoLabel1;
    private static JLabel infoLabel2;
    private static JLabel infoLabel3;
    private static JLabel infoLabel4;
    private static JLabel infoLabel5;
    private static JLabel infoLabel6;
    private static JLabel infoLabel7;
    private static JLabel infoLabel8;
    private static JLabel infoLabel9;

    private static JPanel mappingPanel;
    
    private static JPanel consolePanel;
    private static Console console;

    private void createAndShowGUI() {
        frame = new JFrame("Silver Surfer Command Center");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setBackground(new Color(221, 230, 231));
        JPanel directionPanel = directionPanel();
        JPanel otherPanel = otherPanel();
        JPanel infoPanel = infoPanel();
        JPanel mappingPanel = mappingPanel();
        JPanel consolePanel = consolePanel();

        GUIMenuBar bar = new GUIMenuBar(this);
        frame.setJMenuBar(bar);

        GroupLayout frameLayout = new GroupLayout(frame.getContentPane());
        frame.getContentPane().setLayout(frameLayout);
        frameLayout.setHorizontalGroup(frameLayout
                .createSequentialGroup()
                .addGroup(
                        frameLayout
                                .createParallelGroup(
                                        GroupLayout.Alignment.CENTER)
                                .addComponent(directionPanel)
                                .addComponent(otherPanel)
                                .addComponent(infoPanel))
                                .addGroup(
                                        frameLayout
                                                .createParallelGroup(
                                                        GroupLayout.Alignment.CENTER)
                                                .addComponent(mappingPanel)
                                                .addComponent(consolePanel)));
        frameLayout.setVerticalGroup(frameLayout
                .createParallelGroup(GroupLayout.Alignment.CENTER)
                .addGroup(
                        frameLayout.createSequentialGroup()
                                .addComponent(directionPanel)
                                .addComponent(otherPanel)
                                .addComponent(infoPanel))
                                .addGroup(
                                        frameLayout.createSequentialGroup()
                                                .addComponent(mappingPanel)
                                                .addComponent(consolePanel)));
        frameLayout.linkSize(SwingConstants.HORIZONTAL, directionPanel);
        frameLayout.linkSize(SwingConstants.VERTICAL, directionPanel);
        frameLayout.linkSize(SwingConstants.VERTICAL, consolePanel);

        frame.pack();
        frame.setSize(1000, 800);
        frame.setVisible(true);
        
        statusInfoBuffer = new StatusInfoBuffer();
        statusInfoBuffer.setSSG(this);
        communicator = new Communicator(statusInfoBuffer);
        System.out.println("[CONNECTION] Entered simulator mode.");

        getSimulationPanel().setSimulationPilot(
                communicator.getSimulationPilot());
        addListeners();
        barDecoder = new BarDecoder(this, communicator);

        changeSpeed(2);
        updateStatus();
    }

    public JFrame getFrame() {
        return frame;
    }

    public SimulationJPanel getSimulationPanel() {
        return simulationPanel;
    }

    private JPanel directionPanel() {
        JLabel angleLabel = new JLabel("Angle (degrees)", JLabel.CENTER);

        SpinnerNumberModel angleModel = new SpinnerNumberModel(90, 0, 1080, 1);
        angle = new JSpinner(angleModel);

        turnLeftButton = new JButton("Turn left");
        turnRightButton = new JButton("Turn right");

        JLabel lengthLabel = new JLabel("Length (centimeters)", JLabel.CENTER);

        SpinnerNumberModel lenghtModel = new SpinnerNumberModel(25, 0, 1000, 1);
        length = new JSpinner(lenghtModel);

        moveButton = new JButton("Move");

        JPanel directionPanel = new JPanel();
        directionPanel.setBorder(BorderFactory.createTitledBorder(
                createBorder(), "Turn and Move"));
        directionPanel.setOpaque(false);

        GroupLayout directionLayout = new GroupLayout(directionPanel);
        directionPanel.setLayout(directionLayout);
        directionLayout.setAutoCreateGaps(true);
        directionLayout.setAutoCreateContainerGaps(true);
        directionLayout.setHorizontalGroup(directionLayout
                .createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(angleLabel)
                .addComponent(angle)
                .addGroup(
                        directionLayout.createSequentialGroup()
                                .addComponent(turnLeftButton)
                                .addComponent(turnRightButton))
                .addComponent(lengthLabel).addComponent(length)
                .addComponent(moveButton));
        directionLayout.setVerticalGroup(directionLayout
                .createSequentialGroup()
                .addComponent(angleLabel)
                .addComponent(angle)
                .addGroup(
                        directionLayout
                                .createParallelGroup(
                                        GroupLayout.Alignment.CENTER)
                                .addComponent(turnLeftButton)
                                .addComponent(turnRightButton))
                .addComponent(lengthLabel).addComponent(length)
                .addComponent(moveButton));

        return directionPanel;
    }

    private JPanel otherPanel() {
        lookAroundButton = new JButton("Look Around");
        alignWhiteLineButton = new JButton("Align on white line");
        alignWall = new JButton("Align on walls");

        JPanel otherPanel = new JPanel();
        otherPanel.setBorder(BorderFactory.createTitledBorder(createBorder(),
                "Other"));
        otherPanel.setOpaque(false);

        GroupLayout otherLayout = new GroupLayout(otherPanel);
        otherPanel.setLayout(otherLayout);
        otherLayout.setAutoCreateGaps(true);
        otherLayout.setAutoCreateContainerGaps(true);
        otherLayout.setHorizontalGroup(otherLayout
                .createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(lookAroundButton).addComponent(alignWhiteLineButton).addComponent(alignWall));
        otherLayout.setVerticalGroup(otherLayout.createSequentialGroup()
        		.addComponent(lookAroundButton).addComponent(alignWhiteLineButton).addComponent(alignWall));

        return otherPanel;
    }
    
    private JPanel infoPanel() {
        infoLabel1 = new JLabel("", JLabel.CENTER);
        infoLabel2 = new JLabel("", JLabel.CENTER);
        infoLabel3 = new JLabel("", JLabel.CENTER);
        infoLabel4 = new JLabel("", JLabel.CENTER);
        infoLabel5 = new JLabel("", JLabel.CENTER);
        infoLabel6 = new JLabel("", JLabel.CENTER);
        infoLabel7 = new JLabel("", JLabel.CENTER);
        infoLabel8 = new JLabel("", JLabel.CENTER);
        infoLabel9 = new JLabel("", JLabel.CENTER);
        
        JPanel outputPanel = new JPanel();
        outputPanel.setBorder(BorderFactory.createTitledBorder(createBorder(),
                "Output"));
        outputPanel.setOpaque(false);
        
        GroupLayout outputLayout = new GroupLayout(outputPanel);
        outputPanel.setLayout(outputLayout);
        outputLayout.setAutoCreateGaps(true);
        outputLayout.setAutoCreateContainerGaps(true);
        outputLayout.setHorizontalGroup(outputLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
        		.addComponent(infoLabel1)
        		.addComponent(infoLabel2)
        		.addComponent(infoLabel3)
        		.addComponent(infoLabel4)
        		.addComponent(infoLabel5)
        		.addComponent(infoLabel6)
        		.addComponent(infoLabel7)
        		.addComponent(infoLabel8)
        		.addComponent(infoLabel9));
        outputLayout.setVerticalGroup(outputLayout.createSequentialGroup()
        		.addComponent(infoLabel1)
        		.addComponent(infoLabel2)
        		.addComponent(infoLabel3)
        		.addComponent(infoLabel4)
        		.addComponent(infoLabel5)
        		.addComponent(infoLabel6)
        		.addComponent(infoLabel7)
        		.addComponent(infoLabel8)
        		.addComponent(infoLabel9));
    	return outputPanel;
    }

    public void updateStatus() {
    	infoLabel1.setText("Bluetooth: " + communicator.getRobotConnected());
    	infoLabel3.setText("Ultrasonicsensor: "
                + getSimulationPanel().getSimulationPilot()
                .getUltraSensorValue());
    	infoLabel4.setText("Lightsensor: "
                + getSimulationPanel().getSimulationPilot()
                .getLightSensorValue());
    	infoLabel5.setText("Touchsensor 1: "
                + getSimulationPanel().getSimulationPilot()
                .getTouchSensor1Value());
    	infoLabel6.setText("Touchsensor 2: "
                + getSimulationPanel().getSimulationPilot()
                .getTouchSensor2Value());
        infoLabel7.setText("Left Motor: "
                + statusInfoBuffer.getLeftMotorMoving() + " "
                + statusInfoBuffer.getLeftMotorSpeed());
        infoLabel8.setText("Right Motor: "
                + statusInfoBuffer.getRightMotorMoving() + " "
                + statusInfoBuffer.getRightMotorSpeed());
        infoLabel9.setText("Buzy: " + statusInfoBuffer.getBusy());
    }

    private JPanel mappingPanel() {
        simulationPanel = new SimulationJPanel();
        simulationPanel.setSSG(this);
        simulationPanel.setSize(20000, 20000);
        simulationPanel.setBackground(Color.WHITE);
        simulationPanel.setBorder(createBorder());

        mappingPanel = new JPanel();
        mappingPanel.setBorder(BorderFactory.createTitledBorder(createBorder(),
                "Simulator"));
        mappingPanel.setOpaque(false);

        GroupLayout mappingLayout = new GroupLayout(mappingPanel);
        mappingPanel.setLayout(mappingLayout);
        mappingLayout.setAutoCreateGaps(true);
        mappingLayout.setAutoCreateContainerGaps(true);
        mappingLayout.setHorizontalGroup(mappingLayout.createSequentialGroup()
                .addComponent(simulationPanel));
        mappingLayout.setVerticalGroup(mappingLayout.createSequentialGroup()
                .addComponent(simulationPanel));

        return mappingPanel;
    }

    public void updateCoordinates(String s) {
        mappingPanel.setBorder(BorderFactory.createTitledBorder(createBorder(),
                s));
    }

    private JPanel consolePanel() {
        console = new Console();
        JScrollPane scrollPane = new JScrollPane(console);
        console.setScroll(scrollPane);

        consolePanel = new JPanel();
        consolePanel.setOpaque(false);

        GroupLayout consolelayout = new GroupLayout(consolePanel);
        consolePanel.setLayout(consolelayout);
        consolelayout.setAutoCreateGaps(true);
        consolelayout.setAutoCreateContainerGaps(true);
        consolelayout.setHorizontalGroup(consolelayout.createSequentialGroup()
                .addComponent(scrollPane));
        consolelayout.setVerticalGroup(consolelayout.createSequentialGroup()
                .addComponent(scrollPane));

        return consolePanel;
    }

    protected static void connectBluetooth() {
        try {
            communicator.setRobotConnected(true);
            simulationPanel.resetMap();
            communicator.getSimulationPilot().setRealRobot(true);
            System.out.println("[CONNECTION] Connection established.");
            changeSpeed(2);
        } catch (Exception e) {
            System.out
                    .println("[CONNECTION] Oops! Something went wrong connecting! \n[CONNECTION] Please make sure your robot and bluetooth are turned on.");
        }
    }

    protected static void disconnectBluetooth() {
        try {
            communicator.setRobotConnected(false);
            simulationPanel.resetMap();
            communicator.getSimulationPilot().setRealRobot(true);
            System.out
                    .println("[CONNECTION] Connection succesfully closed. Entered simulator mode.");
            changeSpeed(2);
        } catch (Exception e) {
            System.out
                    .println("[CONNECTION] Oops! Something went wrong disconnecting!");
        }
    }
    
    protected static void clearScreen() {
        System.out.println("[GUI] Screen cleared.");
        simulationPanel.clear();
    }

    public static void changeSpeed(int value) {
        communicator.setSpeed(value);
    	infoLabel2.setText("Speed level: " + value);
        System.out.println(communicator.getConsoleTag()
                + " Current Speed Level: " + value + ".");
    }

    private void addListeners() {
        turnLeftButton.addMouseListener(new MouseListener() {
            @Override
            public void mouseReleased(MouseEvent arg0) {
            }

            @Override
            public void mousePressed(MouseEvent arg0) {
            }

            @Override
            public void mouseExited(MouseEvent arg0) {
            }

            @Override
            public void mouseEntered(MouseEvent arg0) {
            }

            @Override
            public void mouseClicked(MouseEvent arg0) {
                MoveTurnThread MTT = new MoveTurnThread("MTT");
                MTT.setCommunicator(communicator);
                MTT.setLength(0);
                MTT.setAngles(-1
                        * Integer.parseInt(angle.getValue().toString()));
                MTT.setAmtOfAngles(0);
                MTT.start();
            }
        });
        turnRightButton.addMouseListener(new MouseListener() {
            @Override
            public void mouseReleased(MouseEvent arg0) {
            }

            @Override
            public void mousePressed(MouseEvent arg0) {
            }

            @Override
            public void mouseExited(MouseEvent arg0) {
            }

            @Override
            public void mouseEntered(MouseEvent arg0) {
            }

            @Override
            public void mouseClicked(MouseEvent arg0) {
                MoveTurnThread MTT = new MoveTurnThread("MTT");
                MTT.setCommunicator(communicator);
                MTT.setLength(0);
                MTT.setAngles(Integer.parseInt(angle.getValue().toString()));
                MTT.setAmtOfAngles(0);
                MTT.start();
            }
        });
        moveButton.addMouseListener(new MouseListener() {
            @Override
            public void mouseReleased(MouseEvent arg0) {
            }

            @Override
            public void mousePressed(MouseEvent arg0) {
            }

            @Override
            public void mouseExited(MouseEvent arg0) {
            }

            @Override
            public void mouseEntered(MouseEvent arg0) {
            }

            @Override
            public void mouseClicked(MouseEvent arg0) {
                MoveTurnThread MTT = new MoveTurnThread("MTT");
                MTT.setCommunicator(communicator);
                MTT.setLength(Integer.parseInt(length.getValue().toString()));
                MTT.setAngles(0);
                MTT.setAmtOfAngles(0);
                MTT.start();
            }
        });
        lookAroundButton.addMouseListener(new MouseListener() {
            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println(communicator.getConsoleTag()
                        + " Looking around for obstructions.");
                MoveTurnThread MTT = new MoveTurnThread("MTT");
                MTT.setCommunicator(communicator);
                MTT.setLength(0);
                MTT.setAngles(0);
                MTT.setAmtOfAngles(0);
                MTT.setCommand(Command.LOOK_AROUND);
                MTT.start();
            }
        });
        alignWhiteLineButton.addMouseListener(new MouseListener() {
            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println(communicator.getConsoleTag()
                        + " Aligning on white line.");
                MoveTurnThread MTT = new MoveTurnThread("MTT");
                MTT.setCommunicator(communicator);
                MTT.setLength(0);
                MTT.setAngles(0);
                MTT.setAmtOfAngles(0);
                MTT.setCommand(Command.ALIGN_PERPENDICULAR);
                MTT.start();
            }
        });
        alignWall.addMouseListener(new MouseListener() {
            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println(communicator.getConsoleTag()
                        + " Aligning on walls.");
                MoveTurnThread MTT = new MoveTurnThread("MTT");
                MTT.setCommunicator(communicator);
                MTT.setLength(0);
                MTT.setAngles(0);
                MTT.setAmtOfAngles(0);
                MTT.setCommand(Command.ALIGN_WALL);
                MTT.start();
            }
        });
        
    }

    private static javax.swing.border.Border createBorder() {
        return BorderFactory.createEtchedBorder(1);
    }

    public Communicator getCommunicator() {
        return communicator;
    }

    public static StatusInfoBuffer getInformationBuffer() {
        return statusInfoBuffer;
    }

    public void executeBarcode() {
        barDecoder.decode(statusInfoBuffer.getBarcode());
    }

    public static void main(String[] args) {
        SilverSurferGUI SSG = new SilverSurferGUI();
        SSG.createAndShowGUI();
    }
}