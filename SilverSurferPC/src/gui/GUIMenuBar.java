package gui;

import java.awt.Color;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import communication.StatusInfoBuffer;
import communication.StatusInfoBuffer.LSInfoNode;
import communication.StatusInfoBuffer.USInfoNode;

//Creates the menubar.
public class GUIMenuBar extends JMenuBar {

    private SilverSurferGUI gui;

    private JMenu fileMenu;
    private JMenu blueToothMenu;
    private JMenu screenMenu;
    private JMenu robotMenu;
    private JMenu mapMenu;

    public GUIMenuBar(final SilverSurferGUI gui) {
        setGui(gui);
        this.add(getFileMenu());
        this.add(getBlueToothMenu());
        this.add(getScreenMenu());
        this.add(getRobotMenu());
        this.add(getMapMenu());
        setBackground(new Color(221, 230, 231));
    }

    private JMenu getBlueToothMenu() {

        blueToothMenu = new JMenu("Bluetooth");
        // blueToothMenu.setMnemonic('B');

        final JMenuItem connectItem = new JMenuItem("Connect...");
        // connectItem.setMnemonic('C');
        blueToothMenu.add(connectItem);

        final JMenuItem disconnectItem = new JMenuItem("Disconnect...");
        // disconnectItem.setMnemonic('D');
        blueToothMenu.add(disconnectItem);

        connectItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent arg0) {
                SilverSurferGUI.connectBluetooth();
            }
        });

        disconnectItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                SilverSurferGUI.disconnectBluetooth();
            }
        });

        return blueToothMenu;

    }

    private JMenu getFileMenu() {

        fileMenu = new JMenu("File");
        // fileMenu.setMnemonic('F');

        final JMenuItem exportLSItem = new JMenuItem("Export Lightsensor data");
        // exportLSItem.setMnemonic('L');
        fileMenu.add(exportLSItem);

        exportLSItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent arg0) {
                // Prompt for a File
                final FileDialog prompt = new FileDialog(GUIMenuBar.this
                        .getGui().getFrame(), "Select file:", FileDialog.SAVE);
                prompt.setFilenameFilter(new FilenameFilter() {

                    @Override
                    public boolean accept(final File dir, final String name) {
                        return name.endsWith(".txt");
                    }
                });

                // Display the dialog and wait for the user's response
                prompt.setVisible(true);

                final File export = new File(prompt.getDirectory()
                        + prompt.getFile());
                try {
                    // Create file
                    export.createNewFile();

                    // Get buffer
                    final StatusInfoBuffer buffer = SilverSurferGUI
                            .getStatusInfoBuffer();
                    LSInfoNode head = buffer.getStartLSInfo();
                    // Create data output flow
                    final FileWriter outFile = new FileWriter(export);
                    final PrintWriter out = new PrintWriter(outFile);
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

                } catch (final IOException e) {
                    System.out
                            .println("Sorry, something went wrong exporting your data.");
                }

            }
        });

        final JMenuItem exportUSItem = new JMenuItem(
                "Export Ultrasonicsensor data");
        // exportUSItem.setMnemonic('U');
        fileMenu.add(exportUSItem);

        exportUSItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent arg0) {
                // Prompt for a File
                final FileDialog prompt = new FileDialog(GUIMenuBar.this
                        .getGui().getFrame(), "Select file:", FileDialog.SAVE);
                prompt.setFilenameFilter(new FilenameFilter() {

                    @Override
                    public boolean accept(final File dir, final String name) {
                        return name.endsWith(".txt");
                    }
                });

                // Display the dialog and wait for the user's response
                prompt.setVisible(true);

                final File export = new File(prompt.getDirectory()
                        + prompt.getFile());
                try {
                    // Create file
                    export.createNewFile();

                    // Get buffer
                    final StatusInfoBuffer buffer = SilverSurferGUI
                            .getStatusInfoBuffer();
                    USInfoNode head = buffer.getStartUSInfo();
                    // Create data output flow
                    final FileWriter outFile = new FileWriter(export);
                    final PrintWriter out = new PrintWriter(outFile);
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

                } catch (final IOException e) {
                    System.out
                            .println("Sorry, something went wrong exporting your data.");
                }

            }
        });

        return fileMenu;
    }

    public SilverSurferGUI getGui() {
        return gui;
    }

    private JMenu getMapMenu() {

        mapMenu = new JMenu("Map");
        // mapMenu.setMnemonic('M');

        final JMenuItem loadMapItem = new JMenuItem("Load map...");
        // loadMapItem.setMnemonic('M');
        mapMenu.add(loadMapItem);

        loadMapItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                // reset the current map.
                gui.getSimulationPanel().resetMap();

                // Prompt for a File
                final FileDialog prompt = new FileDialog(GUIMenuBar.this
                        .getGui().getFrame(), "Select maze:", FileDialog.LOAD);
                prompt.setDirectory("resources/maze_maps");

                // Display the dialog and wait for the user's response
                prompt.setVisible(true);

                final File mapFile = new File(prompt.getDirectory()
                        + prompt.getFile()); // Load and display selection
                prompt.dispose(); // Get rid of the dialog box

                gui.getCommunicator().getPilot().setMapFile(mapFile, 0, 0);

                System.out.println("[I/O] Map succesfully loaded!");

            }

        });

        final JMenuItem exploreItem = new JMenuItem("Explore...");
        // exploreItem.setMnemonic('E');
        mapMenu.add(exploreItem);

        exploreItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                /* TOON mazexplorer moet een pilot hebben */
                // MazeExplorer exp = new MazeExplorer(gui);
                // ExploreThread explorer = new ExploreThread(exp);
                // gui.getCommunicator().setExplorer(exp);
                // explorer.start();
            }
        });

        return mapMenu;

    }

    private JMenu getRobotMenu() {

        robotMenu = new JMenu("Robot");
        // speedMenu.setMnemonic('S');

        final JMenuItem slowSpeedItem = new JMenuItem("Slow Speed");
        // slowSpeedItem.setMnemonic('S');
        robotMenu.add(slowSpeedItem);

        final JMenuItem normalSpeedItem = new JMenuItem("Normal Speed");
        // normalSpeedItem.setMnemonic('N');
        robotMenu.add(normalSpeedItem);

        final JMenuItem fastSpeedItem = new JMenuItem("Fast Speed");
        // fastSpeedItem.setMnemonic('F');
        robotMenu.add(fastSpeedItem);

        final JMenuItem veryFastSpeedItem = new JMenuItem("Very Fast Speed");
        // veryFastSpeedItem.setMnemonic('V');
        robotMenu.add(veryFastSpeedItem);

        slowSpeedItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent arg0) {
                SilverSurferGUI.changeSpeed(1);
            }
        });
        normalSpeedItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent arg0) {
                SilverSurferGUI.changeSpeed(2);
            }
        });
        fastSpeedItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent arg0) {
                SilverSurferGUI.changeSpeed(3);
            }
        });

        veryFastSpeedItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent arg0) {
                SilverSurferGUI.changeSpeed(4);
            }
        });

        return robotMenu;

    }

    private JMenu getScreenMenu() {

        screenMenu = new JMenu("Screen");
        // clearScreenMenu.setMnemonic('C');

        final JMenuItem zoomInItem = new JMenuItem("Zoom In");
        screenMenu.add(zoomInItem);

        zoomInItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent arg0) {
                gui.zoomIn();
            }
        });

        final JMenuItem zoomOutItem = new JMenuItem("Zoom Out");
        screenMenu.add(zoomOutItem);

        zoomOutItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent arg0) {
                gui.zoomOut();
            }
        });

        final JMenuItem clearScreanItem = new JMenuItem("Clear Screen");
        screenMenu.add(clearScreanItem);

        clearScreanItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent arg0) {
                gui.getSimulationPanel().resetMap();
                SilverSurferGUI.clearScreen();
            }
        });

        final JMenuItem removeWallsItem = new JMenuItem("Remove Walls");
        screenMenu.add(removeWallsItem);

        removeWallsItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent arg0) {
                gui.getSimulationPanel().removeWalls();
            }
        });

        final JMenuItem removeBarcodesItem = new JMenuItem("Remove Barcodes");
        screenMenu.add(removeBarcodesItem);

        removeBarcodesItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent arg0) {
                gui.getSimulationPanel().removeBarCodes();
            }
        });

        final JMenuItem removePathItem = new JMenuItem("Clear Path");
        screenMenu.add(removePathItem);

        removePathItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent arg0) {
                // TODO clearpath in simulatorpanel
                // gui.getSimulationPanel().clearPath();
            }
        });
        return screenMenu;

    }

    public void setGui(final SilverSurferGUI gui) {
        this.gui = gui;
    }
}