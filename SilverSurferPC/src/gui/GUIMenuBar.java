package gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.swing.*;

import mapping.MapGraph;
import mazeAlgorithm.ExploreThread;
import mazeAlgorithm.MazeExplorer;

import communication.*;
import communication.StatusInfoBuffer.*;

//Creates the menubar.
public class GUIMenuBar extends JMenuBar {

    private SilverSurferGUI gui;

    private JMenu fileMenu;
    private JMenu blueToothMenu;
    private JMenu screenMenu;
    private JMenu robotMenu;
    private JMenu mapMenu;

    public GUIMenuBar(SilverSurferGUI gui) {
        setGui(gui);
        this.add(getFileMenu());
        this.add(getBlueToothMenu());
        this.add(getScreenMenu());
        this.add(getRobotMenu());
        this.add(getMapMenu());
        setBackground(new Color(221, 230, 231));
    }

    private JMenu getFileMenu() {

        fileMenu = new JMenu("File");
        // fileMenu.setMnemonic('F');

        JMenuItem exportLSItem = new JMenuItem("Export Lightsensor data");
        // exportLSItem.setMnemonic('L');
        fileMenu.add(exportLSItem);

        exportLSItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                // Prompt for a File
                FileDialog prompt = new FileDialog(GUIMenuBar.this.getGui()
                        .getFrame(), "Select file:", FileDialog.SAVE);
                prompt.setFilenameFilter(new FilenameFilter() {

                    @Override
                    public boolean accept(File dir, String name) {
                        return name.endsWith(".txt");
                    }
                });

                // Display the dialog and wait for the user's response
                prompt.show();

                File export = new File(prompt.getDirectory() + prompt.getFile());
                try {
                    // Create file
                    export.createNewFile();

                    // Get buffer
                    StatusInfoBuffer buffer = SilverSurferGUI
                            .getStatusInfoBuffer();
                    LSInfoNode head = buffer.getStartLSInfo();
                    // Create data output flow
                    FileWriter outFile = new FileWriter(export);
                    PrintWriter out = new PrintWriter(outFile);
                    buffer.claimBuffer();
                    // Print buffer
                    if (head != null) {
                        do {
                            out.println(head.info);
                            head = head.next;
                        } while (head != null);
                    }
                    // free buffer and close stream.
                    buffer.freeBuffer();
                    out.close();

                } catch (IOException e) {
                    System.out
                            .println("Sorry, something went wrong exporting your data.");
                }

            }
        });

        JMenuItem exportUSItem = new JMenuItem("Export Ultrasonicsensor data");
        // exportUSItem.setMnemonic('U');
        fileMenu.add(exportUSItem);

        exportUSItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                // Prompt for a File
                FileDialog prompt = new FileDialog(GUIMenuBar.this.getGui()
                        .getFrame(), "Select file:", FileDialog.SAVE);
                prompt.setFilenameFilter(new FilenameFilter() {

                    @Override
                    public boolean accept(File dir, String name) {
                        return name.endsWith(".txt");
                    }
                });

                // Display the dialog and wait for the user's response
                prompt.show();

                File export = new File(prompt.getDirectory() + prompt.getFile());
                try {
                    // Create file
                    export.createNewFile();

                    // Get buffer
                    StatusInfoBuffer buffer = SilverSurferGUI
                            .getStatusInfoBuffer();
                    USInfoNode head = buffer.getStartUSInfo();
                    // Create data output flow
                    FileWriter outFile = new FileWriter(export);
                    PrintWriter out = new PrintWriter(outFile);
                    buffer.claimBuffer();
                    // Print buffer
                    if (head != null) {
                        do {
                            System.out.println(head.info);
                            out.println(head.info);
                            head = head.next;
                        } while (head != null);
                    }
                    // free buffer and close stream.
                    buffer.freeBuffer();
                    out.close();

                } catch (IOException e) {
                    System.out
                            .println("Sorry, something went wrong exporting your data.");
                }

            }
        });

        return fileMenu;
    }

    private JMenu getBlueToothMenu() {

        blueToothMenu = new JMenu("Bluetooth");
        // blueToothMenu.setMnemonic('B');

        JMenuItem connectItem = new JMenuItem("Connect...");
        // connectItem.setMnemonic('C');
        blueToothMenu.add(connectItem);

        JMenuItem disconnectItem = new JMenuItem("Disconnect...");
        // disconnectItem.setMnemonic('D');
        blueToothMenu.add(disconnectItem);

        connectItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                SilverSurferGUI.connectBluetooth();
            }
        });

        disconnectItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                SilverSurferGUI.disconnectBluetooth();
            }
        });

        return blueToothMenu;

    }

    private JMenu getScreenMenu() {

        screenMenu = new JMenu("Screen");
        // clearScreenMenu.setMnemonic('C');

        JMenuItem zoomInItem = new JMenuItem("Zoom In");
        screenMenu.add(zoomInItem);

        zoomInItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                gui.zoomIn();
            }
        });

        JMenuItem zoomOutItem = new JMenuItem("Zoom Out");
        screenMenu.add(zoomOutItem);

        zoomOutItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                gui.zoomOut();
            }
        });

        JMenuItem clearScreanItem = new JMenuItem("Clear Screen");
        screenMenu.add(clearScreanItem);

        clearScreanItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                gui.getSimulationPanel().resetMap();
                SilverSurferGUI.clearScreen();
            }
        });

        JMenuItem removeWallsItem = new JMenuItem("Remove Walls");
        screenMenu.add(removeWallsItem);

        removeWallsItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                gui.getSimulationPanel().removeWalls();
            }
        });

        JMenuItem removeBarcodesItem = new JMenuItem("Remove Barcodes");
        screenMenu.add(removeBarcodesItem);

        removeBarcodesItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                gui.getSimulationPanel().removeBarCodes();
            }
        });

        JMenuItem removePathItem = new JMenuItem("Clear Path");
        screenMenu.add(removePathItem);

        removePathItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                // TODO clearpath in simulatorpanel
                // gui.getSimulationPanel().clearPath();
            }
        });
        return screenMenu;

    }

    private JMenu getRobotMenu() {

        robotMenu = new JMenu("Robot");
        // speedMenu.setMnemonic('S');

        JMenuItem slowSpeedItem = new JMenuItem("Slow Speed");
        // slowSpeedItem.setMnemonic('S');
        robotMenu.add(slowSpeedItem);

        JMenuItem normalSpeedItem = new JMenuItem("Normal Speed");
        // normalSpeedItem.setMnemonic('N');
        robotMenu.add(normalSpeedItem);

        JMenuItem fastSpeedItem = new JMenuItem("Fast Speed");
        // fastSpeedItem.setMnemonic('F');
        robotMenu.add(fastSpeedItem);

        JMenuItem veryFastSpeedItem = new JMenuItem("Very Fast Speed");
        // veryFastSpeedItem.setMnemonic('V');
        robotMenu.add(veryFastSpeedItem);

        slowSpeedItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                SilverSurferGUI.changeSpeed(1);
            }
        });
        normalSpeedItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                SilverSurferGUI.changeSpeed(2);
            }
        });
        fastSpeedItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                SilverSurferGUI.changeSpeed(3);
            }
        });

        veryFastSpeedItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                SilverSurferGUI.changeSpeed(4);
            }
        });

        return robotMenu;

    }

    private JMenu getMapMenu() {

        mapMenu = new JMenu("Map");
        // mapMenu.setMnemonic('M');

        JMenuItem loadMapItem = new JMenuItem("Load map...");
        // loadMapItem.setMnemonic('M');
        mapMenu.add(loadMapItem);

        loadMapItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // reset the current map.
                gui.getSimulationPanel().resetMap();

                // Prompt for a File
                FileDialog prompt = new FileDialog(GUIMenuBar.this.getGui()
                        .getFrame(), "Select maze:", FileDialog.LOAD);
                prompt.setDirectory("resources/maze_maps");

                // Display the dialog and wait for the user's response
                prompt.show();

                File mapFile = new File(prompt.getDirectory()
                        + prompt.getFile()); // Load and display selection
                prompt.dispose(); // Get rid of the dialog box

                gui.getCommunicator().getSimulationPilot()
                        .setMapFile(mapFile, 0, 0);

                System.out.println("[I/O] Map succesfully loaded!");

            }

        });

        JMenuItem exploreItem = new JMenuItem("Explore...");
        // exploreItem.setMnemonic('E');
        mapMenu.add(exploreItem);

        exploreItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                MazeExplorer exp = new MazeExplorer(gui);
                ExploreThread explorer = new ExploreThread(exp);
                gui.getCommunicator().setExplorer(exp);
                explorer.start();
            }
        });

        return mapMenu;

    }

    public void setGui(SilverSurferGUI gui) {
        this.gui = gui;
    }

    public SilverSurferGUI getGui() {
        return gui;
    }
}