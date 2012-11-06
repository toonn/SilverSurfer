package gui;

import java.awt.Color;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import communication.SimulatorCommunicator;

import mapping.MapGraph;
import mapping.MapReader;
import mapping.Orientation;


public class GUIMenuBar extends JMenuBar {

	private SilverSurferGUI gui;

	public GUIMenuBar(SilverSurferGUI gui){
		setGui(gui);
		this.add(getMapMenu());
		this.add(getBlueToothMenu());
		setBackground(new Color(221,230,231));
	}


	private JMenu getMapMenu(){

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
