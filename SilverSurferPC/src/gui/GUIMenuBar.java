package gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.swing.*;

import communication.*;
import communication.StatusInfoBuffer.*;

public class GUIMenuBar extends JMenuBar {

    private SilverSurferGUI gui;

    public GUIMenuBar(SilverSurferGUI gui) {
        setGui(gui);
        this.add(getFileMenu());
        this.add(getBlueToothMenu());
        setBackground(new Color(221, 230, 231));
    }

    private JMenu getFileMenu() {

        JMenu menu = new JMenu("File");
        menu.setMnemonic('F');

        JMenuItem loadMapItem = new JMenuItem("Load map...");
        loadMapItem.setMnemonic('M');
        menu.add(loadMapItem);

        loadMapItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Prompt for a File
                FileDialog prompt = new FileDialog(GUIMenuBar.this.getGui()
                        .getFrame(), "Select file:", FileDialog.LOAD);

                // Display the dialog and wait for the user's response
                prompt.show();

                File mapFile = new File(prompt.getDirectory()
                        + prompt.getFile()); // Load and display selection
                prompt.dispose(); // Get rid of the dialog box

                JPanel messagePanel = new JPanel();
                SpinnerNumberModel xModel = new SpinnerNumberModel(1, 0, 1000,
                        1);
                SpinnerNumberModel yModel = new SpinnerNumberModel(1, 0, 1000,
                        1);
                JLabel x = new JLabel("x:");
                JLabel y = new JLabel("y:");

                JSpinner xcoord = new JSpinner(xModel);
                JSpinner ycoord = new JSpinner(yModel);
                messagePanel.add(x);
                messagePanel.add(xcoord);
                messagePanel.add(y);
                messagePanel.add(ycoord);

                // Populate your panel components here.
                JOptionPane.showMessageDialog(getGui().getFrame(),
                        messagePanel, "Enter relative starting coordinates:",
                        JOptionPane.OK_OPTION);

                int xCo = Integer.valueOf(xcoord.getValue().toString());
                int yCo = Integer.valueOf(ycoord.getValue().toString());

                gui.getCommunicator().getSimulationPilot().setMapFile(mapFile, xCo, yCo);

                System.out.println("[I/O] Map succesfully loaded!");
            }

        });

        JMenuItem deleteMapItem = new JMenuItem("Delete map");
        deleteMapItem.setMnemonic('D');
        menu.add(deleteMapItem);

        deleteMapItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                gui.getCommunicator().getSimulationPilot().setMapGraph(null);

                JPanel messagePanel = new JPanel();
                JLabel x = new JLabel("Map succesfully deleted!");
                messagePanel.add(x);

                // Populate your panel components here.
                JOptionPane.showMessageDialog(getGui().getFrame(),
                        messagePanel, "Map deleted:", JOptionPane.OK_OPTION);

                System.out.println("[I/O] Map succesfully deleted!");
            }

        });

        JMenuItem exportLSItem = new JMenuItem("Export Lightsensor data");
        exportLSItem.setMnemonic('L');
        menu.add(exportLSItem);

        exportLSItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                // TODO Auto-generated method stub
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
                            .getInformationBuffer();
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
        exportUSItem.setMnemonic('U');
        menu.add(exportUSItem);

        exportUSItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                // TODO Auto-generated method stub
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
                            .getInformationBuffer();
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

        JMenuItem exportTS1Item = new JMenuItem("Export Touchsensor1 data");
        menu.add(exportTS1Item);
        exportTS1Item.setMnemonic('1');

        exportTS1Item.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                // TODO Auto-generated method stub
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
                            .getInformationBuffer();
                    TS1InfoNode head = buffer.getStartTS1Info();
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

        JMenuItem exportTS2Item = new JMenuItem("Export Touchsensor2 data");
        menu.add(exportTS2Item);
        exportTS2Item.setMnemonic('2');

        exportTS2Item.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                // TODO Auto-generated method stub
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
                System.out.println(prompt.getDirectory() + prompt.getFile());
                File export = new File(prompt.getDirectory() + prompt.getFile());
                try {
                    // Create file
                    export.createNewFile();

                    // Get buffer
                    StatusInfoBuffer buffer = SilverSurferGUI
                            .getInformationBuffer();
                    TS2InfoNode head = buffer.getStartTS2Info();
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

        return menu;
    }

    private JMenu getBlueToothMenu() {

        JMenu bluetoothMenu = new JMenu("Bluetooth");
        bluetoothMenu.setMnemonic('B');

        JMenuItem connectItem = new JMenuItem("Connect...");
        connectItem.setMnemonic('C');
        bluetoothMenu.add(connectItem);

        JMenuItem disconnectItem = new JMenuItem("Disconnect...");
        disconnectItem.setMnemonic('D');
        bluetoothMenu.add(disconnectItem);

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

        return bluetoothMenu;

    }

    /**
     * Set the SilverSurferGUI this menubar should operate on.
     */
    public void setGui(SilverSurferGUI gui) {
        this.gui = gui;
    }

    /**
     * get the SilverSurferGUI this menubar is operating on.
     */
    public SilverSurferGUI getGui() {
        return gui;
    }

}