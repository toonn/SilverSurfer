package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import mapping.MapGraph;
import mapping.MapReader;


public class GUIMenuBar {
	
	private SilverSurferGUI gui;
	
	public GUIMenuBar(SilverSurferGUI gui){
		
	}

	
	private JMenu getMapMenu(){

		JMenu menu = new JMenu("Map");
		menu.setMnemonic('M');

		JMenuItem loadItem = new JMenuItem("Load...");
		loadItem.setMnemonic('L');
		menu.add(loadItem);

		loadItem.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent e)
					{
						//Prompt for a File
						JFileChooser prompt = new JFileChooser();
						prompt.showDialog(getGui(), "Open");
						
						File selected = prompt.getSelectedFile();
						try {
							if(selected.exists()){
								MapGraph map = MapReader.createMapFromFile(selected);
								System.out.println(map.getCurrentTile());
							}
								
						} catch (Exception e2) {
							System.out.println("There was a problem loading the map,\nplease try again.");
						}
					}
				}
		);

		return menu;
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
