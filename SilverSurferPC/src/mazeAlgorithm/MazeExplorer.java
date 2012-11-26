package mazeAlgorithm;

import gui.SilverSurferGUI;
import java.util.Vector;
import communication.Communicator;
import mapping.Tile;

public class MazeExplorer {
	
	/**
	 * Hierin komt elke tile die bezocht is, op het einde dus alle tiles van het doolhof
	 */
	private Vector<Tile> allTiles = new Vector<Tile>();
	/**
	 * hierin worden de tiles in de wachtrij gezet
	 */
	private Vector<Tile> queu = new Vector<Tile>();
	private Tile startTile = null;
	private SilverSurferGUI gui;
	
	
	public MazeExplorer(SilverSurferGUI gui){
		this.gui = gui;
		startTile = gui.getSimulationPanel().getMapGraphConstructed().getTileWithCoordinates(gui.getInformationBuffer().getXCoordinateRelative(), gui.getInformationBuffer().getYCoordinateRelative());
	}
	
	/**
	 * Deze methode wordt opgeroepen als het object het algoritme moet uitvoeren
	 */
	public void startExploringMaze(){
		allTiles.add(startTile);
		algorithm(startTile);
	}
	
	/**
	 * Deze methode exploreert het doolhof
	 */
	private void algorithm(Tile currentTile){	
		
		//kijkt eerst of er muren zijn, deze methode zet ook al tiles waar er zowiezo liggen
		//(dus waar geen muur staat),
		//zodat deze al in de map zitten en de robot er naartoe kan gaan
		gui.getCommunicator().getSimulationPilot().checkForObstructions();
	
		//voegt buurtiles van de currentTile toe aan de queu, enkel als deze nog niet begaan
		//zijn (niet gemarkeerd) 
		for(Object neighbourTile: currentTile.getReachableNeighbours()){
			if(neighbourTile != null && !(((Tile) neighbourTile).isMarkedExploreMaze())){
				queu.add((Tile) neighbourTile);}
		}
		
		//zet het mark-veld van de currentTile op true zodat deze niet meer opnieuw in de queu
		//terecht kan komen
		currentTile.setMarkingExploreMaze(true);
		
		//returnt als er geen tiles meer in de wachtrij zitten (algoritme is afgelopen)
		if(queu.isEmpty()){
			return;
		}
		//zet de nextTile
		Tile nextTile = queu.lastElement();
		//voegt nextTile toe aan allTiles
		allTiles.add(nextTile);
		//verwijdert alle nextTiles uit de queu. De reden waarom deze meermaals in de queu 
		//voorkomen is omdat het voordeliger is om de laatste versie te pakken omdat deze
		//het dichts bij de currentTile ligt, zodat de robot niet voor elke nextTile massa's
		//omweg moet doen
		while(queu.contains(nextTile)){
		queu.remove(nextTile);}
		//voert een shortestPath uit om van currentTile naar startTile te gaan.
		ShortestPath shortestPath = new ShortestPath(gui, currentTile, nextTile, allTiles);
		shortestPath.goShortestPath();
		//voert methode opnieuw uit met nextTile
		algorithm(nextTile);
		
	}
	
}

