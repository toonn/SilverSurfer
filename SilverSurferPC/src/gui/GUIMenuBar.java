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

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import org.w3c.dom.ls.LSInput;

import communication.SimulatorCommunicator;
import communication.StatusInfoBuffer;
import communication.StatusInfoBuffer.LSInfoNode;
import communication.StatusInfoBuffer.USInfoNode;
import communication.StatusInfoBuffer.TS1InfoNode;
import communication.StatusInfoBuffer.TS2InfoNode;



import mapping.MapGraph;
import mapping.MapReader;
import mapping.Orientation;


public class GUIMenuBar extends JMenuBar {

	private SilverSurferGUI gui;

	public GUIMenuBar(SilverSurferGUI gui){
		setGui(gui);
		this.add(getFileMenu());
		this.add(getBlueToothMenu());
		setBackground(new Color(221,230,231));
	}


	private JMenu getFileMenu(){

		JMenu menu = new JMenu("File");
		menu.setMnemonic('F');

		JMenuItem loadItem = new JMenuItem("Load map...");
		loadItem.setMnemonic('L');
		menu.add(loadItem);

		loadItem.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent e)
					{
						//Prompt for a File
						FileDialog prompt = new FileDialog(GUIMenuBar.this.getGui().getFrame(), "Select file:", FileDialog.LOAD);

						// Display the dialog and wait for the user's response
						prompt.show();                        

						File mapFile = new File(prompt.getDirectory() + prompt.getFile()); // Load and display selection
						prompt.dispose();                     // Get rid of the dialog box

						MapGraph map = MapReader.createMapFromFile(mapFile);
						((SimulatorCommunicator)getGui().getUnitCommunicator()).getSim().setMapGraph(map);

						System.out.println("[I/O] Map succesfully loaded!");
					}

				}

		);

		JMenuItem exportLSItem = new JMenuItem("Export Lightsensor data");
		menu.add(exportLSItem);

		exportLSItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				//Prompt for a File
				FileDialog prompt = new FileDialog(GUIMenuBar.this.getGui().getFrame(), "Select file:", FileDialog.SAVE);
				prompt.setFilenameFilter(new FilenameFilter() {

					@Override
					public boolean accept(File dir, String name) {
						return name.endsWith(".txt");
					}
				});

				// Display the dialog and wait for the user's response
				prompt.show();

				File export = new File(prompt.getDirectory()+prompt.getFile());
				try {
					// Create file
					export.createNewFile();
					
					// Get buffer
					StatusInfoBuffer buffer = getGui().getInformationBuffer();
					LSInfoNode head = buffer.getStartLSInfo();
					// Create data output flow
					FileWriter outFile = new FileWriter(export);
					PrintWriter out = new PrintWriter(outFile);
					buffer.claimBuffer();
					// Print buffer
					if (head != null){
						do{
							out.println(head.info);
							head = head.next;						
						} while (head != null);
					}
					// free buffer and close stream.
					buffer.freeBuffer();
					out.close();

				} catch (IOException e) {
					System.out.println("Sorry, something went wrong exporting your data.");
				}



			}
		});

		JMenuItem exportUSItem = new JMenuItem("Export Ultrasonicsensor data");
		menu.add(exportUSItem);

		exportUSItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				//Prompt for a File
				FileDialog prompt = new FileDialog(GUIMenuBar.this.getGui().getFrame(), "Select file:", FileDialog.SAVE);
				prompt.setFilenameFilter(new FilenameFilter() {

					@Override
					public boolean accept(File dir, String name) {
						return name.endsWith(".txt");
					}
				});

				// Display the dialog and wait for the user's response
				prompt.show();

				File export = new File(prompt.getDirectory()+prompt.getFile());
				try {
					// Create file
					export.createNewFile();
					
					// Get buffer
					StatusInfoBuffer buffer = getGui().getInformationBuffer();
					USInfoNode head = buffer.getStartUSInfo();
					// Create data output flow
					FileWriter outFile = new FileWriter(export);
					PrintWriter out = new PrintWriter(outFile);
					buffer.claimBuffer();
					// Print buffer
					if (head!= null){
						do{
							out.println(head.info);
							head = head.next;						
						} while (head != null);
					}
					// free buffer and close stream.
					buffer.freeBuffer();
					out.close();

				} catch (IOException e) {
					System.out.println("Sorry, something went wrong exporting your data.");
				}



			}
		});

		
		JMenuItem exportTS1Item = new JMenuItem("Export Touchsensor1 data");
		menu.add(exportTS1Item);

		exportTS1Item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				//Prompt for a File
				FileDialog prompt = new FileDialog(GUIMenuBar.this.getGui().getFrame(), "Select file:", FileDialog.SAVE);
				prompt.setFilenameFilter(new FilenameFilter() {

					@Override
					public boolean accept(File dir, String name) {
						return name.endsWith(".txt");
					}
				});

				// Display the dialog and wait for the user's response
				prompt.show();

				File export = new File(prompt.getDirectory()+prompt.getFile());
				try {
					// Create file
					export.createNewFile();
					
					// Get buffer
					StatusInfoBuffer buffer = getGui().getInformationBuffer();
					TS1InfoNode head = buffer.getStartTS1Info();
					// Create data output flow
					FileWriter outFile = new FileWriter(export);
					PrintWriter out = new PrintWriter(outFile);
					buffer.claimBuffer();
					// Print buffer
					if (head!= null){
						do{
							out.println(head.info);
							head = head.next;						
						} while (head != null);
					}
					// free buffer and close stream.
					buffer.freeBuffer();
					out.close();

				} catch (IOException e) {
					System.out.println("Sorry, something went wrong exporting your data.");
				}



			}
		});

		JMenuItem exportTS2Item = new JMenuItem("Export Touchsensor2 data");
		menu.add(exportTS2Item);

		exportTS2Item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				//Prompt for a File
				FileDialog prompt = new FileDialog(GUIMenuBar.this.getGui().getFrame(), "Select file:", FileDialog.SAVE);
				prompt.setFilenameFilter(new FilenameFilter() {

					@Override
					public boolean accept(File dir, String name) {
						return name.endsWith(".txt");
					}
				});

				// Display the dialog and wait for the user's response
				prompt.show();
				System.out.println(prompt.getDirectory()+prompt.getFile());
				File export = new File(prompt.getDirectory()+prompt.getFile());
				try {
					// Create file
					export.createNewFile();
					
					// Get buffer
					StatusInfoBuffer buffer = getGui().getInformationBuffer();
					TS2InfoNode head = buffer.getStartTS2Info();
					// Create data output flow
					FileWriter outFile = new FileWriter(export);
					PrintWriter out = new PrintWriter(outFile);
					buffer.claimBuffer();
					// Print buffer
					if (head!= null){
						do{
							out.println(head.info);
							head = head.next;						
						} while (head != null);
					}
					// free buffer and close stream.
					buffer.freeBuffer();
					out.close();

				} catch (IOException e) {
					System.out.println("Sorry, something went wrong exporting your data.");
				}



			}
		});

		return menu;
	}

	private JMenu getBlueToothMenu(){

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
				getGui().connectBluetooth();

			}
		});

		disconnectItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				getGui().disconnectBluetooth();

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
