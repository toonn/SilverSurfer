/**
 * een object van deze klasse kan maar 1x gebruikt worden!
 * velden worden ingevuld bij aanmaak object en kunnen niet meer veranderen.
 */

package mazeAlgorithm;

import gui.SilverSurferGUI;

import java.io.File;
import java.io.IOException;
import java.util.*;
import mapping.*;

public class ShortestPath {
	
	/**
	 * hierin worden de tiles in de wachtrij gezet
	 */
	private ArrayList queu = new ArrayList();
	/**
	 * hierin worden de tiles opgeslagen die het uiteindelijk pad vormen van startdoel tot einddoel,
	 * inclusief deze laatste 2
	 */
	private Vector tilesPath = new Vector<Tile>();
	/**
	 * zijn alle tiles die meegegeven worden wanneer het algoritme opgeroepen wordt.
	 * dus de tiles ter beschikking om van start- naar einddoel te gaan
	 */
	Vector<Tile> tiles = null;
	Tile startTile = null;
	Tile endTile = null;
	SilverSurferGUI gui = null;
	
	public ShortestPath(SilverSurferGUI gui, Tile startTile, Tile endTile, Vector<Tile> tiles){
		this.gui = gui;
		this.tiles = tiles;
		this.startTile = startTile;
		this.endTile = endTile;
		for(Tile tile: tiles){
			tile.setMarkingShortestPath(false);
		}
	}
	
	/**
	 * De methode die moet opgeroepen worden en alle methodes in de juiste volgorde uitvoert.
	 * eerst worden de heuristieken gezet
	 * dan fillTilesPath, en aan de hand hiervan wordt naar de robot/simulator het commando gestuurd
	 * om deze tiles te "bewandelen".
	 * Op het einde wordt de kost van alle tiles terug op hun initiele waarde gezet.
	 */
	public void goShortestPath() {
		setHeuristics();
		startTile.setCost(0);
		fillTilesPath(startTile);
		if(tilesPath.size()==1){
			return;
		}
		for(int i = 0; i < tilesPath.size() - 1; i++){
			int[] ar = new int[2];
			ar[0] = ((Tile) tilesPath.get(i+1)).getxCoordinate() - ((Tile) tilesPath.get(i)).getxCoordinate();
			ar[1] = ((Tile) tilesPath.get(i+1)).getyCoordinate() - ((Tile) tilesPath.get(i)).getyCoordinate();
			Orientation orientation = Orientation.getOrientationOfArray(ar);
			try {
				gui.getCommunicator().goToNextTile(orientation);
			} catch (IOException e) {
				System.err.println("exception in shortestpad gui.getunitcommunicator.goTonextTile");
				e.printStackTrace();
			}
		}
		
		for(Object tile: getTiles()){
			((Tile) tile).setCostBackToInitiatedValue();
		}
		
	}
	
	private Vector getTilesPath() {
		return tilesPath;
	}

	private Vector<Tile> getTiles() {
		return tiles;
	}
	
	/**
	 * zet de heuristiek op elke tile afhankelijk van de endTile die heuristiekwaarde 0 krijgt.
	 */
	private void setHeuristics(){
			for(Tile tile: tiles){
				int xNextTile = endTile.getxCoordinate();
				int yNextTile = endTile.getyCoordinate();
				int heuristic = Math.abs(xNextTile - tile.getxCoordinate()) + Math.abs(yNextTile - tile.getyCoordinate());
				tile.setManhattanValue(heuristic);}
	}

	/**
	 * In deze methode wordt tilesPath gevuld.
	 */
	private void fillTilesPath(Tile currentTile){
		tilesPath.add(currentTile);
		
		if(currentTile.getManhattanValue() == 0){
			//endTile bereikt
			deleteSuperfluousTiles();
			return;
		}

		//voeg neighbourTiles van de currentTile toe aan de queu
		for(Object neighbourTile: currentTile.getReachableNeighbours()){
			if(neighbourTile != null && tiles.contains(neighbourTile) && !((Tile) neighbourTile).isMarkedShortestPath()){
				((Tile) neighbourTile).setCost(currentTile.getCost() + 1);
				queu.add((Tile) neighbourTile);}
		}
		
		//sorteer de queu: kleinste vooraan, nog niet getest
		Collections.sort(queu, new Comparator<Tile>(){
			@Override
			public int compare(Tile o1, Tile o2) {
				if(o1.getManhattanValue() + o1.getCost() < o2.getManhattanValue() + o2.getCost())
					return 1;
				else if(o1.getManhattanValue() + o1.getCost() == o2.getManhattanValue() + o2.getCost())
					return 0;
				else
					return -1;
			}		
		});
		
		currentTile.setMarkingShortestPath(true);
		
		Tile nextTile = (Tile) queu.get(queu.size()-1);
		while(queu.contains(nextTile)){
		queu.remove(nextTile);}
		fillTilesPath(nextTile);	
	} 
	
	/**
	 * wordt opgeroepen als einddoel bereikt is in fillTilesPath.
	 * tilesPath bevat nu alle tiles die afgegaan zijn dus diegene die niet naar het doel leiden
	 * moeten nog verwijderd worden.
	 * dit gebeurt als volgt:
	 * ge begint vanaf uw voorlaatste tile en checkt of deze buren is met de volgende EN een kost heeft
	 * 1 minder als de kost van de volgende, indien dit niet zo is , wordt deze tile verwijderd en
	 * checkt men de tile ervoor, enz...
	 */	
	private void deleteSuperfluousTiles(){
		for(int i = tilesPath.size()-2; i!= 0 ; i--){
			if((((Tile) tilesPath.get(i)).getCost() != ((Tile) tilesPath.get(i+1)).getCost() - 1) ||
					!((Tile) tilesPath.get(i)).areNeighbours((Tile) tilesPath.get(i+1))){
				tilesPath.remove(i);
			}
		} 
	}

	
	public static void main(String[] args) {
		MapGraph map = (new MapReader()).createMapFromFile(new File("resources/maze_maps/Semester1Demo2.txt"), 2, 2);
		Vector vec = new Vector<Tile>();
		for(Tile tile: map.getTiles()){
			vec.add(tile);
		}
		ShortestPath shor = new ShortestPath(new SilverSurferGUI(), map.getTileWithCoordinates(2, 4), map.getTileWithCoordinates(2, 2), vec);
		shor.setHeuristics();
		shor.fillTilesPath(map.getTileWithCoordinates(2, 4));
		System.out.println("sysout");
		for(Object tile:shor.getTilesPath()){
			System.out.println(((Tile) tile).getxCoordinate() + "" +  ((Tile) tile).getyCoordinate());
		}
	}
}
