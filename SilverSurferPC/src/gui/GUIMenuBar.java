package gui;

import java.awt.Color;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

@SuppressWarnings("serial")
public class GUIMenuBar extends JMenuBar {

    private SilverSurferGUI gui;

    private JMenu blueToothMenu, screenMenu, speedMenu, mapMenu;

    public GUIMenuBar(final SilverSurferGUI gui) {
        this.gui = gui;
        this.add(getBlueToothMenu());
        this.add(getScreenMenu());
        this.add(getSpeedMenu());
        this.add(getMapMenu());
        setBackground(new Color(221, 230, 231));
    }

    private JMenu getBlueToothMenu() {
        blueToothMenu = new JMenu("Bluetooth");

        final JMenuItem connectItem = new JMenuItem("Connect...");
        blueToothMenu.add(connectItem);
        connectItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent arg0) {
                SilverSurferGUI.connectBluetooth();
            }
        });

        final JMenuItem disconnectItem = new JMenuItem("Disconnect...");
        blueToothMenu.add(disconnectItem);
        disconnectItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                SilverSurferGUI.disconnectBluetooth();
            }
        });

        return blueToothMenu;
    }

    private JMenu getScreenMenu() {
        screenMenu = new JMenu("Screen");

        final JMenuItem inputPanel = new JMenuItem("Show/Hide inputpanel");
        screenMenu.add(inputPanel);
        inputPanel.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent arg0) {
                gui.toggleInputPanel();
            }
        });

        final JMenuItem infoPanel = new JMenuItem("Show/Hide infopanel");
        screenMenu.add(infoPanel);
        infoPanel.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent arg0) {
                gui.toggleInfoPanel();
            }
        });

        final JMenuItem sensorPanel = new JMenuItem("Show/Hide sensorpanel");
        screenMenu.add(sensorPanel);
        sensorPanel.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent arg0) {
                gui.toggleSensorPanel();
            }
        });

        final JMenuItem resetRobotsItem = new JMenuItem("Reset Robots");
        screenMenu.add(resetRobotsItem);
        resetRobotsItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent arg0) {
                gui.resetRobots();
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

        return speedMenu;
    }

    private JMenu getMapMenu() {
        mapMenu = new JMenu("Map");

        final JMenuItem loadMapNormalItem = new JMenuItem("Load map");
        mapMenu.add(loadMapNormalItem);
        loadMapNormalItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final FileDialog prompt = new FileDialog(GUIMenuBar.this.gui
                        .getFrame(), "Select maze:", FileDialog.LOAD);
                prompt.setDirectory("resources/maze_maps");
                prompt.setVisible(true);

                final File mapFile = new File(prompt.getDirectory()
                        + prompt.getFile()); // Load and display selection
                prompt.dispose();
                
                if(mapFile.exists()) {
                    System.out.println("[I/O] Loading map ...");
                    SilverSurferGUI.getSimulatorPanel().setMapFile(mapFile, 1, 0);
                    System.out.println("[I/O] Map loaded!");
                }
            }

        });

        final JMenuItem loadMapWithDummiesItem = new JMenuItem("Load map with 1 dummy");
        mapMenu.add(loadMapWithDummiesItem);
        loadMapWithDummiesItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final FileDialog prompt = new FileDialog(GUIMenuBar.this.gui
                        .getFrame(), "Select maze:", FileDialog.LOAD);
                prompt.setDirectory("resources/maze_maps");
                prompt.setVisible(true);

                final File mapFile = new File(prompt.getDirectory()
                        + prompt.getFile()); // Load and display selection
                prompt.dispose();
                
                if(mapFile.exists()) {
                    System.out.println("[I/O] Loading map ...");
                    SilverSurferGUI.getSimulatorPanel().setMapFile(mapFile, 2, 1);
                    System.out.println("[I/O] Map loaded!");
                }
            }

        });

        final JMenuItem loadMapWithoutDummiesItem = new JMenuItem("Load map with 3 sims");
        mapMenu.add(loadMapWithoutDummiesItem);
        loadMapWithoutDummiesItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final FileDialog prompt = new FileDialog(GUIMenuBar.this.gui
                        .getFrame(), "Select maze:", FileDialog.LOAD);
                prompt.setDirectory("resources/maze_maps");
                prompt.setVisible(true);

                final File mapFile = new File(prompt.getDirectory()
                        + prompt.getFile()); // Load and display selection
                prompt.dispose();
                
                if(mapFile.exists()) {
                    System.out.println("[I/O] Loading map ...");
                    SilverSurferGUI.getSimulatorPanel().setMapFile(mapFile, 4, 0);
                    System.out.println("[I/O] Map loaded!");
                }
            }

        });

        final JMenuItem removeMapItem = new JMenuItem("Remove map");
        mapMenu.add(removeMapItem);
        removeMapItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent arg0) {
            	SilverSurferGUI.getSimulatorPanel().removeMapFile();
            }
        });

        final JMenuItem exploreItem = new JMenuItem("Explore");
        mapMenu.add(exploreItem);
        exploreItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                SilverSurferGUI.getSimulatorPanel().startSimulation();
            }
        });

        return mapMenu;
    }
}

/*
 * private JMenu getFileMenu() {
 * 
 * fileMenu = new JMenu("File"); // fileMenu.setMnemonic('F');
 * 
 * // final JMenuItem exportLSItem = new //
 * JMenuItem("Export Lightsensor data"); // // exportLSItem.setMnemonic('L'); //
 * fileMenu.add(exportLSItem);
 * 
 * // exportLSItem.addActionListener(new ActionListener() { // // @Override //
 * public void actionPerformed(final ActionEvent arg0) { // // Prompt for a File
 * // final FileDialog prompt = new FileDialog(GUIMenuBar.this //
 * .getGui().getFrame(), "Select file:", FileDialog.SAVE); //
 * prompt.setFilenameFilter(new FilenameFilter() { // // @Override // public
 * boolean accept(final File dir, final String name) { // return
 * name.endsWith(".txt"); // } // }); // // // Display the dialog and wait for
 * the user's response // prompt.setVisible(true); // // final File export = new
 * File(prompt.getDirectory() // + prompt.getFile()); // try { // // Create file
 * // export.createNewFile(); // // // Get buffer // final StatusInfoBuffer
 * buffer = SilverSurferGUI // .getStatusInfoBuffer(); // LSInfoNode head =
 * buffer.getStartLSInfo(); // // Create data output flow // final FileWriter
 * outFile = new FileWriter(export); // final PrintWriter out = new
 * PrintWriter(outFile); // buffer.claimBuffer(); // // Print buffer // if (head
 * != null) { // do { // out.println(head.info); // head = head.next; // } while
 * (head != null); // } // // free buffer and close stream. //
 * buffer.freeBuffer(); // out.close(); // // } catch (final IOException e) { //
 * System.out // .println("Sorry, something went wrong exporting your data.");
 * // } // // } // });
 * 
 * // final JMenuItem exportUSItem = new JMenuItem( //
 * "Export Ultrasonicsensor data"); // // exportUSItem.setMnemonic('U'); //
 * fileMenu.add(exportUSItem); // // exportUSItem.addActionListener(new
 * ActionListener() { // // @Override // public void actionPerformed(final
 * ActionEvent arg0) { // // Prompt for a File // final FileDialog prompt = new
 * FileDialog(GUIMenuBar.this // .getGui().getFrame(), "Select file:",
 * FileDialog.SAVE); // prompt.setFilenameFilter(new FilenameFilter() { // //
 * 
 * @Override // public boolean accept(final File dir, final String name) { //
 * return name.endsWith(".txt"); // } // }); // // // Display the dialog and
 * wait for the user's response // prompt.setVisible(true); // // final File
 * export = new File(prompt.getDirectory() // + prompt.getFile()); // try { //
 * // Create file // export.createNewFile(); // // // Get buffer // final
 * StatusInfoBuffer buffer = SilverSurferGUI // .getStatusInfoBuffer(); //
 * USInfoNode head = buffer.getStartUSInfo(); // // Create data output flow //
 * final FileWriter outFile = new FileWriter(export); // final PrintWriter out =
 * new PrintWriter(outFile); // buffer.claimBuffer(); // // Print buffer // if
 * (head != null) { // do { // System.out.println(head.info); //
 * out.println(head.info); // head = head.next; // } while (head != null); // }
 * // // free buffer and close stream. // buffer.freeBuffer(); // out.close();
 * // // } catch (final IOException e) { // System.out //
 * .println("Sorry, something went wrong exporting your data."); // } // // } //
 * });
 * 
 * return fileMenu; }
 */