package gui;

import java.awt.Color;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import simulator.pilot.RobotPilot;

@SuppressWarnings("serial")
public class GUIMenuBar extends JMenuBar {

    private SilverSurferGUI gui;
    private JFrame frame;
    private JMenu screenMenu, speedMenu, inputMenu, connectionMenu, mapMenu, exploreMenu;

    public GUIMenuBar(final SilverSurferGUI gui, final JFrame frame) {
        this.gui = gui;
        this.frame = frame;
        this.add(getScreenMenu());
        this.add(getSpeedMenu());
        this.add(createEmptyMenu());
        this.add(getInputMenu());
        this.add(createEmptyMenu());
        this.add(getConnectionMenu());
        this.add(getMapMenu());
        this.add(getExploreMenu());
        setBackground(new Color(221, 230, 231));
    }

    private JMenu createEmptyMenu() {
        JMenu emptyMenu = new JMenu("     ");
        emptyMenu.setEnabled(false);
        return emptyMenu;
    }

    private JMenu getScreenMenu() {
        screenMenu = new JMenu("Screen");

        final JMenuItem all = new JMenuItem("Toggle all");
        screenMenu.add(all);
        all.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent arg0) {
                gui.toggleAll();
            }
        });
        
        screenMenu.addSeparator();

        final JMenuItem teamPilots = new JMenuItem("Toggle team pilots");
        screenMenu.add(teamPilots);
        teamPilots.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent arg0) {
                SilverSurferGUI.getSimulatorPanel().toggleTeamPilots();
            }
        });

        final JMenuItem overallMap = new JMenuItem("Toggle overall map");
        screenMenu.add(overallMap);
        overallMap.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent arg0) {
                SilverSurferGUI.getSimulatorPanel().toggleOverallMap();
            }
        });

        final JMenuItem sidePanel = new JMenuItem("Toggle sidepanel");
        screenMenu.add(sidePanel);
        sidePanel.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent arg0) {
                gui.toggleSidePanel();
            }
        });

        final JMenuItem sensorPanel = new JMenuItem("Toggle sensorpanel");
        screenMenu.add(sensorPanel);
        sensorPanel.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent arg0) {
                gui.toggleSensorPanel();
            }
        });
        
        screenMenu.addSeparator();

        final JMenuItem pauseSensor = new JMenuItem("Pause/Resume sensorpanel");
        screenMenu.add(pauseSensor);
        pauseSensor.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent arg0) {
                gui.pauseSensorPanel();
            }
        });

        return screenMenu;
    }

    private JMenu getSpeedMenu() {
        speedMenu = new JMenu("Speed");

        final JMenuItem slowSpeedItem = new JMenuItem("Slow Speed");
        speedMenu.add(slowSpeedItem);
        slowSpeedItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent arg0) {
                SilverSurferGUI.getSimulatorPanel().changeSpeedMainRobot(1);
            }
        });

        final JMenuItem normalSpeedItem = new JMenuItem("Normal Speed");
        speedMenu.add(normalSpeedItem);
        normalSpeedItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent arg0) {
                SilverSurferGUI.getSimulatorPanel().changeSpeedMainRobot(2);
            }
        });

        final JMenuItem fastSpeedItem = new JMenuItem("Fast Speed");
        speedMenu.add(fastSpeedItem);
        fastSpeedItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent arg0) {
                SilverSurferGUI.getSimulatorPanel().changeSpeedMainRobot(3);
            }
        });

        final JMenuItem veryFastSpeedItem = new JMenuItem("Very Fast Speed");
        speedMenu.add(veryFastSpeedItem);
        veryFastSpeedItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent arg0) {
                SilverSurferGUI.getSimulatorPanel().changeSpeedMainRobot(4);
            }
        });
        
        speedMenu.addSeparator();

        final JMenuItem slowSpeedAllItem = new JMenuItem("Slow Speed (all)");
        speedMenu.add(slowSpeedAllItem);
        slowSpeedAllItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent arg0) {
                SilverSurferGUI.getSimulatorPanel().changeSpeed(1);
            }
        });

        final JMenuItem normalSpeedAllItem = new JMenuItem("Normal Speed (all)");
        speedMenu.add(normalSpeedAllItem);
        normalSpeedAllItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent arg0) {
                SilverSurferGUI.getSimulatorPanel().changeSpeed(2);
            }
        });

        final JMenuItem fastSpeedAllItem = new JMenuItem("Fast Speed (all)");
        speedMenu.add(fastSpeedAllItem);
        fastSpeedAllItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent arg0) {
                SilverSurferGUI.getSimulatorPanel().changeSpeed(3);
            }
        });

        final JMenuItem veryFastSpeedAllItem = new JMenuItem(
                "Very Fast Speed (all)");
        speedMenu.add(veryFastSpeedAllItem);
        veryFastSpeedAllItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent arg0) {
                SilverSurferGUI.getSimulatorPanel().changeSpeed(4);
            }
        });
        
        speedMenu.addSeparator();

        final JMenuItem slowMotionItem = new JMenuItem("Slow Motion");
        speedMenu.add(slowMotionItem);
        slowMotionItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent arg0) {
                SilverSurferGUI.getSimulatorPanel().changeSpeed(-1);
            }
        });

        final JMenuItem fastForwardItem = new JMenuItem("Fast Forward");
        speedMenu.add(fastForwardItem);
        fastForwardItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent arg0) {
                SilverSurferGUI.getSimulatorPanel().changeSpeed(5);
            }
        });

        return speedMenu;
    }

    private JMenu getInputMenu() {
        inputMenu = new JMenu("Input");

        final JMenuItem turnDifferentAmountItem = new JMenuItem("Turn ...");
        inputMenu.add(turnDifferentAmountItem);
        turnDifferentAmountItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                JTextField inputField = new JTextField(5);
                inputField.setText("90");
        		JPanel inputPanel = new JPanel();
        		inputPanel.add(new JLabel("Angle:"));
        		inputPanel.add(inputField);
        		
        		int answer = JOptionPane.showOptionDialog(frame, inputPanel, "Enter amount.", JOptionPane.DEFAULT_OPTION, JOptionPane.DEFAULT_OPTION, null, null, null);
        		if(answer != -1)
        			try {
            			SilverSurferGUI.getSimulatorPanel().turnRightPrincipalPilot(Integer.parseInt(inputField.getText()));
        			} catch(Exception ex) {
        				
        			}
            }
        });

        final JMenuItem moveDifferentAmountItem = new JMenuItem("Move ...");
        inputMenu.add(moveDifferentAmountItem);
        moveDifferentAmountItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                JTextField inputField = new JTextField(5);
                inputField.setText("40");
        		JPanel inputPanel = new JPanel();
        		inputPanel.add(new JLabel("Distance:"));
        		inputPanel.add(inputField);
        		
        		int answer = JOptionPane.showOptionDialog(frame, inputPanel, "Enter amount.", JOptionPane.DEFAULT_OPTION, JOptionPane.DEFAULT_OPTION, null, null, null);
        		if(answer != -1)
        			try {
        				SilverSurferGUI.getSimulatorPanel().travelPrincipalPilot(Integer.parseInt(inputField.getText()));
        			} catch(Exception ex) {
        				
        			}
            }
        });
        
        return inputMenu;
    }

    private JMenu getConnectionMenu() {
        connectionMenu = new JMenu("Connection");

        final JMenuItem connectItem = new JMenuItem("Connect ...");
        connectionMenu.add(connectItem);
        connectItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent arg0) {
                SilverSurferGUI.getSimulatorPanel().connect();
                System.out.println("[CONNECTION] Connection established.");
            }
        });

        final JMenuItem disconnectItem = new JMenuItem("Disconnect ...");
        connectionMenu.add(disconnectItem);
        disconnectItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                SilverSurferGUI.getSimulatorPanel().disconnect();
                System.out
                        .println("[CONNECTION] Connection succesfully closed. Entered simulator mode.");
            }
        });
        
        connectionMenu.addSeparator();

        final JMenuItem readyItem = new JMenuItem("Robot ready");
        connectionMenu.add(readyItem);
        readyItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
            	if(SilverSurferGUI.getSimulatorPanel().getPrincipalPilot() instanceof RobotPilot)
            		((RobotPilot)(SilverSurferGUI.getSimulatorPanel().getPrincipalPilot())).setReady();
            }
        });

        return connectionMenu;
    }

    private JMenu getMapMenu() {
        mapMenu = new JMenu("Map");

        final JMenuItem loadMapOnePlayerItem = new JMenuItem("Load map (1 player)");
        mapMenu.add(loadMapOnePlayerItem);
        loadMapOnePlayerItem.addActionListener(new ActionListener() {
        	
            @Override
            public void actionPerformed(final ActionEvent e) {
                final FileDialog prompt = new FileDialog(frame, "Select maze:",
                        FileDialog.LOAD);
                prompt.setDirectory("resources/maze_maps");
                prompt.setVisible(true);

                final File mapFile = new File(prompt.getDirectory()
                        + prompt.getFile()); // Load and display selection
                prompt.dispose();

                if (mapFile.exists()) {
                    System.out.println("[MAP] Loading map ...");
                    SilverSurferGUI.getSimulatorPanel().setMapFile(mapFile, 1);
                    System.out.println("[MAP] Map loaded!");
                }
            }
        });

        final JMenuItem loadMapTwoPlayersItem = new JMenuItem(
                "Load map (2 players)");
        mapMenu.add(loadMapTwoPlayersItem);
        loadMapTwoPlayersItem.addActionListener(new ActionListener() {
        	
            @Override
            public void actionPerformed(final ActionEvent e) {
                final FileDialog prompt = new FileDialog(frame, "Select maze:",
                        FileDialog.LOAD);
                prompt.setDirectory("resources/maze_maps");
                prompt.setVisible(true);

                final File mapFile = new File(prompt.getDirectory()
                        + prompt.getFile()); // Load and display selection
                prompt.dispose();

                if (mapFile.exists()) {
                    System.out.println("[MAP] Loading map ...");
                    SilverSurferGUI.getSimulatorPanel().setMapFile(mapFile, 2);
                    System.out.println("[MAP] Map loaded!");
                }
            }
        });

        final JMenuItem loadMapThreePlayersItem = new JMenuItem(
                "Load map (3 players)");
        mapMenu.add(loadMapThreePlayersItem);
        loadMapThreePlayersItem.addActionListener(new ActionListener() {
        	
            @Override
            public void actionPerformed(final ActionEvent e) {
                final FileDialog prompt = new FileDialog(frame, "Select maze:",
                        FileDialog.LOAD);
                prompt.setDirectory("resources/maze_maps");
                prompt.setVisible(true);

                final File mapFile = new File(prompt.getDirectory()
                        + prompt.getFile()); // Load and display selection
                prompt.dispose();

                if (mapFile.exists()) {
                    System.out.println("[MAP] Loading map ...");
                    SilverSurferGUI.getSimulatorPanel().setMapFile(mapFile, 3);
                    System.out.println("[MAP] Map loaded!");
                }
            }
        });

        final JMenuItem loadMapFourPlayersItem = new JMenuItem(
                "Load map (4 players)");
        mapMenu.add(loadMapFourPlayersItem);
        loadMapFourPlayersItem.addActionListener(new ActionListener() {
        	
            @Override
            public void actionPerformed(final ActionEvent e) {
                final FileDialog prompt = new FileDialog(frame, "Select maze:",
                        FileDialog.LOAD);
                prompt.setDirectory("resources/maze_maps");
                prompt.setVisible(true);

                final File mapFile = new File(prompt.getDirectory()
                        + prompt.getFile()); // Load and display selection
                prompt.dispose();

                if (mapFile.exists()) {
                    System.out.println("[MAP] Loading map ...");
                    SilverSurferGUI.getSimulatorPanel().setMapFile(mapFile, 4);
                    System.out.println("[MAP] Map loaded!");
                }
            }
        });
        
        mapMenu.addSeparator();

        final JMenuItem removeMapItem = new JMenuItem("Remove map");
        mapMenu.add(removeMapItem);
        removeMapItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent arg0) {
                SilverSurferGUI.getSimulatorPanel().removeMapFile();
                System.out.println("[MAP] Map removed!");
            }
        });

        return mapMenu;
    }

    private JMenu getExploreMenu() {
        exploreMenu = new JMenu("Explore");

        final JMenuItem exploreItem = new JMenuItem("Explore map");
        exploreMenu.add(exploreItem);
        exploreItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                SilverSurferGUI.getSimulatorPanel().startSimulation();
                System.out.println("[EXPLORE] Start exploration.");
            }
        });

        final JMenuItem playGameItem = new JMenuItem("Play Game");
        exploreMenu.add(playGameItem);
        playGameItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                SilverSurferGUI.getSimulatorPanel().playGame();
                System.out.println("[EXPLORE] Started playing Treasure Trek.");
            }
        });
        
        exploreMenu.addSeparator();

        final JMenuItem resetRobotsItem = new JMenuItem("Stop and reset");
        exploreMenu.add(resetRobotsItem);
        resetRobotsItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent arg0) {
                SilverSurferGUI.getSimulatorPanel().resetRobots();
                System.out
                        .println("[EXPLORE] Exploration stopped and robots resetted (might have to do multiple times).");
            }
        });

        return exploreMenu;
    }
}