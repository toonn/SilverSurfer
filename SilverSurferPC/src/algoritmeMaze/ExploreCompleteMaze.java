package algoritmeMaze;

import gui.SilverSurferGUI;

import java.awt.Shape;
import java.io.IOException;
import java.util.Vector;

import commands.Command;

import mapping.MapGraph;
import mapping.Orientation;
import mapping.Tile;

public class ExploreCompleteMaze {
	
	private Vector<Tile> allTiles = new Vector<Tile>();
	private Vector<Tile> queu = new Vector<Tile>();
	private SilverSurferGUI gui;
	
	public ExploreCompleteMaze(SilverSurferGUI gui){
		this.gui = gui;
	}
	
	public void algorithm(Tile startTile) throws IOException{
		
		allTiles.add(startTile);
		
		for(Object neighbourTile: startTile.getReachableNeighbours()){
			if(neighbourTile != null && !(((Tile) neighbourTile).isMarked())){
				queu.add((Tile) neighbourTile);}
		}
		startTile.setMarking(true);
		if(queu.isEmpty()){
			return;
		}
		Tile nextTile = queu.lastElement();
		ShortestPad shortestPath = new ShortestPad(gui, startTile, nextTile, allTiles);
		shortestPath.goShortestPath();
		algorithm(nextTile);
		
	}
	
}
 


/**
 * Hallo dit is the pseudocode van de completness van de maze:
 * 
 * Vakje houdt bij of het niet, wel gemarkeerd (boolean) is en de coordinaten.
 * Gebruik van Tile in maping en Enumeratie voor marking (noemt Marking in package mapping).
 * 
 * 
 * robot start:
 * 			checked de kinderen (muren), die hou je bij in een lijst.
 * 			en check ook of ze gemarkeerd zijn. 
 * 			ga naar het laatste kind in de lijst
 *         	en markeer je de tegel.
 * 			terug
 * als de lijst leeg is, is het algoritme op zen einde. Nu komt het algoritme naar het korste pad
 * naar de barcode met waarde "finish"
 * 
 * en ook nog checken of de barcode "finish" gevonden is. Stel dat die barcode nog niet gevonden is 
 * alles wissen. (marking op false en de lijst wissen met kinderen en de tiles in zen geheugen)
 *  
 *  bij resultaat een geluidje geven om te zeggen dat het aan het kortste pad algoritme begint.
 */

