package algoritmeMaze;

import gui.SilverSurferGUI;

import java.awt.List;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import mapping.Orientation;
import mapping.Tile;

public class ShortestPad {
	
	private ArrayList queu = new ArrayList();
	private Vector tilesPath = new Vector<Tile>();
	Vector<Tile> tiles = null;
	Tile startTile = null;
	Tile endTile = null;
	SilverSurferGUI gui = null;
	
	public ShortestPad(SilverSurferGUI gui, Tile startTile, Tile endTile, Vector<Tile> tiles){
		this.gui = gui;
		this.tiles = tiles;
		this.startTile = startTile;
		this.endTile = endTile;
	}

	
	private void fillTilesPath(Tile startTile){
		tilesPath.add(startTile);
		//doelTile bereikt
		if(startTile.getManhattanValue() == 0){
			deleteSuperfluousTiles();
			return;
		}

		//voeg neighbourTiles toe aan de queu
		for(Object neighbourTile: startTile.getReachableNeighbours()){
			if(neighbourTile != null ){
				queu.add((Tile) neighbourTile);}
		}
		
		//sorteer de queu: kleinste vooraan, nog niet getest
		Collections.sort(queu, new Comparator<Tile>(){
			@Override
			public int compare(Tile o1, Tile o2) {
				if(o1.getManhattanValue() < o2.getManhattanValue())
					return 1;
				else if(o1.getManhattanValue() == o2.getManhattanValue())
					return 0;
				else
					return -1;
			}		
		});
		
		Tile nextTile = (Tile) queu.get(0);
		fillTilesPath(nextTile);	
	} 
	
	private void deleteSuperfluousTiles(){
		for(int i = 1; i< tilesPath.size(); i++){
			Tile tile = (Tile) tilesPath.get(i);
		if(!tile.areNeighbours((Tile) tilesPath.get(i-1))){
			tilesPath.remove(i-1);
			i = i-2 ;
		}
	}
	}
	
	public void goShortestPath() throws IOException {
		setHeuristics();
		fillTilesPath(startTile);
		for(int i = 0; i < tilesPath.size() - 1; i++){
			int[] ar = new int[2];
			ar[0] = ((Tile) tilesPath.get(i+1)).getxCoordinate() - ((Tile) tilesPath.get(i)).getxCoordinate();
			ar[1] = ((Tile) tilesPath.get(i+1)).getyCoordinate() - ((Tile) tilesPath.get(i)).getyCoordinate();
			Orientation orientation = Orientation.getOrientationOfArray(ar);
			gui.getUnitCommunicator().goToNextTile(orientation);
		}
	}
	
	private void setHeuristics(){
			for(Tile tile: tiles){
				int xNextTile = endTile.getxCoordinate();
				int yNextTile = endTile.getyCoordinate();
				int heuristic = Math.abs(xNextTile - tile.getxCoordinate()) + Math.abs(yNextTile - tile.getyCoordinate());
				tile.setManhattanValue(heuristic);}
	}
	
	
	
	
	
	
	/*
	 * eens heel het doolhof is verkend en de barcode "finish" is gevonden wordt dit algoritme uitgevoerd.
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 */

	
	
	/*
	 * Methode die alle manhattan heuristiek berekent. Een tile meegeven of coordinatne en daaruit berekent
	 * ge de heuristieken voor alle vakjes.
	 * 
	 * Geeft een lijst terug in met alle tiles in de volgorde dat hij moet doorlopen
	 * 
	 */
	
	
	/*
	 * 
	 * barcodes disablelen! zoniet een pad vinden met de minst aantal barcodes. Hierover 
	 * moet nog eens nagedacht worden dan. Waarschijnlijk zal dit niet zo zijn want dan 
	 * wordt het algoritme te ingewikkeld.
	 * 
	 */
}
